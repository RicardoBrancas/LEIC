#!/usr/bin/python3

import argparse
import socket
import select
import sys
from multiprocessing import Process

import os

import errno

from common import *

inputFilesPath = "input_files/"

tcpSock = None
udpSock = None

processingTasks = {}
requestNumber = 0

port = None
hostname = ""  # meaning all available interfaces


def cleanup():
	print("\nCleaning up...")
	tryClose(tcpSock)
	tryClose(udpSock)
	print("Exiting")
	exit()


def split(data: str, ptc):
	numberOfWSs = len(processingTasks[ptc])
	approximateCharsPerPart = int(len(data) / numberOfWSs)

	result = {}
	i = 0
	last = 0

	for i in range(numberOfWSs - 1):
		nearest_space = data.rfind(' ', last, (i + 1) * approximateCharsPerPart)
		result[str(i).rjust(3, '0')] = data[last:nearest_space]
		last = nearest_space + 1

	i += 1
	result[str(i).rjust(3, '0')] = data[last:]

	return result


def assembleBack(replies, ptc):
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
		return ' '.join(sorted(replies.keys)), 'F'

	return "", "R"


def userConnection(sock: socket.SocketType, requestNumber: int):
	tcpSock.close()
	udpSock.close()
	try:
		clientAddress = sock.getpeername()
		bufferedReader = sock.makefile('rb', buffering=1024)

		select.select([bufferedReader], [], [])
		try:
			if bufferedReader.peek() == b'':
				raise EOFError()

			msgType = readWord(bufferedReader)
			print("[" + msgType + "] request from", clientAddress)

			if msgType == 'LST':
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

			elif msgType == 'REQ':
				try:
					ptc = readWord(bufferedReader)
					size = readNumber(bufferedReader)
					data = bufferedReader.read(size)
					assertEndOfMessage(bufferedReader)

					f = open(inputFilesPath + str(requestNumber).rjust(5, '0') + ".txt", 'wb')
					f.write(data)
					f.close()

					splitData = split(data.decode('ascii'), ptc)

					socketList = []
					partNumbers = {}
					for address, dataPart in zip(processingTasks[ptc], splitData.items()):
						partNumber = dataPart[0]
						data = dataPart[1]
						dataSize = len(data)
						filename = str(requestNumber).rjust(5, '0') + partNumber + ".txt"

						wsSock = prepare_tcp_client(address)
						partNumbers[wsSock] = partNumber
						sendMsg(wsSock, 'WRQ', ptc, filename, dataSize, data)
						socketList += [wsSock]

					replies = {}
					while len(socketList) != 0:
						readable, _, _ = select.select(socketList, [], [])

						for wsSock in readable:
							socketList.remove(wsSock)

							bufferedReader = wsSock.makefile('rb', buffering=1024)
							msgType = readWord(bufferedReader)
							protocolAssert(msgType == 'REP')
							replyType = readWord(bufferedReader)
							size = readNumber(bufferedReader)
							replies[partNumbers[wsSock]] = bufferedReader.read(size).decode('ascii')

							bufferedReader.close()
							wsSock.close()

					finalReply, finalReplyType = assembleBack(replies, ptc)
					replySize = len(finalReply.encode('ascii'))
					sendMsg(sock, 'REP', finalReplyType, replySize, finalReply)

				except ProtocolError:
					print('Incorrectly formulated REQ request. Replying ERR')
					sendMsg(sock, 'REP', 'ERR')
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


def prepareTcp():
	global tcpSock

	print("Starting TCP server")
	try:
		tcpSock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		tcpSock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)  # ignore system TIME_WAIT
	except OSError:
		print("Exception while creating TCP socket!")
		exit()

	try:
		host = (hostname, port)
		tcpSock.bind(host)
	except OSError:
		print("Error during TCP bind. Maybe the port is in use already?")
		exit()

	tcpSock.listen(5)


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


def prepareUdp():
	global udpSock

	print("Starting UDP server")
	try:
		udpSock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
		udpSock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)  # ignore system TIME_WAIT
	except OSError:
		print("Exception while creating UDP socket!")
		exit()

	try:
		host = (hostname, port)
		udpSock.bind(host)
	except OSError:
		print("Error during UDP bind. Maybe the port is in use already?")
		exit()


def udpReceive():
	try:
		data, address = udpSock.recvfrom(4096)
		msg = parseData(data)
		print("[" + msg[0] + "] received via UDP from", address)

		if msg[0] == 'REG':
			try:
				assert len(msg) > 3

				wsAddress = msg[-2]
				wsPort = int(msg[-1])

				for i in range(1, len(msg) - 2):
					assert msg[i] != ""

					if not msg[i] in processingTasks:
						processingTasks[msg[i]] = []
					if not (wsAddress, wsPort) in processingTasks[msg[i]]:
						processingTasks[msg[i]] += [(wsAddress, wsPort)]

				msg, encodedMsg = constructMessage('RAK', 'OK')
			except Exception:
				msg, encodedMsg = constructMessage('RAK', 'ERR')
			finally:
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
	try:
		parser = argparse.ArgumentParser()
		parser.add_argument('-p', metavar="CSport", default=defaultPort)
		args = parser.parse_args()

		port = args.p

		print("Starting central server on " + socket.gethostname())

		try:
			os.makedirs(inputFilesPath)
		except OSError as e:
			if e.errno != errno.EEXIST:  # else, directory already exists. Continue
				raise

		prepareUdp()
		prepareTcp()

		print("Waiting for connections...")
		while True:
			readable, _, _ = select.select([udpSock, tcpSock], [], [])

			for sock in readable:
				if sock == tcpSock:
					tcpAccept()
				elif sock == udpSock:
					udpReceive()

	except KeyboardInterrupt:
		cleanup()
