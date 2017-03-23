pre#!/bin/sh

rm results.1
rm results.2
cd ./profile_tests/

echo "Starting!"

for v in {0..10000000..400000}
do
	for dv in {0..0..1}
	do
		let vi=$((($v+$dv)>5?$v+$dv:5))
				echo "Running ${vi}-1-0"
				for i in {0..5}
				do
					echo "	Run ${i}"
					./gen_p1 ${vi} 1 0 > test_${vi}-1-0.in
					cat test_${vi}-1-0.in | /usr/bin/time -f "${vi};%e" -a -o ../results.1 ../out/main > /dev/null
				done
	done
done

echo "Cleaning up!"
ls | grep -v gen_p1 | parallel  rm

cd ..

python3 compile_result.py
