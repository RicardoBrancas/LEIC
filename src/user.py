#!/usr/bin/env python3

import argparse
import readline  # do not remove
import os

from common import *


def action_list():
	sock = prepare_tcp_client((host, port))
	bufferedReader = sock.makefile('rb', buffer_size)

	try:
		sendMsg(sock, 'LST')

		protocol_message = readWord(bufferedReader)
		protocolAssert(protocol_message == 'FPT')

		word = readWord(bufferedReader)
		if word == 'EOF':
			assertEndOfMessage(bufferedReader)
			print('No tasks available')

		elif word == 'ERR':
			assertEndOfMessage(bufferedReader)
			print('Server complained about protocol error.')

		else:
			protocolAssert(word.isdigit())
			n = int(word)

			print("Server supports the following tasks:")
			for i in range(n):
				ptc = readWord(bufferedReader)
				print(str(i + 1) + ". " + ptc + ": " + ptcDescriptions[ptc])

	except ProtocolError:
		print("Protocol error while communicating with server.")
	finally:
		bufferedReader.close()
		sock.close()


def action_request(input_parts):
	if len(input_parts) != 3:
		print("Syntax error. Please try again")

	ptc = input_parts[1]
	filename = input_parts[2]

	if not os.path.isfile(filename):
		print("File", filename, "does not exist.")
		return

	file = open(filename, 'rb')
	file_size = os.path.getsize(filename)

	sock = prepare_tcp_client((host, port))
	bufferedReader = sock.makefile('rb', buffer_size)

	try:
		sendMsg(sock, 'REQ', ptc, file_size, tail=' ')

		readFileToSocket(file, sock, file_size)
		file.close()
		sock.sendall(b'\n')

		protocol_message = readWord(bufferedReader)
		protocolAssert(protocol_message == 'REP')

		msg = readWord(bufferedReader)
		if msg == 'EOF':
			assertEndOfMessage(bufferedReader)
			print("Server does not support", ptc, "please use 'list' to get supported operations.")

		elif msg == 'ERR':
			assertEndOfMessage(bufferedReader)
			raise ProtocolError

		elif msg == 'R' or msg == 'F':
			reply_type = msg
			size = readNumber(bufferedReader)

			if reply_type == 'R':
				data = bufferedReader.read(size)
				assertEndOfMessage(bufferedReader)
				print('Reply:', data.decode('ascii'))

			else:
				print('Saving reply to', filename + ".new")
				file_out = open(filename + ".new", 'wb')
				readSocketToFile(bufferedReader, file_out, size)
				file_out.close()
				assertEndOfMessage(bufferedReader)
		else:
			raise ProtocolError

	except ProtocolError:
		print("Protocol error while communicating with server.")
	finally:
		bufferedReader.close()
		sock.close()


if __name__ == '__main__':
	parser = argparse.ArgumentParser()
	parser.add_argument('-n', metavar="CSname", default='')
	parser.add_argument('-p', metavar="CSport", default=defaultPort)
	args = parser.parse_args()

	host = args.n
	port = int(args.p)

	print("Checking if server is online...")
	tempSock = prepare_tcp_client((host, port))
	tempSock.close()

	print("Waiting for commands...")
	try:
		while True:
			line = input('> ')
			line_parts = line.split()
			try:
				if len(line_parts) < 1:
					print("Unknown command. Please try again.")
					continue

				if line_parts[0] == 'list':
					action_list()

				elif line_parts[0] == 'request':
					action_request(line_parts)

				elif line_parts[0] == 'exit':
					break

				else:
					print("Unknown command. Please try again.")

			except OSError:
				print("Error while talking to server. Exiting.")
				break
	except KeyboardInterrupt:
		pass

	exit()
