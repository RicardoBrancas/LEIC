#!/usr/bin/python3

import argparse
import socket

from common import *

udpSock = None
udpTimeout = 10

hostname = ''  # meaning all available interfaces
address = socket.gethostbyname(socket.gethostname())
port = None

csName = None
csPort = None

PTCs = None


def work(ptc, data: str):
	if ptc == 'WCT':
		return len(data.split()), 'R'

	elif ptc == 'FLW':
		greatestWord = ''
		wordLen = 0
		for word in data.split():
			temp = len(word)
			if temp > wordLen:
				greatestWord = word
				wordLen = temp
		return greatestWord, 'R'

	elif ptc == 'UPP':
		return data.upper(), 'F'

	elif ptc == 'LOW':
		return data.lower(), 'F'

	return "", "R"


def prepareUDP():
	global udpSock

	print("Starting UDP server")
	try:
		udpSock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
		udpSock.settimeout(udpTimeout)
	except OSError:
		print("Exception while creating UDP socket!")
		exit()


def registerUDP():
	global udpSock

	print("Trying to register in central server...")

	registerMsg = "REG " + " ".join(PTCs) + " " + address + " " + str(port) + "\n"
	encoded = registerMsg.encode('ascii')
	success = False
	for i in range(1, 6):
		try:
			udpSock.sendto(encoded, (csName, csPort))
			response = parseData(udpSock.recv(1024))

			protocolAssert(len(response) == 2)
			protocolAssert(response[0] == "RAK")
			if response[1] == "OK":
				print("Central server accepted registration.")
				return
			elif response[1] == "NOK":
				print("Central server refused registration. Exiting...")
				exit()
			elif response[1] == "ERR":
				print("Central server found protocol error in registration. Exiting...")
				exit()
			else:
				raise ProtocolError

		except OSError:
			print("Try", i, "failed, trying again...")
		except ProtocolError:
			print("Protocol error. Trying again...")
		else:
			success = True
			break

	if not success:
		print("Failed to register on central server. Exiting...")
		exit(-1)


def prepareTCP():
	tcpSock = None

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

	tcpSock.listen(2)

	return tcpSock


if __name__ == '__main__':
	try:
		parser = argparse.ArgumentParser()
		parser.add_argument('-p', metavar="WSport", default=59000)
		parser.add_argument('-n', metavar="CSname", default='')
		parser.add_argument('-e', metavar="CSport", default=defaultPort)
		parser.add_argument('ptcs', metavar="PTC", nargs='+')
		args = parser.parse_args()

		port = int(args.p)
		csName = args.n
		csPort = int(args.e)

		PTCs = args.ptcs

		prepareUDP()
		registerUDP()

		tcpSock = prepareTCP()

		while tcpSock:
			try:
				newSock, address = tcpSock.accept()
				print("Accepted connection from:", address)
				bufferedReader = newSock.makefile('rb', 1024)

				msgType = readWord(bufferedReader)

				if msgType == 'WRQ':
					try:
						ptc = readWord(bufferedReader)
						if ptc not in PTCs:
							print("Unknown PTC sent by server:", ptc)
							sendMsg(newSock, 'WRP', 'EOF')

						filename = readWord(bufferedReader)
						size = readNumber(bufferedReader)
						data = bufferedReader.read(size).decode('ascii')

						result, type = work(ptc, data)

						replySize = len(result.encode('ascii'))

						sendMsg(newSock, 'REP', type, replySize, result)

					except ProtocolError:
						print("Request not correctly formulated. Replying with WRP ERR")
						sendMsg(newSock, 'WRP', 'ERR')
				else:
					print("Unknown message from server:", msgType)
					sendMsg(newSock, 'ERR')

			except OSError:
				pass
			finally:
				tryClose(newSock)

	except KeyboardInterrupt:
		pass
