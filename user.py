#!/usr/bin/python3

import argparse
import socket

from common import *

host = None
port = None
sock = None


def cleanup():
	print("Cleaning up")
	tryClose(sock)
	exit()


def prepareTCP():
	global sock

	print("Starting TCP server")
	try:
		sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)  # ignore system TIME_WAIT
	except OSError:
		print("Exception while creating socket!")
		cleanup()
		exit()

	print("Trying to connect to host '" + host + "' on port " + str(port))
	try:
		sock.connect((host, port))
	except OSError:
		print("Could not establish connection. Maybe server is offline?")
		cleanup()
		exit()

	print("Connection established.")


if __name__ == '__main__':
	parser = argparse.ArgumentParser()
	parser.add_argument('-n', metavar="CSname", default='')
	parser.add_argument('-p', metavar="CSport", default=defaultPort)
	args = parser.parse_args()

	host = args.n
	port = int(args.p)

	prepareTCP()

	while True:
		line = sys.stdin.readline()
		lineLst = line.split()
		try:
			if lineLst[0] == 'list':
				msg, encoded = constructMessage('LST')
				sock.sendall(encoded)

				response = parseData(sock.recv(1024))

				assert response[0] == 'FPT'

				if response[1] == 'EOF':
					assert len(response) == 2

					print('No tasks available')
				elif response[1].isdigit():
					n = int(response[1])
					assert len(response[2:]) == n

					print('Server supports the following tasks:')
					i = 1
					for ptc in response[2:]:
						print(str(i) + ". " + ptc + ": " + ptcDescriptions[ptc])
						i += 1

				elif response[1] == 'ERR':
					print("Server complained about protocol error.")
				else:
					print("Server replied with garbage")

			elif lineLst[0] == 'exit':
				break
		except OSError as ose:
			print("Error while talking to server:")
			print(ose)
			break

	cleanup()
