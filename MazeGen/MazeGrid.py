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