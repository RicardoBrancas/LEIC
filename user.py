import sys
import argparse
import socket

parser = argparse.ArgumentParser()
parser.add_argument('-n', default='')
parser.add_argument('-p', default=58016)
args = parser.parse_args()

host = args.n
port = int(args.p)

try:
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
except Exception:
    print("Error creating socket")
    exit()

print("Trying to connect to host " + host + " on port " + str(port))
sock.connect((host, port))

while True:
  line = sys.stdin.readline()
  lineLst = line.split(' ')
  
  if lineLst[0] == 'list':
    print("Asking for list")
    sock.sendall(b'LST\n')
    result = sock.recv(1024)
    print(result)

sock.close()
