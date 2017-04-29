import subprocess
import time
import math
import os, shutil

jobs = 2
generator = "./gen_p2"
result = open("results.data", "w+");

print("Starting!");

result.write("c t tln\n")
for c in range(0, 600000, 50000):
	ci = max(c, 2)
	a = int(min(ci**2 / 2, 4*ci));
	r = int(min(ci**2 / 2, 4*ci));
	maxW = int(ci / 2);

	print("Running test "+str(ci)+"-"+str(a)+"-"+str(r)+"-"+str(maxW)+"-0", sep='')

	ns = 0;
	sums = 0;
	f_name = "./profile_tests/test_"+str(ci)+"-"+str(a)+"-"+str(r)+"-"+str(maxW)+"-0.in"
	print("\tGenerating input...");
	f = open(f_name, "w+")
	subprocess.run([generator, str(ci), str(a), str(r), str(maxW), "0"], stdout=f)
	f.close();

	for i in range(0, 10, jobs):

		ps = [None] * jobs
		for j in range(0, jobs):
			print("\tStarting " + str(i+j))
			fin = open(f_name, "r");
			ps[j] = subprocess.Popen(['/usr/bin/time -f "%e" ./out/main > /dev/null'], stdin=fin, stdout=subprocess.DEVNULL, stderr=subprocess.PIPE, universal_newlines=True, shell=True)

		for j in range(0, jobs):
			ps[j].wait()

			time = float(ps[j].stderr.read())
			print("\t" + str(i+j) + ": ", time)

			ns+=1
			sums+=time

	f.close();

	print("Total time: " + "{0:.5f}".format(sums) + " over " + str(ns) + " runs, avg: " + "{0:.5f}".format(sums/ns) + "\n\n")
	result.write(str(ci) + " " + "{0:.5f}".format(sums/ns) + " " + "{0:.5f}".format(sums/ns / math.log(ci)) + "\n")
	result.flush();

	subprocess.Popen(['pdflatex', 'relatorio.tex'], stdout=subprocess.DEVNULL)

result.close();

folder = './profile_tests/'
for the_file in os.listdir(folder):
    file_path = os.path.join(folder, the_file)
    try:
        if os.path.isfile(file_path):
            os.unlink(file_path)

    except Exception as e:
        print(e)

print("Generating PDF file...")

print("All done!")
