#!/usr/bin/python3

import argparse
import socket

from common import *

udpSock = None
udpTimeout = 10

hostname = ''  # meaning all available interfaces
address = socket.gethostbyname(hostname)
port = None

csName = None
csPort = None

PTCs = None


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


if __name__ == '__main__':
	try:
		parser = argparse.ArgumentParser()
		parser.add_argument('-p', metavar="WSport", default=59000)
		parser.add_argument('-n', metavar="CSname", default='')
		parser.add_argument('-e', metavar="CSport", default=defaultPort)
		parser.add_argument('ptcs', metavar="PTC", nargs='+')
		args = parser.parse_args()

		port = args.p
		csName = args.n
		csPort = args.e

		PTCs = args.ptcs

		prepareUDP()
		registerUDP()

	except KeyboardInterrupt:
		pass
