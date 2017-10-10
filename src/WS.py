#!/usr/bin/env python3

import argparse

import os

from multiprocessing import Process

from common import *

hostname = ''
address = socket.gethostbyname(hostname)
port = None

csName = None
csPort = None

PTCs = None


def work(ptc, file: BufferedIOBase, file_size: int):
	if ptc == 'WCT':
		wct = 0

		last_byte = file.read(1)
		if not last_byte.isspace(): wct += 1  # starts with a word
		while file.tell() < file_size:
			new_byte = file.read(1)
			if last_byte.isspace() and not new_byte.isspace():
				wct += 1
			last_byte = new_byte

		return wct, 'R'

	elif ptc == 'FLW':
		greatest_word = b''
		gw_len = 0

		current_word = b''
		current_len = 0

		last_byte = file.read(1)
		while file.tell() < file_size:
			new_byte = file.read(1)

			if not last_byte.isspace():
				current_word += last_byte
				current_len += 1

			if not last_byte.isspace() and new_byte.isspace():  # end of word
				if current_len > gw_len:
					greatest_word = current_word
					gw_len = current_len
				current_word = b''
				current_len = 0

			last_byte = new_byte

		if not last_byte.isspace():
			current_word += last_byte
			current_len += 1
			if current_len > gw_len:
				greatest_word = current_word

		return greatest_word, 'R'

	elif ptc == 'UPP':
		while file.tell() < file_size:
			data = file.read(min(buffer_size, file_size - file.tell()))
			file.seek(-len(data), os.SEEK_CUR)
			data = data.upper()
			file.write(data)

		return '', 'F'

	elif ptc == 'LOW':
		while file.tell() < file_size:
			data = file.read(min(buffer_size, file_size - file.tell()))
			file.seek(-len(data), os.SEEK_CUR)
			data = data.lower()
			file.write(data)

		return '', 'F'

	return "", "R"


def process(sock):
	udp_sock.close()
	tcp_sock.close()

	bufferedReader = sock.makefile('rb', buffer_size)

	try:
		msgType = readWord(bufferedReader)

		if msgType == 'WRQ':

			ptc = readWord(bufferedReader)
			if ptc not in PTCs:
				print("Unknown PTC sent by server:", ptc)
				sendMsg(sock, 'WRP', 'EOF')
			else:
				print("Request PTC is", ptc)

				filename = readWord(bufferedReader) + ".ws"
				size = readNumber(bufferedReader)

				file = open(filename, 'w+b')
				readSocketToFile(bufferedReader, file, size)
				assertEndOfMessage(bufferedReader)

				file.seek(0)

				result, type = work(ptc, file, size)

				if type == 'R':
					file.close()
					if isinstance(result, bytes):
						replySize = len(result)
					else:
						replySize = len(str(result).encode('ascii'))

					sendMsg(sock, 'REP', type, replySize, result)
				elif type == 'F':
					file.seek(0)

					sendMsg(sock, 'REP', type, size, tail=' ')
					readFileToSocket(file, sock, size)
					sock.sendall(b'\n')

					file.close()
		else:
			print("Unknown message from server:", msgType)
			sendMsg(sock, 'ERR')

	except ProtocolError:
		print("Request not correctly formulated. Replying with WRP ERR")
		sendMsg(sock, 'WRP', 'ERR')


def register(sock, address, port):
	print("Trying to register in central server...")

	registerMsg = "REG " + " ".join(PTCs) + " " + address + " " + str(port) + "\n"
	encoded = registerMsg.encode('ascii')
	success = False
	for i in range(1, 6):
		try:
			sock.sendto(encoded, (csName, csPort))
			response = parseData(sock.recv(buffer_size))

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


def unregister(sock, address, port):
	print("Trying to unregister from central server...")

	message = "UNR " + address + " " + str(port) + "\n"
	encoded = message.encode('ascii')
	success = False
	for i in range(1, 6):
		try:
			sock.sendto(encoded, (csName, csPort))
			response = parseData(sock.recv(buffer_size))

			protocolAssert(len(response) == 2)
			protocolAssert(response[0] == "UAK")
			if response[1] == "OK":
				print("Central server accepted unregistration.")
				return
			elif response[1] == "NOK":
				print("Central server refused unregistration. Exiting anyway...")
				exit()
			elif response[1] == "ERR":
				print("Central server found protocol error in registration. Exiting anyway...")
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
		print("Failed to unregister from central server. Exiting anyway...")
		exit(-1)


if __name__ == '__main__':

	parser = argparse.ArgumentParser()
	parser.add_argument('-p', type=int, metavar="WSport", default=59000)
	parser.add_argument('-n', metavar="CSname", default='')
	parser.add_argument('-e', type=int, metavar="CSport", default=defaultPort)
	parser.add_argument('ptcs', metavar="PTC", nargs='+')
	args = parser.parse_args()

	port = args.p
	csName = args.n
	csPort = args.e

	PTCs = args.ptcs

	udp_sock = prepare_udp_client(20)
	tcp_sock = prepare_tcp_server((address, port))

	try:
		if address.startswith('127.') or address == '0.0.0.0':
			address = get_ip()

		register(udp_sock, address, port)

		while tcp_sock:
			newSock = None
			try:
				newSock, address = tcp_sock.accept()
				print("Accepted connection from:", address, ". Forking...")

				Process(target=process, args=(newSock,), daemon=True).start()

				newSock.close()

			except OSError:
				pass
			finally:
				if newSock:
					newSock.close()

	except KeyboardInterrupt:
		print()
	finally:
		tcp_sock.close()
		unregister(udp_sock, address, port)
		udp_sock.close()
