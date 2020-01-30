package Players

import Board
import Enums.Direction
import Enums.Piece
import Enums.Position
import Exceptions.InvalidMoveException

class Human(name: String, playerPiece: Piece, board: Board) : Player(name, playerPiece, board) {
    
    override fun moveNeutron() {
        print("Player ${playerPiece.mark} move neutron (direction only, e.g. 'NE'): ")
        val input = readLine()!!.trim()

        try {
            val dir = Direction.valueOf(input)
            board.move(this, board.neutron, Piece.Neutron, dir)
        } catch (e: IllegalArgumentException) {
            println("$input is not a valid direction")
            moveNeutron()
        } catch (e: InvalidMoveException) {
            println(e.message)
            moveNeutron()
        }
    }

    override fun movePlayerPiece() {
        print("Player ${playerPiece.mark} move piece (position + direction, e.g. 'A1 NE'): ")
        val input = readLine()!!.trim()

        try {
            val pos = Position(input.substring(0..1))
            val dir = Direction.valueOf(input.substring(2).trim())
            board.move(this, pos, playerPiece, dir)
        } catch (e: EnumConstantNotPresentException) {
            println("$input is not a valid direction")
            movePlayerPiece()
        } catch (e: InvalidMoveException) {
            println(e.message)
            movePlayerPiece()
        } catch (e: IndexOutOfBoundsException) {
            println(e.message)
            movePlayerPiece()
        }
    }
}