class Dims:
    def __init__(self, width, height):
        self.max_x = -1
        self.min_x = width
        self.max_y = -1
        self.min_y = height

    def mid_x(self):
        return (self.min_x + self.max_x) / 2

    def mid_y(self):
        return (self.min_y + self.max_y) / 2

    def update_dims(self, x, y):
        if x > self.max_x:
            self.max_x = x
        if x < self.min_x:
            self.min_x = x
        if y > self.max_y:
            self.max_y = y
        if y < self.min_y:
            self.min_y = y
