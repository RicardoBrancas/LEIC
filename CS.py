#!/usr/bin/python3

import argparse
import socket
import select

from multiprocessing import Process

from common import *

tcpSock = None
udpSock = None

processingTasks = {}

port = None
hostname = ""  # meaning all available interfaces


def cleanup():
	print("\nCleaning up...")
	try:
		tcpSock.close()
	except OSError:
		pass
	try:
		udpSock.close()
	except OSError:
		pass
	print("Exiting")
	exit()


def userConnection(sock):
	def userConnectionCleanup():
		try:
			sock.close()
		except OSError:
			pass
		exit()

	try:
		client_name = sock.getpeername()
		print("Forked")

		while True:
			try:
				msg = parseData(sock.recv(1024))

				if msg is not None:
					print('[', msg[0], '] request received from', client_name)

					if msg[0] == "LST":
						assert len(msg) == 1

						if len(processingTasks) == 0:
							sock.sendall("FTP EOF\n".encode('ascii'))
						else:
							response = "FPT " + str(len(processingTasks)) + " "
							for task in processingTasks.keys():
								response += task + " "
							response += "\n"
							sock.sendall(response.encode('ascii'))

				else:
					print("Empty message received... Leaving connection")
					userConnectionCleanup()


			except socket.error:
				print("Client", client_name, "lost...")
				userConnectionCleanup()

	except KeyboardInterrupt:
		print("\nTCP connection exiting")
		userConnectionCleanup()


def prepareTcp():
	global tcpSock

	print("Starting TCP server")
	try:
		tcpSock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		tcpSock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
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
	try:
		newSock, address = tcpSock.accept()
		print("Connection from client", address, "accepted, forking...")

		Process(target=userConnection, args=(newSock,), daemon=True).start()

		newSock.close()
	except OSError:
		pass


def prepareUdp():
	global udpSock

	print("Starting UDP server")
	try:
		udpSock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
		udpSock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
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
		data, address = udpSock.recvfrom(1024)
		msg = data.decode('ascii').split(" ")

		if msg[0] == 'REG':
			try:
				assert len(msg) > 3

				print(address, "has asked to register with following PTCs:", msg[1:-2])

				wsAddress = msg[-2]
				wsPort = msg[-1]

				for i in range(1, len(msg)-2):
					assert msg[i] != ""

					if not msg[i] in processingTasks:
						processingTasks[msg[i]] = []
					if not (wsAddress, wsPort) in processingTasks[msg[i]]:
						processingTasks[msg[i]] += [(wsAddress, wsPort)]

				print("Registration from", address, "accepted")
				udpSock.sendto("RAK OK\n".encode('ascii'), address)
			except Exception:
				print("Registration from", address, "refused (protocol error)")
				udpSock.sendto("RAK ERR\n".encode('ascii'), address)
		else:
			print("Unknown UDP message received. Ignoring...")

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
