import argparse
import socket
import select

from multiprocessing import Process

from common import *

tcpSock = None
udpSock = None

processingTasks = {}


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
				msg = sock.recv(1024)

				if msg != b'':
					action = msg[0:3]
					print('Request received from ', client_name, ': ', repr(action))

					if msg == b"LST\n":
						if len(processingTasks) == 0:
							sock.sendall("FTP EOF\n".encode('ascii'))
						else:
							response = "FPT " + str(len(processingTasks))
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
	except OSError:
		print("Exception while creating TCP socket!")
		exit()

	try:
		host = (hostname, port)
		tcpSock.bind(host)
	except OSError:
		print("Error during TCP bind. Maybe the port is in use already?")
		exit()

	tcpSock.listen()


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
		print("UDP packet from", address, "received")
	except OSError:
		pass


# START MAIN

try:
	parser = argparse.ArgumentParser()
	parser.add_argument('-p', default=defaultPort)
	args = parser.parse_args()

	port = args.p

	hostname = ""  # meaning all available interfaces

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

# END MAIN
