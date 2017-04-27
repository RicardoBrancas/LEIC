import subprocess
import time
import math
import os, shutil

generator = "./gen_p2"

result = open("results1.data", "w+");

print("Starting!");

result.write("c t tln\n")
for c in range(0, 20000, 1000):
	ci = max(c, 2)
	a = int(c**2 / 2);
	r = int(c**2 / 2);
	maxW = int(c / 2);

	print("Running test "+str(ci)+"-"+str(a)+"-"+str(r)+"-"+str(maxW)+"-0", sep='')

	ns = 0;
	sums = 0;

	print("\tGenerating input...");
	f = open("./profile_tests/test_"+str(ci)+"-"+str(a)+"-"+str(r)+"-"+str(maxW)+"-0.in", "w+")
	subprocess.run([generator, str(ci), str(a), str(r), str(maxW), "0"], stdout=f)

	for i in range(0, 3):
		print("\t" + str(i), end=': ', flush=True)

		f.seek(0);
		cp = subprocess.run(['/usr/bin/time -f "%e" ./out/main > /dev/null'], stdin=f, stdout=subprocess.DEVNULL, stderr=subprocess.PIPE, universal_newlines=True, shell=True)
		time = float(cp.stderr)
		print(time)

		ns+=1;
		sums+=time;

	f.close();

	print("Total time: " + "{0:.5f}".format(sums) + " over " + str(ns) + " tests, avg: " + "{0:.5f}".format(sums/ns) + "\n\n")
	result.write(str(ci) + " " + "{0:.5f}".format(sums/ns) + " " + "{0:.5f}".format(sums/ns / math.log(ci)) + "\n")
	result.flush();

result.close();

folder = './profile_tests/'
for the_file in os.listdir(folder):
    file_path = os.path.join(folder, the_file)
    try:
        if os.path.isfile(file_path):
            os.unlink(file_path)
        #elif os.path.isdir(file_path): shutil.rmtree(file_path)
    except Exception as e:
        print(e)

# result = open("results2.data", "w+");
#
# print("Starting (2)!");
#
# n = {}
# sums = {}
#
# result.write("c t tln\n")
# c = 2000
# for a in range(c, int(c**2 / 2), 50*c):
# 	ci = max(c, 2)
# 	r = a;
# 	maxW = int(c / 2);
#
# 	print("Running test "+str(ci)+"-"+str(a)+"-"+str(r)+"-"+str(maxW)+"-0", sep='')
#
# 	ns = 0;
# 	sums = 0;
#
# 	for i in range(0, 3):
# 		print("\t" + str(i), end=': ')
# 		f = open("./profile_tests/test_"+str(ci)+"-"+str(a)+"-"+str(r)+"-"+str(maxW)+"-0.in", "w+")
# 		subprocess.run([generator, str(ci), str(a), str(r), str(maxW), "0"], stdout=f)
#
# 		f.seek(0);
# 		cp = subprocess.run(['/usr/bin/time -f "%e" ./out/main > /dev/null'], stdin=f, stdout=subprocess.DEVNULL, stderr=subprocess.PIPE, universal_newlines=True, shell=True)
# 		time = float(cp.stderr)
# 		print(time)
#
# 		f.close();
#
# 		ns+=1;
# 		sums+=time;
#
# 	print("Total time: " + "{0:.6f}".format(sums) + " over " + str(ns) + " tests\n\n")
# 	result.write(str(a) + " " + "{0:.6f}".format(sums/ns) + " " + "{0:.6f}".format(sums/ns / math.log(c)) + "\n")
# 	result.flush();
#
# result.close();

print("Generating PDF file...")
subprocess.run(['pdflatex', 'relatorio.tex'], stdout=subprocess.DEVNULL)
print("All done!")
