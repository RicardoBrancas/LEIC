pre#!/bin/sh

rm results.csv
cd ./profile_tests/

echo "Starting!"

for v in {5..100000..125}
do
	for e in {5..5}
	do
		for r in {0..0}
		do
			echo "Running ${v}-${e}-${r}" &
			for i in {0..15}
			do
				echo "	Run ${i}"
				./gen_p1 ${v} ${e} ${r} > test_${v}-${e}-${r}.in
				cat test_${v}-${e}-${r}.in | /usr/bin/time -f "${v};%e;%M" -a -o ../results.csv ../out/main > /dev/null
			done
		done
	done
done

wait

echo "Cleaning up!"
ls | grep -v gen_p1 | parallel  rm
