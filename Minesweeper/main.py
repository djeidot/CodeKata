import json
import random

from Minesweeper.atomicfile import InputAtomicFile, OutputAtomicFile

game_file = "../../codekata-minesweeper/game.json"
command_file = "../../codekata-minesweeper/command.json"

def main():
    status = "playing"
    board = read_mine_file()
    print_board(board)
    while status == "playing":
        moves = get_moves(board)
        write_move_file(moves)
        board = read_mine_file()
        print_board(board)

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
    for row in board["grid"]:
        for cell in row:
            print(cell, end="")
        print()
    print()

def get_moves(board):
    moves = []
    moves.extend(find_zeroes(board))

    if moves == []:
        row = random.randint(0, board["nrows"])
        col = random.randint(0, board["ncols"])
        moves.append(position(row, col))

    return moves

def find_zeroes(board):
    moveset = set()
    for r in range(0, board["nrows"]):
        for c in range (0, board["ncols"]):
            if board["grid"][r][c] == 0:
                add_nbors(moveset, r, c, board)

    return list(map(lambda cell: position(cell[0], cell[1]), moveset))

def add_nbors(moveset, r, c, board):
    for i in range(r - 1, r + 2):
        for j in range(c - 1, c + 2):
            if i >= 0 and i < board["ncols"] and j >= 0 and j < board["nrows"] and not (i == r and j == c) and board["grid"][i][j] == "?":
                moveset.add((i, j))

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