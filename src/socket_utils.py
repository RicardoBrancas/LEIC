import socket
from io import BufferedReader, BufferedIOBase

buffer_size = 4096


def ownHostname():
	return socket.gethostname()


def readFileToSocket(file: BufferedIOBase, sock: socket, size):
	logProgress = False
	if size > 1000000:  # 1M
		logProgress = True

	try:
		while file.tell() < size:
			bytes = file.read(buffer_size)
			sock.sendall(bytes)
			if logProgress:
				print("Sending file", file.tell(), "/", size, "bytes sent", end='\r')
		if logProgress: print()
	except Exception:
		print("Error while sending file. Maybe the other side refused?")


def readSocketToFile(bufferedReader: BufferedReader, file: BufferedIOBase, size):
	logProgress = False
	if size > 1000000:  # 1M
		logProgress = True

	try:
		pointer = 0
		while pointer < size:
			data = bufferedReader.read(min(buffer_size, size - pointer))
			file.write(data)
			pointer += len(data)
			if logProgress:
				print("Receiving file", pointer, "/", size, "bytes received", end='\r')
		if logProgress: print()
	except Exception:
		print("Error while receiving file.")


def prepare_tcp_server(address, backlog=5) -> socket:
	tcpSock = None

	print("Starting TCP server")
	try:
		tcpSock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		tcpSock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)  # ignore system TIME_WAIT
	except OSError:
		print("Exception while creating TCP socket!")

	try:
		host = address
		tcpSock.bind(host)
	except OSError:
		print("Error during TCP bind. Maybe the port is in use already?")
		exit()

	tcpSock.listen(backlog)

	return tcpSock


def prepare_tcp_client(address) -> socket:
	sock = None

	try:
		sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)  # ignore system TIME_WAIT
	except OSError:
		print("Exception while creating socket!")
		sock.close()

	try:
		sock.connect(address)
	except OSError:
		print("Could not establish connection. Maybe server is offline?")
		sock.close()

	return sock


def prepare_udp_server(address) -> socket:
	udpSock = None

	print("Starting UDP server")
	try:
		udpSock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
		udpSock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)  # ignore system TIME_WAIT
	except OSError:
		print("Exception while creating UDP socket!")

	try:
		host = address
		udpSock.bind(host)
	except OSError:
		print("Error during UDP bind. Maybe the port is in use already?")
		exit()

	return udpSock


def prepare_udp_client(timeout=0) -> socket:
	udpSock = None

	print("Starting UDP server")
	try:
		udpSock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
		udpSock.settimeout(timeout)
	except OSError:
		print("Exception while creating UDP socket!")

	return udpSock
