import search


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


# TAI Board

def show_board(board):
    for i in range(len(board)):
        print(board[i])


def copy_board(board):
    newboard = []
    for i in range(len(board)):
        newboard.append(board[i])
    return newboard


def board_find_groups(board):
    groups = []
    aux = []
    for i in range(len(board)):
        aux.append([False] * len(board[0]))
    for i in range(len(board)):
        for j in range(len(board[0])):
            if not aux[i][j]:
                color = board[i][j]
                group = []
                has_next(color, aux, i, j, group, board)
                groups.append(group)
    print(groups)


def has_next(color, aux, i, j, group, board):
    if i < len(board) and j < len(board[0]) and i >= 0 and j >= 0:
        if board[i][j] == color and not aux[i][j]:
            add_to_group(group, (i, j))
            aux[i][j] = True
            has_next(color, aux, i - 1, j, group, board)
            has_next(color, aux, i + 1, j, group, board)
            has_next(color, aux, i, j + 1, group, board)
            has_next(color, aux, i, j - 1, group, board)
    else:
        return


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

    # faz shift para cima das colunas
    for col, maxLine in columns.items():
        aux = 0
        for i in reversed(range(0, maxLine + 1)):
            if no_color(newboard[i][col]):
                aux = aux + 1
            elif color(newboard[i][col]):
                newboard[i + aux][col] = newboard[i][col]
        for j in range(0, aux):
            newboard[j][col] = get_no_color()
    # verifica se ha colunas vazias, se houver faz shift
    for col in reversed(range(len(newboard[0]))):
        empty = False
        l = 0
        while not empty and l < len(newboard):
            if color(newboard[l][col]):
                empty = True
            l = l + 1
        if not empty:
            for i in range(len(newboard)):
                newboard[i].pop(col)
                newboard[i].append(get_no_color())
    return newboard


class sg_state(object):
    var = 0

    def __init__(self, board):
        self.board = board
        self.order = self.var
        self.var = self.var + 1

    def __lt__(self, state):
        return self.order <= state.order
