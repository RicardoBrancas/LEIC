#!/usr/bin/env python3

import argparse
import select
import sys
from multiprocessing import Process
import os
import errno

from common import *

processingTasks = {}
request_number = 0


def split_by_files(request_id, file: BufferedIOBase, file_size: int, ptc):
	"""
		Splits a file in several parts. As many as there are working servers for that specific task.

	:param file: BufferedReader that wraps a file descriptor for the file received.
	:param file_size: File size in bytes
	:param ptc: PTC requested by user
	:param request_id:
	:return: an ordered list of filenames
	"""
	ws_number = len(processingTasks[ptc])
	approx_chars_per_part = int(file_size / ws_number)

	print("Splitting request #" + str(request_id), "in", ws_number, "parts. Approximately", approx_chars_per_part,
		  "chars per part.")

	file_names = []

	last_pos = 0
	i = 1
	while last_pos < file_size:
		part_filename = make_filename(request_id, i)

		if i < ws_number:  # if this is not the last part
			file.seek(i * approx_chars_per_part)

			while True:  # now go back until we find a space
				b = file.read(1)
				if b.isspace():
					break
				file.seek(-2, os.SEEK_CUR)

		else:  # go to end of file
			file.seek(file_size)

		new_pos = file.tell()
		file.seek(last_pos)

		file_part = open(part_filename, 'wb')

		pos = last_pos
		while pos < new_pos:
			data = file.read(min(buffer_size, new_pos - pos))
			bytes_written = file_part.write(data)
			pos += bytes_written

		file_part.close()
		last_pos = pos
		i += 1

		file_names.append(part_filename)

	return file_names


def assemble_back(replies, input_filenames, ptc, result_filename=''):
	"""
		Assembles back a set of replies or files. Into a single response. Generally only one of (replies|input_filenames) is used.

	:param replies: A dictionary of pairs (index, reply)
	:param input_filenames: A list of filenames where each element is a filename where a reply was saved
	:param ptc:
	:param result_filename: where we should store the result

	:return: Returns a tuple where the first element is the reply type and the second is the reply. The second element is only used in replies of type R
	"""
	if ptc == 'WCT':
		return 'R', sum(map(int, replies.values()))

	elif ptc == 'FLW':
		greatestWord = ''
		wordLen = 0
		for word in replies.values():
			temp = len(word)
			if temp > wordLen:
				greatestWord = word
				wordLen = temp
		return 'R', greatestWord

	elif ptc == 'UPP' or ptc == 'LOW':
		file = open(result_filename, 'wb')

		for part_filename in sorted(input_filenames):  # concat files
			part_file = open(part_filename, 'rb')
			part_file_size = os.path.getsize(part_filename)
			while part_file.tell() < part_file_size:
				bytes = part_file.read(buffer_size)
				file.write(bytes)
		return 'F', ""

	return None


def do_lst(sock: socket, bufferedReader: BufferedReader):
	try:
		assert_end_of_message(bufferedReader)

		if len(processingTasks) == 0:
			print('Sending list of PTCs (empty)')
			send_msg(sock, 'FPT', 'EOF')
		else:
			print('Sending list of PTCs')
			print(list(processingTasks))
			send_msg(sock, 'FPT', len(processingTasks), list(processingTasks))

	except ProtocolError:
		print('Incorrectly formulated LST request. Replying ERR')
		send_msg(sock, 'FPT', 'ERR')


def do_req(sock: socket, bufferedReader: BufferedReader, request_number: int):
	try:
		filename = make_filename(request_number)

		ptc = read_word(bufferedReader)
		size = read_number(bufferedReader)

		file = open(filename, 'w+b')
		read_socket_to_file(bufferedReader, file, size)
		assert_end_of_message(bufferedReader)

		if ptc not in processingTasks.keys():
			print(ptc, "PTC not currently supported.")
			send_msg(sock, "REP", "EOF")
			return

		file.seek(0)

		filenames = split_by_files(request_number, file, size, ptc)

		socketList = []
		index_by_sock = {}
		for index, (address, part_filename) in enumerate(zip(processingTasks[ptc], filenames), start=1):
			part_file = open(part_filename, 'rb')
			size = os.path.getsize(part_filename)

			wsSock = prepare_tcp_client(address)

			if not wsSock:  # if WS went offline without unregistering try to send this part to some other WS
				print("WS not responding!")
				processingTasks[ptc].remove(address)
				for alternativeAddress in processingTasks[ptc]:
					wsSock = prepare_tcp_client(alternativeAddress)
					if wsSock:
						print("Alternative WS found.")
						break
				if not wsSock:
					print("No WSs available")

			index_by_sock[wsSock] = index  # used later to known which parts come from which WS
			send_msg(wsSock, 'WRQ', ptc, part_filename, size, tail=' ')
			read_file_to_socket(part_file, wsSock, size)
			wsSock.sendall(b'\n')
			socketList += [wsSock]

		out_filenames = []
		replies = {}
		while len(socketList) != 0:
			readable, _, _ = select.select(socketList, [], [])

			for wsSock in readable:
				socketList.remove(wsSock)

				bufferedReader = wsSock.makefile('rb', buffering=buffer_size)
				protocol_message = read_word(bufferedReader)
				protocol_assert(protocol_message == 'REP')
				reply_type = read_word(bufferedReader)
				protocol_assert(reply_type == 'R' or reply_type == 'F')
				size = read_number(bufferedReader)

				if reply_type == 'R':
					replies[index_by_sock[wsSock]] = bufferedReader.read(size).decode('ascii')
					assert_end_of_message(bufferedReader)

				else:
					out_filename = make_filename(request_number, index_by_sock[wsSock], ext='.out')
					out_file = open(out_filename, 'wb')

					read_socket_to_file(bufferedReader, out_file, size)
					assert_end_of_message(bufferedReader)

					out_filenames.append(out_filename)
					out_file.close()

				bufferedReader.close()
				try_close(wsSock)

		out_filename = make_filename(request_number, folder=output_files_path)

		final_reply_type, final_reply = assemble_back(replies, out_filenames, ptc, out_filename)

		if final_reply_type == 'R':
			replySize = len(str(final_reply).encode('ascii'))
			send_msg(sock, 'REP', final_reply_type, replySize, final_reply)

		elif final_reply_type == 'F':
			out_file = open(out_filename, 'rb')

			size = os.path.getsize(out_filename)
			send_msg(sock, 'REP', final_reply_type, size, tail=' ')
			read_file_to_socket(out_file, sock, size)
			sock.sendall(b'\n')

			out_file.close()

	except ProtocolError:
		print('Incorrectly formulated REQ request. Replying ERR')
		send_msg(sock, 'REP', 'ERR')


def user_connection(sock: socket, request_number: int):
	"""
		Function corresponding to the child process created when a user connects.
	"""
	tcpSock.close()
	udpSock.close()
	try:
		clientAddress = sock.getpeername()
		bufferedReader = sock.makefile('rb', buffering=buffer_size)

		select.select([bufferedReader], [], [])
		try:
			if bufferedReader.peek() == b'':  # nothing left
				raise EOFError()

			msgType = read_word(bufferedReader)
			print("[" + msgType + "] request from", clientAddress)

			if msgType == 'LST':
				do_lst(sock, bufferedReader)

			elif msgType == 'REQ':
				do_req(sock, bufferedReader, request_number)

			else:
				print("Unknown message received.")
				send_msg(sock, 'ERR')

		except EOFError:
			print("End of stream. Client", clientAddress, "lost.")

	except KeyboardInterrupt:
		print("\nTCP connection", clientAddress, "exiting")
	finally:
		try_close(sock)
		sys.exit()


def tcp_accept():
	global request_number
	try:
		newSock, address = tcpSock.accept()
		print("Connection from client", address, "accepted, forking...")

		Process(target=user_connection, args=(newSock, request_number), daemon=True).start()
		request_number += 1

		try_close(newSock)
	except OSError:
		pass


def udp_receive():
	global processingTasks

	try:
		data, address = udpSock.recvfrom(buffer_size)
		msg = parse_data(data)

		if msg[0] == 'REG':
			try:
				protocol_assert(len(msg) > 3)

				wsAddress = msg[-2]
				wsPort = int(msg[-1])
				wsHost = (wsAddress, wsPort)

				print("Registration request received with address", wsHost, end='... ')

				for i in range(1, len(msg) - 2):
					protocol_assert(msg[i] != '')

					ptcHosts = processingTasks.setdefault(msg[i], [])
					if wsHost not in ptcHosts:
						ptcHosts.append(wsHost)

				encodedMsg = construct_message('RAK', 'OK')
				print("OK")
				udpSock.sendto(encodedMsg, address)

			except ProtocolError:
				encodedMsg = construct_message('RAK', 'ERR')
				print("ERR")
				udpSock.sendto(encodedMsg, address)

		elif msg[0] == 'UNR':
			try:
				protocol_assert(len(msg) == 3)

				wsAddress = msg[1]
				wsPort = int(msg[2])
				wsHost = (wsAddress, wsPort)

				print("Unregistration request received with address", wsHost, end='... ')

				found = False
				for ptc in list(processingTasks.keys()):
					if wsHost in processingTasks[ptc]:
						found = True
						processingTasks[ptc].remove(wsHost)

						if len(processingTasks[ptc]) == 0:
							del processingTasks[ptc]

				if found:
					print("OK")
					encodedMsg = construct_message('UAK', 'OK')
					udpSock.sendto(encodedMsg, address)
				else:
					print("NOK. No server with that address found.")
					encodedMsg = construct_message('UAK', 'NOK')
					udpSock.sendto(encodedMsg, address)

			except ProtocolError:
				encodedMsg = construct_message('UAK', 'ERR')
				print("Replying via UDP with ERR")
				udpSock.sendto(encodedMsg, address)

		else:
			print("Unknown UDP message received")
			encodedMsg = construct_message('ERR')
			udpSock.sendto(encodedMsg, address)

	except OSError:
		print("Error while receiving UDP message. Ignoring...")
		pass


if __name__ == '__main__':
	tcpSock = None
	udpSock = None

	try:
		parser = argparse.ArgumentParser()
		parser.add_argument('-p', type=int, metavar="CSport", default=default_port)
		args = parser.parse_args()

		hostname = ''  # meaning all available interfaces
		port = args.p

		print("Starting central server on " + hostname)

		try:
			os.makedirs(input_files_path)
		except OSError as e:
			if e.errno != errno.EEXIST:  # else, directory already exists. Continue
				raise
		try:
			os.makedirs(output_files_path)
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
					tcp_accept()
				elif sock == udpSock:
					udp_receive()

	except KeyboardInterrupt:
		print("\nCleaning up...")
		try_close(tcpSock)
		try_close(udpSock)
		print("Exiting")
		exit()
