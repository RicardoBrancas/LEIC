import argparse
import os

from subprocess import run, DEVNULL

tests_folder = 'tests'

parser = argparse.ArgumentParser(description='Tester for project 1')
parser.add_argument('-v', action='store_true')
args = parser.parse_args()


for root, test_folders, files in os.walk(tests_folder):
	for test_folder in sorted(test_folders):
		in_file = open(os.path.join(root, test_folder, 'input'), 'r')
		result_file = open(os.path.join(root, test_folder, 'result'), 'w+')

		print("Running", test_folder)

		if args.v:
			run(['python3', '-i', 'src/main.py'], stdin=in_file, stdout=result_file)
		else:
			run(['python3', '-i', 'src/main.py'], stdin=in_file, stdout=result_file, stderr=DEVNULL)

		in_file.close()
		result_file.close()

		run(['diff', '--color', os.path.join(root, test_folder, 'output'), os.path.join(root, test_folder, 'result')])


