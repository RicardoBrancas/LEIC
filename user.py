import sys
import argparse
import socket

parser = argparse.ArgumentParser()
parser.add_argument('-n', default='')
parser.add_argument('-p', default=58016)
args = parser.parse_args()

host = args.n
port = args.p

hostaddr = socket.gethostbyname(host);

try:
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
except Exception:
    print("Error creating socket")
    exit()

print("Trying to connect to host " + str(hostaddr) + " on port " + str(port))
sock.connect((hostaddr, port))

sock.close()
