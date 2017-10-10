#!/usr/bin/env python3

import argparse
import select
import sys
from multiprocessing import Process

import os

import errno

from common import *

inputFilesPath = "input_files/"

processingTasks = {}
requestNumber = 0


def split_by_files(file: BufferedIOBase, file_size: int, ptc, request_id):
	ws_number = len(processingTasks[ptc])
	approx_chars_per_part = int(file_size / ws_number)

	file_names = []

	last_pos = 0
	i = 1
	while last_pos < file_size:
		part_filename = inputFilesPath + str(request_id).rjust(5, '0') + str(i).rjust(3, '0') + ".txt"

		if i != ws_number:
			file.seek(i * approx_chars_per_part)
		else:
			file.seek(file_size)
		if i < ws_number:
			while True:
				b = file.read(1)
				if b.isspace():
					break
				file.seek(-2, os.SEEK_CUR)
		new_pos = file.tell()
		file.seek(last_pos)

		file_part = open(part_filename, 'wb')

		pos = last_pos
		while pos < new_pos:
			data = file.read(min(buffer_size, new_pos-pos))
			bytes_written = file_part.write(data)
			pos += bytes_written
		file_part.close()
		last_pos = pos
		i += 1

		file_names.append(part_filename)

	return file_names


def assembleBack(replies, ptc, result_filename='', input_filenames=None):
	if ptc == 'WCT':
		return sum(map(int, replies.values())), 'R'
	elif ptc == 'FLW':
		greatestWord = ''
		wordLen = 0
		for word in replies.values():
			temp = len(word)
			if temp > wordLen:
				greatestWord = word
				wordLen = temp
		return greatestWord, 'R'
	elif ptc == 'UPP' or ptc == 'LOW':
		file = open(result_filename, 'wb')

		for part_filename in sorted(input_filenames):
			part_file = open(part_filename, 'rb')
			part_file_size = os.path.getsize(part_filename)
			while part_file.tell() < part_file_size:
				bytes = part_file.read(buffer_size)
				file.write(bytes)

		return "", "F"

	return "", "R"


def action_lst(sock: socket, bufferedReader: BufferedReader):
	try:
		assertEndOfMessage(bufferedReader)

		if len(processingTasks) == 0:
			print('Sending list of PTCs (empty)')
			sendMsg(sock, 'FPT', 'EOF')
		else:
			print('Sending list of PTCs')
			print(list(processingTasks))
			sendMsg(sock, 'FPT', len(processingTasks), list(processingTasks))

	except ProtocolError:
		print('Incorrectly formulated LST request. Replying ERR')
		sendMsg(sock, 'FPT', 'ERR')


def action_req(sock: socket, bufferedReader: BufferedReader, request_number: int):
	try:
		filename = inputFilesPath + str(request_number).rjust(5, '0') + '.txt'

		ptc = readWord(bufferedReader)
		size = readNumber(bufferedReader)

		file = open(filename, 'w+b')
		readSocketToFile(bufferedReader, file, size)
		assertEndOfMessage(bufferedReader)

		if ptc not in processingTasks.keys():
			print(ptc, "PTC not currently supported.")
			sendMsg(sock, "REP", "EOF")
			return

		file.seek(0)

		filenames = split_by_files(file, size, ptc, requestNumber)

		socketList = []
		sock_filenames_dict = {}
		for address, part_filename in zip(processingTasks[ptc], filenames):
			file = open(part_filename, 'rb')
			size = os.path.getsize(part_filename)

			wsSock = prepare_tcp_client(address)

			if not wsSock:
				print("WS not responding!")
				processingTasks[ptc].remove(address)
				for alternativeAddress in processingTasks[ptc]:
					wsSock = prepare_tcp_client(alternativeAddress)
					if wsSock:
						break
				if not wsSock:
					print("No WSs available")

			sock_filenames_dict[wsSock] = part_filename
			sendMsg(wsSock, 'WRQ', ptc, part_filename, size, tail=' ')
			readFileToSocket(file, wsSock, size)
			wsSock.sendall(b'\n')
			socketList += [wsSock]

		out_filenames = []
		replies = {}
		while len(socketList) != 0:
			readable, _, _ = select.select(socketList, [], [])

			for wsSock in readable:
				socketList.remove(wsSock)

				bufferedReader = wsSock.makefile('rb', buffering=buffer_size)
				protocol_message = readWord(bufferedReader)
				protocolAssert(protocol_message == 'REP')
				reply_type = readWord(bufferedReader)
				protocolAssert(reply_type == 'R' or reply_type == 'F')
				size = readNumber(bufferedReader)

				if reply_type == 'R':
					replies[sock_filenames_dict[wsSock]] = bufferedReader.read(size).decode('ascii')
					assertEndOfMessage(bufferedReader)

				else:
					out_filename = sock_filenames_dict[wsSock] + '.out'
					out_filenames.append(out_filename)
					out_file = open(out_filename, 'wb')

					readSocketToFile(bufferedReader, out_file, size)
					assertEndOfMessage(bufferedReader)

					out_file.close()
				bufferedReader.close()
				wsSock.close()

		out_filename = filename + '.out'

		finalReply, finalReplyType = assembleBack(replies, ptc, out_filename, out_filenames)

		if finalReplyType == 'R':
			replySize = len(str(finalReply).encode('ascii'))
			sendMsg(sock, 'REP', finalReplyType, replySize, finalReply)
		elif finalReplyType == 'F':
			out_file = open(out_filename, 'rb')

			size = os.path.getsize(out_filename)
			sendMsg(sock, 'REP', finalReplyType, size, tail=' ')
			readFileToSocket(out_file, sock, size)
			sock.sendall(b'\n')

	except ProtocolError:
		print('Incorrectly formulated REQ request. Replying ERR')
		sendMsg(sock, 'REP', 'ERR')


def userConnection(sock: socket.SocketType, requestNumber: int):
	tcpSock.close()
	udpSock.close()
	try:
		clientAddress = sock.getpeername()
		bufferedReader = sock.makefile('rb', buffering=buffer_size)

		select.select([bufferedReader], [], [])
		try:
			if bufferedReader.peek() == b'':
				raise EOFError()

			msgType = readWord(bufferedReader)
			print("[" + msgType + "] request from", clientAddress)

			if msgType == 'LST':
				action_lst(sock, bufferedReader)

			elif msgType == 'REQ':
				action_req(sock, bufferedReader, requestNumber)

			else:
				print("Unknown message received.")
				sendMsg(sock, 'ERR')

		except EOFError:
			print("End of stream. Client", clientAddress, "lost.")

	except KeyboardInterrupt:
		print("\nTCP connection", clientAddress, "exiting")
	finally:
		tryClose(sock)
		sys.exit()


def tcpAccept():
	global requestNumber
	try:
		newSock, address = tcpSock.accept()
		print("Connection from client", address, "accepted, forking...")

		Process(target=userConnection, args=(newSock, requestNumber), daemon=True).start()
		requestNumber += 1

		newSock.close()
	except OSError:
		pass


def udpReceive():
	try:
		data, address = udpSock.recvfrom(buffer_size)
		msg = parseData(data)

		if msg[0] == 'REG':
			try:
				protocolAssert(len(msg) > 3)

				wsAddress = msg[-2]
				wsPort = int(msg[-1])
				wsHost = (wsAddress, wsPort)

				print("Registration request received with address", wsHost, end='... ')

				for i in range(1, len(msg) - 2):
					protocolAssert(msg[i] != '')

					ptcHosts = processingTasks.setdefault(msg[i], [])
					if wsHost not in ptcHosts:
						ptcHosts.append(wsHost)

				msg, encodedMsg = constructMessage('RAK', 'OK')
				print("OK")
				udpSock.sendto(encodedMsg, address)

			except ProtocolError:
				msg, encodedMsg = constructMessage('RAK', 'ERR')
				print("ERR")
				udpSock.sendto(encodedMsg, address)

		elif msg[0] == 'UNR':
			try:
				protocolAssert(len(msg) == 3)

				wsAddress = msg[1]
				wsPort = int(msg[2])
				wsHost = (wsAddress, wsPort)

				for ptc in list(processingTasks.keys()):
					if wsHost in processingTasks[ptc]:
						processingTasks[ptc].remove(wsHost)

					if len(processingTasks[ptc]) == 0:
						del processingTasks[ptc]

				msg, encodedMsg = constructMessage('UAK', 'OK')
				print("Replying via UDP with", msg[1])
				udpSock.sendto(encodedMsg, address)

			except ProtocolError:
				msg, encodedMsg = constructMessage('UAK', 'ERR')
				print("Replying via UDP with", msg[1])
				udpSock.sendto(encodedMsg, address)

		else:
			print("Unknown UDP message received")
			msg, encodedMsg = constructMessage('ERR')
			udpSock.sendto(encodedMsg, address)

	except OSError:
		print("Error while receiving UDP message. Ignoring...")
		pass


if __name__ == '__main__':
	tcpSock = None
	udpSock = None

	try:
		parser = argparse.ArgumentParser()
		parser.add_argument('-p', type=int, metavar="CSport", default=defaultPort)
		args = parser.parse_args()

		hostname = ''  # meaning all available interfaces
		port = args.p

		print("Starting central server on " + hostname)

		try:
			os.makedirs(inputFilesPath)
		except OSError as e:
			if e.errno != errno.EEXIST:  # else, directory already exists. Continue
				raise

		udpSock = prepare_udp_server((hostname, port))
		tcpSock = prepare_tcp_server((hostname, port), 5)

		print("Waiting for connections...")
		while True:
			readable, _, _ = select.select([udpSock, tcpSock], [], [])

			for sock in readable:
				if sock == tcpSock:
					tcpAccept()
				elif sock == udpSock:
					udpReceive()

	except KeyboardInterrupt:
		print("\nCleaning up...")
		tcpSock.close()
		udpSock.close()
		print("Exiting")
		exit()
