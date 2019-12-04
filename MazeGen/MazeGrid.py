import random

from PIL import Image, ImageDraw

from MazeGen.MazeBlock import MazeBlock, EDGE
from MazeGen.constants import CELL_SIZE, BORDER_SIZE, WHITE, WALL, UP, DOWN, LEFT, RIGHT


class MazeGrid:
    def __init__(self, width, height):
        self.width = width
        self.height = height

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

    def draw(self, filename):
        image_width = CELL_SIZE * self.width + BORDER_SIZE * 2
        image_height = CELL_SIZE * self.height + BORDER_SIZE * 2

        image = Image.new('RGB', (image_width, image_height), WHITE)
        draw = ImageDraw.Draw(image)

        draw.line([(BORDER_SIZE - 1, BORDER_SIZE - 1), (image_width - BORDER_SIZE, BORDER_SIZE - 1)], WALL)
        draw.line([(BORDER_SIZE - 1, BORDER_SIZE - 1), (BORDER_SIZE - 1, image_height - BORDER_SIZE)], WALL)
        draw.line([(image_width - BORDER_SIZE, BORDER_SIZE - 1), (image_width - BORDER_SIZE, image_height - BORDER_SIZE)],
                  WALL)
        draw.line([(BORDER_SIZE - 1, image_height - BORDER_SIZE), (image_width - BORDER_SIZE, image_height - BORDER_SIZE)],
                  WALL)

        for r in range(0, self.height):
            for c in range (0, self.width):
                self.grid[r][c].draw(image)

        image.save(filename, "PNG")

    def buildWall(self, block, neighbour):
        for i in range(0, 4):
            if block.nbors[i] == neighbour:
                block.walls[i] = True

            if neighbour.nbors[i] == block:
                neighbour.walls[i] = True
    
    def get_block(self, r, c):
        return self.grid[r][c]
    
    def find_path(self, start: MazeBlock, end: MazeBlock = None):
        # if start is already picked, return
        if start.picked:
            return
        
        start.picked = True
        it_block = start
        while True:
            # create array of unpicked neighbor
            nbor_directions = list(filter(lambda dir: not it_block.walls[dir] and not it_block.nbors[dir].picked, range(0, 4)))
            # return if no neighbours available - got yourself cornered
            if len(nbor_directions) == 0:
                return
            # pick one of the neighbours
            picked_nbor_direction = random.choice(nbor_directions)
            picked_neighbour = it_block.nbors[picked_nbor_direction]
            # close borders with all other neighbours
            for dir in nbor_directions:
                if dir != picked_nbor_direction:
                    self.buildWall(it_block, it_block.nbors[dir])
            # add neighbour to list of picked blocks
            picked_neighbour.picked = True
            # if neighbour is end, stop here
            if picked_neighbour == end:
                return
            # otherwise, set neighbour to it_block and iterate
            it_block = picked_neighbour
