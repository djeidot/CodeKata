import pygame
from PIL import ImageDraw

from MazeGen.constants import CELL_SIZE, BORDER_SIZE, WALL, UP, DOWN, LEFT, RIGHT, FREE, SHADOW, SHADOW_WALL, PATH, OPEN, CLOSED


class MazeBlock:

    def __init__(self, r, c) -> None:
        self.r = r
        self.c = c
        self.nbors = [None, None, None, None]
        self.walls = [False, False, False, False]
        self.shadow_walls = [False, False, False, False]
        self.paths = [False, False, False, False]
        self.picked = False
        self.shadow = False
        self.h_cost = 0
        self.g_cost = 0
        self.a_star_parent = None
        self.open = False
        self.closed = False

    def __str__(self) -> str:
        if self.r == -1 and self.c == -1:
            return "EDGE"

        return chr(ord('A') + self.r) + str(self.c)

    def draw_image(self, image):
        y = BORDER_SIZE + self.r * CELL_SIZE
        x = BORDER_SIZE + self.c * CELL_SIZE
        corner_nw = (x, y)
        corner_ne = (x + CELL_SIZE - 1, y)
        corner_sw = (x, y + CELL_SIZE - 1)
        corner_se = (x + CELL_SIZE - 1, y + CELL_SIZE - 1)

        draw = ImageDraw.Draw(image)

        if self.shadow:
            draw.rectangle([corner_nw, corner_se], SHADOW)
        elif not self.picked:
            draw.rectangle([corner_nw, corner_se], FREE)

        image.putpixel(corner_nw, WALL)
        image.putpixel(corner_ne, WALL)
        image.putpixel(corner_sw, WALL)
        image.putpixel(corner_se, WALL)

        edges = {UP: [corner_nw, corner_ne],
                 RIGHT: [corner_ne, corner_se],
                 DOWN: [corner_sw, corner_se],
                 LEFT: [corner_nw, corner_sw]}

        for d in range(0, 4):
            if self.walls[d]:
                draw.line(edges[d], WALL)
            elif self.shadow_walls[d]:
                draw.line(edges[d], SHADOW_WALL)

    def draw_screen(self, screen):
        y = BORDER_SIZE + self.r * CELL_SIZE
        x = BORDER_SIZE + self.c * CELL_SIZE
        corner_nw = (x, y)
        corner_ne = (x + CELL_SIZE - 1, y)
        corner_sw = (x, y + CELL_SIZE - 1)
        corner_se = (x + CELL_SIZE - 1, y + CELL_SIZE - 1)
        center = (x + (CELL_SIZE - 1) / 2, y + (CELL_SIZE - 1) / 2)

        if self.shadow:
            pygame.draw.rect(screen, SHADOW, (corner_nw[0], corner_nw[1], corner_se[0] - corner_nw[0], corner_se[1] - corner_nw[1]))
        elif self.closed:
            pygame.draw.rect(screen, CLOSED, (corner_nw[0], corner_nw[1], corner_se[0] - corner_nw[0], corner_se[1] - corner_nw[1]))
        elif self.open:
            pygame.draw.rect(screen, OPEN, (corner_nw[0], corner_nw[1], corner_se[0] - corner_nw[0], corner_se[1] - corner_nw[1]))
        elif not self.picked:
            pygame.draw.rect(screen, FREE, (corner_nw[0], corner_nw[1], corner_se[0] - corner_nw[0], corner_se[1] - corner_nw[1]))

        screen.set_at(corner_nw, WALL)
        screen.set_at(corner_ne, WALL)
        screen.set_at(corner_sw, WALL)
        screen.set_at(corner_se, WALL)

        edges = {UP: [corner_nw, corner_ne],
                 RIGHT: [corner_ne, corner_se],
                 DOWN: [corner_sw, corner_se],
                 LEFT: [corner_nw, corner_sw]}
        
        sides = {UP: (center[0], corner_nw[1]),
                 RIGHT: (corner_se[0], center[1]),
                 DOWN: (center[0], corner_se[1]),
                 LEFT: (corner_nw[0], center[1])}

        for d in range(0, 4):
            if self.walls[d]:
                pygame.draw.line(screen, WALL, edges[d][0], edges[d][1])
            elif self.shadow_walls[d]:
                pygame.draw.line(screen, SHADOW_WALL, edges[d][0], edges[d][1])

            if self.paths[d]:
                pygame.draw.line(screen, PATH, center, sides[d], 2)

    def f_cost(self):
        return self.g_cost + self.h_cost
    
EDGE = MazeBlock(-1, -1)
