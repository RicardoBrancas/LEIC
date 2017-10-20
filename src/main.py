from search import *


# TAI color
# sem cor = 0
# com cor > 0
def get_no_color():
	return 0


def no_color(c):
	return c == 0


def color(c):
	return c > 0


# TAI pos
# Tuplo (l, c)
def make_pos(l, c):
	return (l, c)


def pos_l(pos):
	return pos[0]


def pos_c(pos):
	return pos[1]


# TAI group

def create_group(pos):
	group = [pos]
	return group


def add_to_group(group, pos):
	group.append(pos)


def group_size(group):
	return len(group)


# TAI Board

def show_board(board):
	for i in range(len(board)):
		print(board[i])


def copy_board(board):
	newboard = []
	for i in range(len(board)):
		newboard.append(list(board[i]))
	return newboard


def board_empty(board):
	return no_color(board[len(board)-1][0])


def board_find_groups(board):
	groups = []
	visited = []
	for i in range(len(board)):
		visited.append([False] * len(board[0]))
	for i in range(len(board)):
		for j in range(len(board[0])):
			if color(board[i][j]) and not visited[i][j]:
				c = board[i][j]
				group = []
				has_next(c, visited, i, j, group, board)
				groups.append(group)

	return groups


def has_next(color, visited, i, j, group, board):
	if (i < len(board)) and (j < len(board[0])) and (i >= 0) and (j >= 0):
		if board[i][j] == color and not visited[i][j]:
			add_to_group(group, (i, j))
			visited[i][j] = True
			has_next(color, visited, i - 1, j, group, board)
			has_next(color, visited, i + 1, j, group, board)
			has_next(color, visited, i, j + 1, group, board)
			has_next(color, visited, i, j - 1, group, board)


def board_remove_group(board, group):
	columns = {}
	newboard = copy_board(board)

	# vai por o grupo no tabuleiro a 0 e criar um dicionario com a colun e a linha maxima
	for pos in group:
		newboard[pos_l(pos)][pos_c(pos)] = get_no_color()
		if pos_c(pos) not in columns:
			columns[pos_c(pos)] = pos_l(pos)
		else:
			columns[pos_c(pos)] = max(columns[pos_c(pos)], pos_l(pos))

	# vertical compression
	for col, maxLine in columns.items():
		zeroes_found = 0
		for i in reversed(range(0, maxLine + 1)):
			if no_color(newboard[i][col]):
				zeroes_found = zeroes_found + 1

			elif color(newboard[i][col]):
				newboard[i + zeroes_found][col] = newboard[i][col]

		for j in range(0, zeroes_found):
			newboard[j][col] = get_no_color()

	# horizontal compression
	for col in reversed(range(len(newboard[0]))):
		empty = True
		line = 0
		while empty and line < len(newboard):
			if color(newboard[line][col]):
				empty = False
			line = line + 1
		if empty:
			for i in range(len(newboard)):
				newboard[i].pop(col)
				newboard[i].append(get_no_color())
	return newboard


class sg_state(object):
	id = 0

	def __init__(self, board):
		self.board = board
		self.order = sg_state.id
		sg_state.id += 1

	def __lt__(self, state):
		return self.order < state.order


class same_game(Problem):
	def __init__(self, board):
		super().__init__(sg_state(board))

	def actions(self, state: sg_state):
		actions = []
		for group in board_find_groups(state.board):
			if group_size(group) >= 2:
				actions += [group]

		return actions

	def result(self, state, action):
		new_board = board_remove_group(state.board, action)
		return sg_state(new_board)

	def goal_test(self, state):
		return board_empty(state.board)

	def path_cost(self, c, state1, action, state2):
		return c + 1

	def h(self, node):
		h = 0

		for group in board_find_groups(node.state.board):
			if group_size(group) >= 2:
				h += 1
			else:
				h += 3

		return h
