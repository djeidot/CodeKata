from copy import copy

import pygame
from MazeGrid import MazeGrid

from MazeGen.constants import WHITE


def main():
    maze_grid = MazeGrid(40, 40)

    pygame.init()
    pygame.display.set_caption("Battleship")
    
    screen = pygame.display.set_mode((maze_grid.screen_width, maze_grid.screen_height))
    
    maze_grid.make_maze(screen)
    maze_grid.solve_maze(screen, (0, 0), (39, 39))

    running = True
    while running:
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                running = False
                
if __name__ == '__main__':
    main()