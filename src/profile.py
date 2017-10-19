import itertools

import time

from main import *


def greedy_search(problem, h=None):
	h = memoize(h or problem.h, 'h')
	return best_first_graph_search(problem, h)


problems = {
	1: [
		[1, 2, 1, 2, 1],
		[2, 1, 2, 1, 2],
		[1, 2, 1, 2, 1],
		[2, 1, 2, 1, 2]
	],
	2: [
		[1, 2, 2, 3, 3],
		[2, 2, 2, 1, 3],
		[1, 2, 2, 2, 2],
		[1, 1, 1, 1, 1]
	],
	3: [
		[3, 1, 3, 2],
		[1, 1, 1, 3],
		[1, 3, 2, 1],
		[1, 1, 3, 3],
		[3, 3, 1, 2],
		[2, 2, 2, 2],
		[3, 1, 2, 3],
		[2, 3, 2, 3],
		[5, 1, 1, 3],
		[4, 5, 1, 2]
	],
	4: [
		[3, 1, 3, 2],
		[1, 1, 1, 3],
		[1, 3, 2, 1],
		[1, 1, 3, 3],
		[3, 3, 1, 2],
		[2, 2, 2, 2],
		[3, 1, 2, 3],
		[2, 3, 2, 3],
		[2, 1, 1, 3],
		[2, 3, 1, 2]
	],
	5: [
		[1, 1, 5, 3],
		[5, 3, 5, 3],
		[1, 2, 5, 4],
		[5, 2, 1, 4],
		[5, 3, 5, 1],
		[5, 3, 4, 4],
		[5, 5, 2, 5],
		[1, 1, 3, 1],
		[1, 2, 1, 3],
		[3, 3, 5, 5]
	],
	# 6: [
	# 	[4, 4, 4, 2, 3, 2, 2, 2, 2, 1, 2, 3, 4, 4, 3],
	# 	[1, 4, 4, 2, 1, 2, 4, 1, 1, 2, 4, 4, 3, 4, 1],
	# 	[2, 3, 2, 2, 2, 4, 3, 2, 2, 2, 2, 1, 4, 2, 3],
	# 	[3, 3, 3, 4, 2, 2, 4, 3, 3, 4, 4, 2, 4, 1, 1],
	# 	[1, 3, 3, 4, 3, 1, 1, 1, 1, 1, 1, 2, 4, 2, 3],
	# 	[4, 2, 3, 3, 1, 3, 3, 2, 2, 4, 3, 3, 2, 1, 4],
	# 	[3, 1, 2, 1, 2, 3, 2, 4, 2, 4, 4, 3, 4, 4, 2],
	# 	[3, 4, 3, 1, 1, 3, 4, 4, 1, 2, 3, 1, 1, 4, 2],
	# 	[1, 3, 1, 3, 2, 1, 2, 1, 3, 4, 4, 3, 2, 3, 1],
	# 	[1, 2, 1, 2, 2, 2, 1, 3, 2, 4, 3, 2, 1, 2, 1]
	# ]
}

searches = {
	'DFS': depth_first_tree_search,
	'Greedy': greedy_search,
	'A*': astar_search
}

for i, problem in problems.items():
	print("Running problem", i)

	sg = same_game(problem)

	for name, search in searches.items():
		instrumented = InstrumentedProblem(sg)

		print('\t' + name + ':', end=' ')
		start = time.perf_counter()
		search(instrumented)
		end = time.perf_counter()
		print(instrumented, "{:.2e}".format((end-start)))
