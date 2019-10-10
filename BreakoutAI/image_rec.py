from pprint import pprint

from PIL import Image

class ImageRec:
    
    COLOR_BRICK = (255, 0, 0)
    COLOR_BALL = (0, 255, 0)
    COLOR_PADDLE = (0, 0, 255)

    def __init__(self, image_bytes) -> None:
        super().__init__()
        self.image = Image.open(image_bytes)
        self.analyse()
    
    def get_data(self):
        return self.has_bricks, self.ball_x, self.paddle_x
        
    def analyse(self):
        (self.width, self.height) = self.image.size
        image_data = list(self.image.getdata())
        
        max_ball_x = -1
        min_ball_x = self.width
        max_paddle_x = -1
        min_paddle_x = self.width
        self.has_bricks = False
        for y in range(0, self.height):
            for x in range(0, self.width):
                byte_color = image_data[y * self.width + x]
                if byte_color == self.COLOR_BRICK:
                    self.has_bricks = True
                elif byte_color == self.COLOR_BALL:
                    if x > max_ball_x:
                        max_ball_x = x
                    if x < min_ball_x:
                        min_ball_x = x
                elif byte_color == self.COLOR_PADDLE:
                    if x > max_paddle_x:
                        max_paddle_x = x
                    if x < min_paddle_x:
                        min_paddle_x = x
        
        self.ball_x = (min_ball_x + max_ball_x) / 2
        self.paddle_x = (min_paddle_x + max_paddle_x) / 2
        print("Image: (", self.width, ", ", self.height, "), Ball X: ", self.ball_x, " (", min_ball_x, "..", max_ball_x, "); Paddle X: ", self.paddle_x, " (", min_paddle_x, "..", max_paddle_x, ")")