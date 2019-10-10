# https://confluence.illumina.com/display/KATA/Breakout%3A+Image+recognition+gameplay
from argparse import ArgumentParser

from image_rec import ImageRec
from socket_helper import SocketHelper


def parse_args():
    parser = ArgumentParser()
    parser.add_argument('-H', '--host', type=str, metavar='HOST',
                        help="Listen on HOST. Default %(default)s")
    parser.add_argument('-P', '--port', type=int, metavar='PORT',
                        help="Listen on PORT. Default %(default)s")
    parser.add_argument('-f', '--format', choices=['bmp', 'png'], metavar='TYPE',
                        help="Serve images as TYPE. Default %(default)s")
    parser.add_argument('-s', '--speed', type=int, choices=range(1, 20),
                        help="Select speed. Smaller number is faster.")
    parser.set_defaults(host='10.44.121.45', port=0xB407, format='png', speed=8)
    #parser.set_defaults(host='localhost', port=0xB407, format='png', speed=8)
    args = parser.parse_args()
    return args


def main():
    args = parse_args()
    sock = SocketHelper(args.host, args.port)
    sock.send_command("G")

    has_bricks = True
    while has_bricks:
        image_bytes = sock.get_image()
        image_rec = ImageRec(image_bytes)
        has_bricks, ball_x, paddle_x = image_rec.get_data()
        
        if has_bricks:
            if ball_x < paddle_x:
                sock.send_command("L")
            elif ball_x > paddle_x:
                sock.send_command("R")
            else:
                sock.send_command(".")

if __name__ == '__main__':
    main()