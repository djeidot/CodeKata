package Players

import Board
import Enums.Piece

abstract class Player(val name: String, val playerPiece: Piece, val board: Board) {
    abstract fun moveNeutron()
    abstract fun movePlayerPiece()
}