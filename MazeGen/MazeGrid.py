import random
import time
from functools import reduce

import pygame
from PIL import Image, ImageDraw

from MazeGen.MazeBlock import MazeBlock, EDGE
from MazeGen.constants import CELL_SIZE, BORDER_SIZE, WHITE, WALL, UP, DOWN, LEFT, RIGHT


class MazeGrid:
    def __init__(self, width, height):
        self.width = width
        self.height = height
        self.screen_width = CELL_SIZE * self.width + BORDER_SIZE * 2
        self.screen_height = CELL_SIZE * self.height + BORDER_SIZE * 2

        self.grid = [[MazeBlock(r, c) for c in range(0, width)] for r in range(0, height)]

        for r in range(0, height):
            for c in range(0, width):
                self.grid[r][c].nbors[UP] = EDGE if r == 0 else self.grid[r - 1][c]
                if c == width - 1:
                    self.grid[r][c].nbors[RIGHT] = EDGE
                else:
                    self.grid[r][c].nbors[RIGHT] = self.grid[r][c + 1]
                self.grid[r][c].nbors[DOWN] = EDGE if r == height - 1 else self.grid[r + 1][c]
                self.grid[r][c].nbors[LEFT] = EDGE if c == 0 else self.grid[r][c - 1]

        for r in range(0, height):
            for c in range(0, width):
                for d in range(0, 4):
                    self.grid[r][c].walls[d] = (self.grid[r][c].nbors[d] == EDGE)

        self.picked_blocks = []
        self.remaining_blocks = [self.grid[r][c] for r in range(0, height) for c in range(0, width)]

    def draw_image(self, filename):
        image = Image.new('RGB', (self.screen_width, self.screen_height), WHITE)
        draw = ImageDraw.Draw(image)

        draw.line([(BORDER_SIZE - 1, BORDER_SIZE - 1), (self.screen_width - BORDER_SIZE, BORDER_SIZE - 1)], WALL)
        draw.line([(BORDER_SIZE - 1, BORDER_SIZE - 1), (BORDER_SIZE - 1, self.screen_height - BORDER_SIZE)], WALL)
        draw.line([(self.screen_width - BORDER_SIZE, BORDER_SIZE - 1), (self.screen_width - BORDER_SIZE, self.screen_height - BORDER_SIZE)],
                  WALL)
        draw.line([(BORDER_SIZE - 1, self.screen_height - BORDER_SIZE), (self.screen_width - BORDER_SIZE, self.screen_height - BORDER_SIZE)],
                  WALL)

        for r in range(0, self.height):
            for c in range (0, self.width):
                self.grid[r][c].draw_image(image)

        image.save(filename, "PNG")

    def draw_screen(self, screen):
        screen.fill(WHITE)
        pygame.draw.line(screen, WALL, (BORDER_SIZE - 1, BORDER_SIZE - 1), (self.screen_width - BORDER_SIZE, BORDER_SIZE - 1))
        pygame.draw.line(screen, WALL, (BORDER_SIZE - 1, BORDER_SIZE - 1), (BORDER_SIZE - 1, self.screen_height - BORDER_SIZE))
        pygame.draw.line(screen, WALL, (self.screen_width - BORDER_SIZE, BORDER_SIZE - 1), (self.screen_width - BORDER_SIZE, self.screen_height - BORDER_SIZE))
        pygame.draw.line(screen, WALL, (BORDER_SIZE - 1, self.screen_height - BORDER_SIZE), (self.screen_width - BORDER_SIZE, self.screen_height - BORDER_SIZE))

        for r in range(0, self.height):
            for c in range (0, self.width):
                self.grid[r][c].draw_screen(screen)

        pygame.display.flip()

    def _setWall(self, block, neighbour, up, shadow=False):
        for i in range(0, 4):
            if block.nbors[i] == neighbour:
                if shadow:
                    block.shadow_walls[i] = up
                else:
                    block.walls[i] = up

            if neighbour.nbors[i] == block:
                if shadow:
                    neighbour.shadow_walls[i] = up
                else:
                    neighbour.walls[i] = up

    def buildShadowWall(self, block, neighbour):
        self._setWall(block, neighbour, True, shadow=True)

    def removeShadowWalls(self, block, turnToReal):
        for i in range(0, 4):
            if block.shadow_walls[i]:
                block.shadow_walls[i] = False
                if turnToReal:
                    block.walls[i] = True

    def breakWall(self, block, neighbour):
        self._setWall(block, neighbour, False, True)
        self._setWall(block, neighbour, False, False)

    def find_path(self, start: MazeBlock, end: bool):
        self.blocks_in_path = [start]
        it_block = start
        while True:
            # create array of neighbors
            nbor_directions = list(filter(lambda dir: it_block.nbors[dir] != EDGE and it_block.nbors[dir] not in self.blocks_in_path, range(0, 4)))
            # return if no neighbours available - got yourself cornered
            if len(nbor_directions) == 0:
                # set as shadow and discard this path if not looking for an exit
                for block in self.blocks_in_path:
                    if not end:
                        block.shadow = True
                    else:
                        block.picked = True
                        self.removeShadowWalls(block, turnToReal=True)
                        self.remaining_blocks.remove(block)
                return
            # pick one of the neighbours
            picked_nbor_direction = random.choice(nbor_directions)
            picked_neighbour = it_block.nbors[picked_nbor_direction]
            # close borders with all other neighbours
            for dir in nbor_directions:
                if dir != picked_nbor_direction:
                    self.buildShadowWall(it_block, it_block.nbors[dir])
            # add neighbour to blocks in path
            self.blocks_in_path.append(picked_neighbour)
            # if joined main thread or reached the end, stop here
            if picked_neighbour.picked:
                self.breakWall(it_block, picked_neighbour)
                for block in self.blocks_in_path:
                    block.picked = True
                    self.removeShadowWalls(block, turnToReal=True)
                    if block in self.remaining_blocks:
                        self.remaining_blocks.remove(block)
                return
            # otherwise, set neighbour to it_block and iterate
            it_block = picked_neighbour

    def clear_shadow_blocks(self):
        for it_block in self.blocks_in_path:
            if it_block.shadow:
                it_block.shadow = False
                self.removeShadowWalls(it_block, turnToReal=False)
                for nbor in it_block.nbors:
                    self.removeShadowWalls(nbor, turnToReal=False)

    def make_maze(self, screen):
        self.find_path(self.grid[int(self.height / 2)][int(self.width / 2)], True)

        while len(self.remaining_blocks) > 0:
            self.clear_shadow_blocks()

            random_start_block = random.choice(self.remaining_blocks)
            self.find_path(random_start_block, False)

            # self.draw_screen(screen)
            # time.sleep(0.1)

        self.draw_image("image.png")
        self.draw_screen(screen)
        
    def toggle_path(self, block, neighbour):
        for i in range(0, 4):
            if block.nbors[i] == neighbour:
                block.paths[i] = not block.paths[i]

            if neighbour.nbors[i] == block:
                neighbour.paths[i] = not neighbour.paths[i]
        
        
    def dist(self, block1, block2):
        return (abs(block1.r - block2.r) + abs(block1.c - block2.c)) * 10
        
    def solve_maze(self, screen, start, end):
        start_block = self.grid[start[0]][start[1]]
        end_block = self.grid[end[0]][end[1]]

        dir = 0
        it_block = start_block
        as_block = start_block
        as_block.open = True
        openset = [as_block]
        
        while it_block != end_block or as_block != end_block:
            if it_block != end_block:
                it_block = end_block    #skip
                
                # Iterate normal
                # dir = (dir + 3) % 4
                # while it_block.walls[dir] or it_block.nbors[dir] == EDGE:
                #     dir = (dir + 1) % 4
                # 
                # self.toggle_path(it_block, it_block.nbors[dir])
                # it_block = it_block.nbors[dir]
            
            if as_block != end_block:
                # Iterate A-star
                as_block = openset[0] if len(openset) == 1 else reduce(lambda a, b: a if a.f_cost() < b.f_cost() or (a.f_cost() == b.f_cost and a.h_cost < b.h_cost) else b, openset[1:], openset[0])
                as_block.open = False
                as_block.closed = True
                openset.remove(as_block)

                if as_block == end_block:
                    continue

                for i_dir in range(0, 4):
                    nbor = as_block.nbors[i_dir]
                    if as_block.walls[i_dir] or nbor == EDGE or nbor.closed:
                        continue

                    nbor_g_cost = as_block.g_cost + 10
                    if not nbor.open or nbor_g_cost < nbor.g_cost:
                        nbor.g_cost = nbor_g_cost
                        nbor.h_cost = self.dist(nbor, end_block)
                        nbor.a_star_parent = as_block
                        nbor.open = True
                        openset.append(nbor)

            self.draw_screen(screen)

        as_block = end_block
        while as_block != start_block:
            self.toggle_path(as_block, as_block.a_star_parent)
            as_block = as_block.a_star_parent
            self.draw_screen(screen)
        