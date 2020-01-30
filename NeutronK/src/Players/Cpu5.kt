package Players

import Board
import Enums.MoveType
import Enums.Piece
import Exceptions.InvalidMoveException
import PlayerMove
import java.lang.RuntimeException

// In addition to the other cpus, Cpu5 will choose the normal move that traps the
// neutron the most
class Cpu5(name: String, playerPiece: Piece, board: Board) : Cpu4(name, playerPiece, board) {

    override fun getPlayerMoves(playerPiece: Piece, board: Board): List<PlayerMove> {
        // Returns a list of possible moves
        // If there is a list of winning moves only that list is returned
        // Otherwise it returns a list of normal moves
        // Losing moves are avoided unless there is no other choice

        // Winning and other moves are gotten from the Cpu4 version of this method so we can call it now
        val startingMoves = super.getPlayerMoves(playerPiece, board)

        if (startingMoves.get(0).moveType == MoveType.winning || startingMoves.get(0).moveType == MoveType.losing) {
            return startingMoves
        }

        var minimumMoves = 9

        for (playerMove in startingMoves) {
            val vBoard5 = Board(board)
            try {
                vBoard5.move(this, vBoard5.getNeutron(), Piece.Neutron, playerMove.neutronMove)
                vBoard5.move(this, playerMove.pieceMove!!.first, playerPiece, playerMove.pieceMove!!.second)
            } catch (e: InvalidMoveException) {
                println("Cpu5 made a wrong move - ${e.message}")
            }

            val neutronMoves = getPossibleMoves(vBoard5.getNeutron(), vBoard5).size
            playerMove.neutronMovesAfter = neutronMoves
            if (neutronMoves < minimumMoves) {
                minimumMoves = neutronMoves
            }
        }
        
        val bestMoves = mutableListOf<PlayerMove>()
        for (playerMove in startingMoves) {
            if (playerMove.neutronMovesAfter == minimumMoves) {
                bestMoves.add(playerMove)
            }
        }

        if (bestMoves.isEmpty()) {
            throw RuntimeException("Cpu5 miscalculated its moves")
        }
        
        board.printIfVisible("Player ${playerPiece.mark} choosing between ${bestMoves.size} out of ${startingMoves.size} regular moves")
        return bestMoves
    }
}