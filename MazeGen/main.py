from copy import copy
from MazeGrid import MazeGrid


def main():
    maze_grid = MazeGrid(18, 18)

    maze_grid.find_path(maze_grid.get_block(0, 0), maze_grid.get_block(17, 17))
    
    maze_grid.draw("image.png")




if __name__ == '__main__':
    main()