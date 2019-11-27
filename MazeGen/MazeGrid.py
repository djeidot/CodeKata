from PIL import Image, ImageDraw

CELL_SIZE = 10
BORDER_SIZE = 5

WHITE = (255, 255, 255)
BLACK = (0, 0, 0)
WALL = BLACK

class MazeBlock:
    left = None
    right = None
    up = None
    down = None

    def __init__(self, r, c) -> None:
        self.r = r
        self.c = c

    def __str__(self) -> str:
        return chr(ord('A') + self.r) + str(self.c)

    def directions(self):
        return [self.up, self.right, self.down, self.left]

    def draw(self, image):
        x = BORDER_SIZE + self.r * CELL_SIZE
        y = BORDER_SIZE + self.c * CELL_SIZE
        corner_nw = (x, y)
        corner_ne = (x + CELL_SIZE - 1, y)
        corner_sw = (x, y + CELL_SIZE - 1)
        corner_se = (x + CELL_SIZE - 1, y + CELL_SIZE - 1)

        image.putpixel(corner_nw, WALL)
        image.putpixel(corner_ne, WALL)
        image.putpixel(corner_sw, WALL)
        image.putpixel(corner_se, WALL)

        draw = ImageDraw.Draw(image)
        if self.up is None:
            draw.line([corner_nw, corner_ne], WALL)
        if self.right is None:
            draw.line([corner_ne, corner_se], WALL)
        if self.down is None:
            draw.line([corner_sw, corner_se], WALL)
        if self.left is None:
            draw.line([corner_nw, corner_sw], WALL)

class MazeGrid:
    def __init__(self, width, height):
        self.width = width
        self.height = height

        self.grid = [[MazeBlock(r, c) for c in range(0, width)] for r in range(0, height)]

        for r in range(0, height):
            for c in range (0, width):
                if c < width - 1:
                    self.grid[r][c].right = self.grid[r][c + 1]
                    self.grid[r][c + 1].left = self.grid[r][c]
                if r < height - 1:
                    self.grid[r][c].down = self.grid[r + 1][c]
                    self.grid[r + 1][c].up = self.grid[r][c]

    def draw(self, filename):
        image_width = CELL_SIZE * self.width + BORDER_SIZE * 2
        image_height = CELL_SIZE * self.height + BORDER_SIZE * 2
        
        image = Image.new('RGB', (image_width, image_height), WHITE)
        draw = ImageDraw.Draw(image)

        draw.line([(BORDER_SIZE - 1, BORDER_SIZE - 1), (image_width - BORDER_SIZE, BORDER_SIZE - 1)], WALL)
        draw.line([(BORDER_SIZE - 1, BORDER_SIZE - 1), (BORDER_SIZE - 1, image_height - BORDER_SIZE)], WALL)
        draw.line([(image_width - BORDER_SIZE, BORDER_SIZE - 1), (image_width - BORDER_SIZE, image_height - BORDER_SIZE)], WALL)
        draw.line([(BORDER_SIZE - 1, image_height - BORDER_SIZE), (image_width - BORDER_SIZE, image_height - BORDER_SIZE)], WALL)

        for r in range(0, self.height):
            for c in range (0, self.width):
                self.grid[r][c].draw(image)

        image.save(filename, "PNG")