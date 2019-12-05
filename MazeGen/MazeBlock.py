from PIL import ImageDraw

from MazeGen.constants import CELL_SIZE, BORDER_SIZE, WALL, UP, DOWN, LEFT, RIGHT, FREE, SHADOW, SHADOW_WALL


class MazeBlock:
    
    def __init__(self, r, c) -> None:
        self.r = r
        self.c = c
        self.nbors = [None, None, None, None]
        self.walls = [False, False, False, False]
        self.shadow_walls = [False, False, False, False]
        self.picked = False
        self.shadow = False

    def __str__(self) -> str:
        if self.r == -1 and self.c == -1:
            return "EDGE"
        
        return chr(ord('A') + self.r) + str(self.c)

    def draw(self, image):
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

EDGE = MazeBlock(-1, -1)