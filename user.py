#!/usr/bin/python3

import sys
import argparse
import socket

from common import *

sock = None


def cleanup():
	print("Cleaning up ")
	try:
		sock.close()
	except OSError:
		pass

	exit()


if __name__ == '__main__':
	parser = argparse.ArgumentParser()
	parser.add_argument('-n', metavar="CSname", default='')
	parser.add_argument('-p', metavar="CSport", default=defaultPort)
	args = parser.parse_args()

	host = args.n
	port = int(args.p)

	try:
		sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	except OSError:
		print("Error creating socket")
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

	while True:
		line = sys.stdin.readline()
		lineLst = line.split()
		try:
			if lineLst[0] == 'list':
				print("Asking for list")
				sock.sendall(b'LST\n')
				result = sock.recv(1024)
				print(result)
			elif lineLst[0] == 'exit':
				break
		except OSError as ose:
			print("Error while talking to server:")
			print(ose)
			break

	cleanup()
