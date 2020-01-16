import json
import random

from Minesweeper.atomicfile import InputAtomicFile, OutputAtomicFile

game_file = "../../codekata-minesweeper/game.json"
command_file = "../../codekata-minesweeper/command.json"
mines = set()

def main():
    status = "playing"
    board = read_mine_file()
    print_board(board)
    while status == "playing":
        find_mines(board)
        moves = get_moves(board)
        write_move_file(moves)
        board = read_mine_file()
        print_board(board)
        if (len(mines) == board["nmines"]):
            status = "Finished"

def read_mine_file():
    with InputAtomicFile(game_file) as handle:
        board = json.load(handle)

    board["nrows"] = len(board["grid"])
    board["ncols"] = len(board["grid"][0])
    return board

def print_board(board):
    # {
    # "status": game_status,
    # "grid": [ [cell, ...], [cell, ...], ...],
    # "nmines": integer
    # }
    for r in range(0, board["nrows"]):
        for c in range (0, board["ncols"]):
            if (r, c) in mines:
                print("#", end = "")
            else:
                print(board["grid"][r][c], end="")
        print()
    print()


def get_moves(board):
    moves = []
    moves.extend(find_blanks(board))

    if moves == []:
        row = random.randint(0, board["nrows"])
        col = random.randint(0, board["ncols"])
        moves.append(position(row, col))

    return moves


def find_mines(board):
    for r in range(0, board["nrows"]):
        for c in range (0, board["ncols"]):
            if board["grid"][r][c] != "?" and board["grid"][r][c] != "#":
                nbors = get_nbors(r, c, board)
                unknowns = get_unknown_nbors(nbors, board)
                if len(unknowns) > 0:
                    nbor_mines = set(get_mines_in_nbors(nbors))
                    nbor_mines.update(unknowns)
                    if len(nbor_mines) == board["grid"][r][c]:
                        mines.update(nbor_mines)
                        for (r, c) in mines:
                            board["grid"][r][c] = "#"


def find_blanks(board):
    moveset = set()
    for r in range(0, board["nrows"]):
        for c in range (0, board["ncols"]):
            if board["grid"][r][c] != "?" and board["grid"][r][c] != "#":
                nbors = get_nbors(r, c, board)
                mines = get_mines_in_nbors(nbors)
                if board["grid"][r][c] == len(mines):
                    moveset.update(get_unknown_nbors(nbors, board))

    return list(map(lambda cell: position(cell[0], cell[1]), moveset))


def get_nbors(r, c, board):
    return [(i, j) for i in range(r - 1, r + 2) for j in range(c - 1, c + 2) if i >= 0 and i < board["nrows"] and j >= 0 and j < board["ncols"] and not (i == r and j == c)]


def get_mines_in_nbors(nbors):
    return [(i, j) for (i, j) in nbors if (i, j) in mines]


def get_unknown_nbors(nbors, board):
    return [(i, j) for (i, j) in nbors if board["grid"][i][j] == "?"]

def position(r, c):
    return {"x": c, "y": r}


def write_move_file(moves):
    #{
    #"move": [position, ...]
    #}
    # {
    #     "x": int,
    #     "y": int
    # }
    command = {"move": moves}

    with OutputAtomicFile(command_file) as handle:
        json.dump(command, handle)


if __name__ == '__main__':
    main()