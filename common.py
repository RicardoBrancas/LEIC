GN = 16
defaultPort = 58000 + GN


def parseData(data):
	if data != b'':
		msg = data.decode('ascii')
		if msg[-1] != "\n":
			print("Malformed message. Missing trailing \\n! Continuing... Unexpected behavior may occur.")
		return msg[:-1].split(" ")
	else:
		return None
