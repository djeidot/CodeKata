
def interp(x, x0, x1, y0, y1):
    return (x - x0) / (x1 - x0) * (y1 - y0) + y0

class PredictiveAI:
    prev_ball = None
    prev_paddle = None

    def get_command(self, ball, paddle):
        ball_x_predict = 0
        if not (self.prev_ball and self.prev_paddle):
            command = self._follow_ball(ball.mid_x(), paddle)
        else:
            ball_dir_y = ball.mid_y() - self.prev_ball.mid_y()
            if ball_dir_y <= 0:
                # if ball is going up, just follow the ball
                command = self._follow_ball(ball.mid_x(), paddle)
            else:
                # otherwise, try to predict where the ball will be when it reaches the paddle (via interpolation)
                ball_x_predict = interp(
                    paddle.min_y, ball.mid_y(), self.prev_ball.mid_y(), ball.mid_x(), self.prev_ball.mid_x()
                )
                command = self._follow_ball(ball_x_predict, paddle)

        print("Ball: (", ball.mid_x(), ", ", ball.mid_y(), "); Paddle: (", paddle.mid_x(), ", ", paddle.mid_y(), "), Ball X predict: ", ball_x_predict)

        self.prev_ball = ball
        self.prev_paddle = paddle
        return command

    def _follow_ball(self, ball_x, paddle):
        if ball_x < paddle.min_x:
            return "L"
        elif ball_x > paddle.max_x:
            return "R"
        else:
            return "."

