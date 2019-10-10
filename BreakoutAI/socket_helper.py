import io
import socket

class SocketHelper:

    def s_get_tcp_socket(self):
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.connect((self.host, self.port))
        return sock

    def s_get_udp_socket(self):
        sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        sock.connect((self.host, self.port))
        return sock

    def __init__(self, host, port) -> None:
        super().__init__()
        self.host = host
        self.port = port
        self.BUFFER_SIZE = 1024
        
    def get_image(self):
        with self.s_get_tcp_socket() as sock:
            buffer = io.BytesIO()
            while True:
                data = sock.recv(102400)
                if not data:
                    break
                buffer.write(data)
    
        return buffer

    def send_command(self, command):
        with self.s_get_udp_socket() as sock:
            sock.send(bytes(command, 'utf-8'))