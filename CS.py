import sys
import argparse
import socket
import signal

from multiprocessing import Process

from common import *


def userConnection(sock):
	try:
		clientName = sock.getpeername()
		print("Forked")

		while True:
			try:
				msg = sock.recv(1024)
				print("Received message:", msg)
				sock.sendall(b'this is a listing')
			except OSError:
				print("Client", clientName, "lost...")
				sock.close()
				exit()
	except KeyboardInterrupt:
		print("TCP connection exiting")
		try:
			sock.close()
		except Exception:
			pass
		exit()


def tcpServer():
	sock = None
	try:
		print("Starting TCP server")
		try:
			sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		except Exception:
			print("Exception while creating TCP socket!")
			exit()

		try:
			host = (hostname, port)
			sock.bind(host)
		except Exception:
			print("Error during TCP bind. Maybe the port is in use already?")
			exit()

		sock.listen()

		print("Waiting for client connections...")
		while True:
			try:
				newSock, address = sock.accept()
				print("Connection from client", address, "accepted, forking...")

				Process(target=userConnection, args=(newSock,), daemon=True).start()

				newSock.close()
			except Exception:
				pass
	except KeyboardInterrupt:
		print("TCP server exiting")
		try:
			sock.close()
		except Exception:
			pass
		exit()

def udpServer():
	sock = None
	try:
		print("Starting UDP server")
		try:
			sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
		except Exception:
			print("Exception while creating UDP socket!")
			exit()

		try:
			host = (hostname, port)
			sock.bind(host)
		except Exception:
			print("Error during UDP bind. Maybe the port is in use already?")
			exit()

		print("Waiting for WS registrations...")
		while True:
			try:
				data, address = sock.recvfrom(1024)
				print("Connection from", address, "accepted, forking...")
			except Exception:
				pass
	except KeyboardInterrupt:
		print("UDP server exiting")
		try:
			sock.close()
		except Exception:
			pass
		exit()


# START MAIN

try:
	parser = argparse.ArgumentParser()
	parser.add_argument('-p', default=defaultPort)
	args = parser.parse_args()

	port = args.p

	hostname = ""  # meaning all available interfaces

	print("Starting central server on " + socket.gethostname())

	Process(target=udpServer, daemon=True).start()
	tcpServer()
except KeyboardInterrupt:
	exit(0)

# END MAIN
