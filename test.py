import os

from subprocess import run, DEVNULL

tests_folder = 'tests'


for root, test_folders, files in os.walk(tests_folder):
	for test_folder in sorted(test_folders):
		in_file = open(os.path.join(root, test_folder, 'input'), 'r')
		result_file = open(os.path.join(root, test_folder, 'result'), 'w+')

		print("Running", test_folder)
		run(['python3', '-i', 'src/main.py'], stdin=in_file, stdout=result_file, stderr=DEVNULL)
		in_file.close()
		result_file.close()

		run(['diff', '--color', os.path.join(root, test_folder, 'output'), os.path.join(root, test_folder, 'result')])

