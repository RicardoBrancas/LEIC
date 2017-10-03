#!/usr/bin/python3

import argparse
import socket
import sys

from common import *

host = None
port = None
sock = None
bufferedReader = None


def cleanup():
	print("Cleaning up")
	tryClose(sock)
	exit()


def prepareTCP():
	global sock, bufferedReader
	
	try:
		sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)  # ignore system TIME_WAIT
	except OSError:
		print("Exception while creating socket!")
		cleanup()
		exit()
	
	try:
		sock.connect((host, port))
		bufferedReader = sock.makefile('rb', buffering=1024)
	except OSError:
		print("Could not establish connection. Maybe server is offline?")
		cleanup()
		exit()


def tryTCPClose():
	global sock
	tryClose(sock)


if __name__ == '__main__':
	parser = argparse.ArgumentParser()
	parser.add_argument('-n', metavar="CSname", default='')
	parser.add_argument('-p', metavar="CSport", default=defaultPort)
	args = parser.parse_args()

	host = args.n
	port = int(args.p)

	print("Checking if server is online...")
	prepareTCP()
	tryTCPClose()

	print("Waiting for commands...")
	while True:
		line = sys.stdin.readline()
		lineLst = line.split()
		try:
			if lineLst[0] == 'list':
				prepareTCP()

				try:
					sendMsg(sock, 'LST')
					msgType = readWord(bufferedReader)

					protocolAssert(msgType == 'FPT')

					msg2 = readWord(bufferedReader)
					if msg2 == 'EOF':
						assertEndOfMessage(bufferedReader)
						print('No tasks available')

					elif msg2 == 'ERR':
						assertEndOfMessage(bufferedReader)
						print('Server complained about protocol error.')

					else:
						protocolAssert(msg2.isdigit())
						n = int(msg2)

						ptcs = []
						for i in range(n):
							ptcs += [readWord(bufferedReader)]
						assertEndOfMessage(bufferedReader)

						print("Server supports the following tasks:")
						for i, ptc in enumerate(ptcs):
							print(str(i + 1) + ". " + ptc + ": " + ptcDescriptions[ptc])
						
				except ProtocolError:
					print("Protocol error while communicating with server.")
				finally:
					tryTCPClose()

			elif lineLst[0] == 'request':
				ptc = lineLst[1]
				filename = lineLst[2]

				file = open(filename, 'r')
				fileData = file.read()
				dataSize = len(fileData.encode('ascii'))
				file.close()
				
				prepareTCP()
				try:
					sendMsg(sock, 'REQ', ptc, dataSize, fileData)

					msgType = readWord(bufferedReader)
					protocolAssert(msgType == 'REP')
					replyType = readWord(bufferedReader)
					size = readNumber(bufferedReader)
					data = bufferedReader.read(size)

					if replyType == 'R':
						print("Server replied with", data.decode('ascii'))
					elif replyType == 'F':
						print("Saving server reply in", filename)
						file = open(filename, 'wb')
						file.write(data)
						file.close()

				except ProtocolError:
					print("Protocol error while communicating with server.")
				finally:
					tryTCPClose()

			elif lineLst[0] == 'exit':
				break

			else:
				print("Unknown command. Please try again.")

		except OSError:
			print("Error while talking to server:")
			break

	cleanup()
