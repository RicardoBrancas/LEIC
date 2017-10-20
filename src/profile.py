import time
from threading import Thread

from multiprocessing import Process

from main import *


def greedy_search(problem, h=None):
	h = memoize(h or problem.h, 'h')
	return best_first_graph_search(problem, h)


problems = [
	[[1, 2, 1, 2, 1], [2, 1, 2, 1, 2], [1, 2, 1, 2, 1], [2, 1, 2, 1, 2]],
	[[1, 2, 2, 3, 3], [2, 2, 2, 1, 3], [1, 2, 2, 2, 2], [1, 1, 1, 1, 1]],
	[[3, 1, 3, 2], [1, 1, 1, 3], [1, 3, 2, 1], [1, 1, 3, 3], [3, 3, 1, 2], [2, 2, 2, 2], [3, 1, 2, 3], [2, 3, 2, 3],
	 [5, 1, 1, 3], [4, 5, 1, 2]],
	[[3, 1, 3, 2], [1, 1, 1, 3], [1, 3, 2, 1], [1, 1, 3, 3], [3, 3, 1, 2], [2, 2, 2, 2], [3, 1, 2, 3], [2, 3, 2, 3],
	 [2, 1, 1, 3], [2, 3, 1, 2]],
	[[1, 1, 5, 3], [5, 3, 5, 3], [1, 2, 5, 4], [5, 2, 1, 4], [5, 3, 5, 1], [5, 3, 4, 4], [5, 5, 2, 5], [1, 1, 3, 1],
	 [1, 2, 1, 3], [3, 3, 5, 5]],
	[[4, 4, 4, 2], [4, 4, 4, 3], [4, 4, 4, 1], [4, 4, 4, 4], [4, 4, 4, 2], [4, 4, 4, 4], [4, 4, 4, 3], [4, 4, 4, 3],
	 [4, 4, 4, 4], [4, 4, 4, 2]],
	[[1, 1, 3, 3, 3, 3], [2, 2, 3, 3, 3, 3], [2, 2, 3, 3, 3, 3], [2, 2, 3, 3, 3, 3], [2, 2, 3, 3, 3, 3]],
	[[3, 3, 0, 3, 3, 3], [2, 2, 0, 3, 3, 3], [2, 2, 0, 3, 3, 3], [2, 2, 1, 3, 3, 3], [2, 2, 1, 3, 3, 3]],
	[[1, 3, 2, 1, 2, 1, 2, 2, 1, 2, 2, 1, 1, 3, 1], [1, 3, 3, 2, 1, 2, 2, 2, 3, 1, 2, 1, 2, 3, 1],
	 [1, 1, 1, 2, 3, 2, 3, 3, 2, 2, 3, 1, 1, 3, 1], [1, 2, 2, 2, 3, 3, 3, 3, 1, 2, 1, 2, 1, 3, 2],
	 [1, 3, 1, 3, 2, 2, 2, 2, 3, 1, 1, 2, 3, 2, 1], [1, 1, 2, 2, 2, 1, 1, 3, 2, 1, 2, 3, 1, 3, 1],
	 [3, 1, 3, 2, 2, 2, 3, 3, 3, 1, 3, 3, 2, 1, 1], [3, 2, 1, 2, 1, 3, 1, 2, 1, 2, 3, 1, 1, 3, 3],
	 [2, 3, 1, 2, 3, 3, 1, 2, 3, 3, 3, 2, 1, 1, 1], [2, 2, 1, 1, 2, 1, 2, 2, 1, 1, 3, 2, 2, 2, 2]],
	[[4, 4, 4, 2, 3, 2, 2, 2, 2, 1, 2, 3, 4, 4, 3], [1, 4, 4, 2, 1, 2, 4, 1, 1, 2, 4, 4, 3, 4, 1],
	 [2, 3, 2, 2, 2, 4, 3, 2, 2, 2, 2, 1, 4, 2, 3], [3, 3, 3, 4, 2, 2, 4, 3, 3, 4, 4, 2, 4, 1, 1],
	 [1, 3, 3, 4, 3, 1, 1, 1, 1, 1, 1, 2, 4, 2, 3], [4, 2, 3, 3, 1, 3, 3, 2, 2, 4, 3, 3, 2, 1, 4],
	 [3, 1, 2, 1, 2, 3, 2, 4, 2, 4, 4, 3, 4, 4, 2], [3, 4, 3, 1, 1, 3, 4, 4, 1, 2, 3, 1, 1, 4, 2],
	 [1, 3, 1, 3, 2, 1, 2, 1, 3, 4, 4, 3, 2, 3, 1], [1, 2, 1, 2, 2, 2, 1, 3, 2, 4, 3, 2, 1, 2, 1]]
]

searches = {
	'DFS': depth_first_tree_search,
	'Greedy': greedy_search,
	'A*': astar_search,
	# 'RBFS': recursive_best_first_search
}


def do_search(name, search, problem):
	start = time.perf_counter()
	search(problem)
	end = time.perf_counter()

	print('\t', name.rjust(8) + ':', problem, "{0:.4f}".format((end - start)))


for i, problem in enumerate(problems):
	print("Running problem", i)

	sg = same_game(problem)

	# if i == 5:
	# 	del searches['DFS']

	processes = []

	for name, search in searches.items():
		if i == 9 and name == 'DFS':
			print('Skipping DFS for problem 9')
			continue

		instrumented = InstrumentedProblem(sg)

		process = Process(target=do_search, args=(name, search, instrumented))
		processes.append(process)
		process.start()

	for process in processes:
		process.join()
