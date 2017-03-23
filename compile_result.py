f = open('results.1')
f1 = open('results.2', "w+")

ns = {}
sums = {}


for line in iter(f):
    parts = line.split(sep=";")
    if int(parts[0]) in ns:
        ns[int(parts[0])] += 1
    else:
        ns[int(parts[0])] = 0
    if int(parts[0]) in sums:
        sums[int(parts[0])] += float(parts[1].rstrip())
    else:
        sums[int(parts[0])] = float(parts[1].rstrip())

for n in sorted(ns):
    f1.write("(" + str(n) + "," + "{0:.6f}".format(sums[n]/ns[n]) + ")")

f.close()
f1.close()
