#!/usr/bin/env python3

import argparse
import readline  # do not remove
import os

from common import *


def do_list():
	sock = prepare_tcp_client((host, port))
	bufferedReader = sock.makefile('rb', buffer_size)

	try:
		send_msg(sock, 'LST')

		protocol_message = read_word(bufferedReader)
		protocol_assert(protocol_message == 'FPT')

		word = read_word(bufferedReader)
		if word == 'EOF':
			assert_end_of_message(bufferedReader)
			print('No tasks available')

		elif word == 'ERR':
			assert_end_of_message(bufferedReader)
			print('Server complained about protocol error.')

		else:
			protocol_assert(word.isdigit())
			n = int(word)

			print("Server supports the following tasks:")
			for i in range(n):
				ptc = read_word(bufferedReader)
				print(str(i + 1) + ". " + ptc + ": " + ptc_descriptions[ptc])

			assert_end_of_message(bufferedReader)

	except ProtocolError:
		print("Protocol error while communicating with server.")
	finally:
		bufferedReader.close()
		sock.close()


def do_request(input_parts):
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
		send_msg(sock, 'REQ', ptc, file_size, tail=' ')

		read_file_to_socket(file, sock, file_size)
		file.close()
		sock.sendall(b'\n')

		protocol_message = read_word(bufferedReader)
		protocol_assert(protocol_message == 'REP')

		msg = read_word(bufferedReader)
		if msg == 'EOF':
			assert_end_of_message(bufferedReader)
			print("Server does not support", ptc, "please use 'list' to get supported operations.")

		elif msg == 'ERR':
			assert_end_of_message(bufferedReader)
			raise ProtocolError

		elif msg == 'R' or msg == 'F':
			reply_type = msg
			size = read_number(bufferedReader)

			if reply_type == 'R':
				data = bufferedReader.read(size)
				assert_end_of_message(bufferedReader)
				print('Reply:', data.decode('ascii'))

			else:
				print('Saving reply to', filename + ".new")
				file_out = open(filename + ".new", 'wb')
				read_socket_to_file(bufferedReader, file_out, size)
				file_out.close()
				assert_end_of_message(bufferedReader)
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
	parser.add_argument('-p', metavar="CSport", default=default_port)
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
					do_list()

				elif line_parts[0] == 'request':
					do_request(line_parts)

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
