from datetime import datetime
from pprint import pprint

from PIL import Image

from dims import Dims


class ImageRec:

    COLOR_BRICK = (255, 0, 0)
    COLOR_BALL = (0, 255, 0)
    COLOR_PADDLE = (0, 0, 255)

    def __init__(self, image_bytes) -> None:
        super().__init__()
        self.image = Image.open(image_bytes)
        #self.save_image()
        self.analyse()

    def save_image(self):
        image_filename = datetime.now().strftime("images/%Y_%m_%d_%H_%M_%S_%f.png")
        self.image.save(image_filename, "PNG")

    def get_data(self):
        return self.has_bricks, self.ball_dims, self.paddle_dims
        
    def analyse(self):
        (self.width, self.height) = self.image.size
        image_data = list(self.image.getdata())
        
        self.ball_dims = Dims(self.width, self.height)
        self.paddle_dims = Dims(self.width, self.height)
        
        self.has_bricks = False
        for y in range(0, self.height):
            for x in range(0, self.width):
                byte_color = image_data[y * self.width + x]
                if byte_color == self.COLOR_BRICK:
                    self.has_bricks = True
                elif byte_color == self.COLOR_BALL:
                    self.ball_dims.update_dims(x, y)
                elif byte_color == self.COLOR_PADDLE:
                    self.paddle_dims.update_dims(x, y)
