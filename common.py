import collections

import sys

GN = 16
defaultPort = 58000 + GN

ptcDescriptions = {
	'WCT': 'word count',
	'FLW': 'find longest word',
	'UPP': 'convert text to uppercase',
	'LOW': 'convert text to lowercase'
}


def tryClose(socket):
	try:
		socket.close()
	except Exception:
		pass


def parseData(data):
	if data != b'':
		msg = data.decode('ascii')
		if msg[-1] != "\n":
			print("Malformed message. Missing trailing \\n! Continuing... Unexpected behavior may occur.")
		return msg[:-1].split(" ")
	else:
		return None


def constructMessage(*args):
	msg = []
	for arg in args:
		if isinstance(arg, collections.Iterable) and not isinstance(arg, str):
			msg += arg
		else:
			msg += [arg]

	string = ' '.join(str(e) for e in msg) + "\n"

	return msg, string.encode('ascii')


def logMessage(io, protocol, tofrom, address, msg, stream=sys.stdout):
	log = io.center(3)
	log += "[" + protocol + "]"
	log += "[" + tofrom.center(4) + "]"
	log += "[" + msg[0] + "]"
	log += "[" + address[0] + "," + str(address[1]) + "]"
	log += "{" + str(msg[1:]) + "}"

	stream.write(log + "\n")
