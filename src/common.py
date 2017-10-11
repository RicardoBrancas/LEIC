from socket_utils import *

GN = 16
default_port = 58000 + GN

ptc_descriptions = {
	'WCT': 'word count',
	'FLW': 'find longest word',
	'UPP': 'convert text to uppercase',
	'LOW': 'convert text to lowercase'
}

input_files_path = "input_files/"


def try_close(closeable):
	try:
		closeable.close()
	except Exception:
		pass


class ProtocolError(Exception):
	pass


def protocol_assert(predicate):
	"""
		If predicate is False, raise an exception.
		Used to detect protocol errors.
	"""
	if not predicate:
		raise ProtocolError


def make_filename(request_id: int, part_number: int = None, ext='.txt') -> str:
	filename = input_files_path + str(request_id).rjust(5, '0')

	if part_number is not None:
		filename += str(part_number).rjust(3, '0')
	filename += ext

	return filename


def read_word(bufferedReader: BufferedReader):  # reads chars until the next space or \n is found
	"""
		Reads a full word (every byte until a space/newline is found), removing it and the (single!) space from the buffer.
		If a new line is found instead of a space it is left in the buffer so that it can be checked later.
	"""
	try:
		string = ''
		spaceRead = False
		while not spaceRead:
			bytes = bufferedReader.peek(1)[:1]  # we may have gotten more than we wanted. Ignore the rest
			char = bytes.decode('ascii')
			if char == ' ':
				bufferedReader.read(1)  # remove from buffer
				spaceRead = True
			elif char == '\n':
				spaceRead = True
			else:
				bufferedReader.read(1)  # remove from buffer
				string += char
	except Exception:
		raise ProtocolError()

	return string


def read_number(bufferedReader: BufferedReader):
	"""
		Tries to read a word (space delimited) and parse it as an integer.
	"""
	digits = read_word(bufferedReader)
	if not digits.isdigit():
		raise ProtocolError('Tried to read a number but found', digits)

	return int(digits)


def assert_end_of_message(bufferedReader: BufferedReader):
	"""
		Asserts that we are at the end of a message and discards the corresponding byte.
		Stream may be left in unusable state if we are not at end.
	"""
	byte = bufferedReader.read(1)
	if not byte == b'\n':
		raise ProtocolError()


def parse_data(data):
	"""
		Simple parsing intended for UDP messages. Minimal error checking.
		:return: Array of strings. Each with a part of a message. Space delimited.
	"""
	if data != b'':
		msg = data.decode('ascii')
		if msg[-1] != "\n":
			print("Malformed message. Missing trailing \\n! Continuing... Unexpected behavior may occur.")
		return msg[:-1].split(" ")
	else:
		return None


def construct_message(*args):
	"""
		Constructs a space delimited message terminating with '\n' from vararg strings.
		To be used primarily with UDP.
	"""
	msg = []
	for arg in args:
		if isinstance(arg, list) or isinstance(arg, tuple):
			msg += arg
		else:
			msg += [arg]

	string = ' '.join(str(e) for e in msg) + "\n"

	return string.encode('ascii')


def encode_elem(elem):
	"""
		:return: a byte representation of the elem.
	"""
	if isinstance(elem, bytes) or isinstance(elem, bytearray):
		return elem
	else:
		return str(elem).encode('ascii')


def send_msg(socket, *args, tail='\n'):
	"""
		Constructs a space delimited message terminating with '\n' from vararg strings, and sends it via the socket.
		To be used primarily with TCP.
	"""
	encoded = b''
	first = True
	for arg in args:
		if not first:
			encoded += b' '
		else:
			first = False

		if isinstance(arg, list) or isinstance(arg, tuple):
			first = True
			for argElem in arg:
				if not first:
					encoded += b' '
				else:
					first = False
				encoded += encode_elem(argElem)
		else:
			encoded += encode_elem(arg)

	encoded += tail.encode('ascii')

	socket.sendall(encoded)


def get_ip():
	"""
		Used just in case if the IP associated with the host is a loopback address
	"""
	s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
	try:
		# doesn't have to be reachable
		s.connect(('10.255.255.255', 1))
		IP = s.getsockname()[0]
	except:
		IP = '127.0.0.1'
	finally:
		s.close()
	return IP
