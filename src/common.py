from io import BufferedReader

GN = 16
defaultPort = 58000 + GN

ptcDescriptions = {
	'WCT': 'word count',
	'FLW': 'find longest word',
	'UPP': 'convert text to uppercase',
	'LOW': 'convert text to lowercase'
}


def tryClose(closeable):
	try:
		closeable.close()
	except Exception:
		pass


class ProtocolError(Exception):
	pass


def protocolAssert(predicate):
	if not predicate:
		raise ProtocolError


def readWord(bufferedReader: BufferedReader):  # reads chars until the next space or \n is found
	string = ''
	spaceRead = False
	while not spaceRead:
		bytes = bufferedReader.peek(1)[:1]  # we may have gotten more than we wanted. Ignore the rest
		char = bytes.decode('ascii')
		if char == ' ':
			bufferedReader.read(1)  # remove from buffer
			spaceRead = True
		elif char == '\n':
			spaceRead = True  # leave in buffer
		else:
			bufferedReader.read(1)  # remove from buffer
			string += char
	return string


def readNumber(bufferedReader: BufferedReader):
	digits = readWord(bufferedReader)
	if not digits.isdigit():
		raise ProtocolError('Tried to read a number but found', digits)

	return int(digits)


def assertEndOfMessage(bufferedReader: BufferedReader):
	byte = bufferedReader.read(1)
	char = byte.decode('ascii')
	if not char == '\n':
		raise ProtocolError()


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
		if isinstance(arg, list) or isinstance(arg, tuple):
			msg += arg
		else:
			msg += [arg]

	string = ' '.join(str(e) for e in msg) + "\n"

	return msg, string.encode('ascii')


def sendMsg(socket, *args):
	_, encoded = constructMessage(*args)
	socket.sendall(encoded)
