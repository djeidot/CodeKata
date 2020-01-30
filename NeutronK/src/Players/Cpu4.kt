package Players

import Board
import Enums.MoveType
import Enums.Piece
import Exceptions.InvalidMoveException
import PlayerMove

// Cpu4 will try to anticipate winning moves by the opponent player so that it can identify
// and avoid losing moves by the current player
open class Cpu4(name: String, playerPiece: Piece, board: Board) : Cpu3(name, playerPiece, board) {

    override fun getPlayerMoves(playerPiece: Piece, board: Board): List<PlayerMove> {
        // Returns a list of possible moves
        // If there is a list of winning moves only that list is returned
        // Otherwise it returns a list of normal moves
        // Losing moves are avoided unless there is no other choice
        
        // Winning and other moves are gotten from the Cpu3 version of this method so we can call it now
        val startingMoves = super.getPlayerMoves(playerPiece, board)

        if (startingMoves[0].moveType == MoveType.winning || startingMoves[0].moveType == MoveType.losing) {
            return startingMoves
        }

        val losingMoves = mutableListOf<PlayerMove>()
        val otherMoves = mutableListOf<PlayerMove>()
        val winningMoves = mutableListOf<PlayerMove>()

        for (playerMove in startingMoves) {
            val vBoard4 = Board(board)
            try {
                vBoard4.move(this, vBoard4.neutron, Piece.Neutron, playerMove.neutronMove)
                vBoard4.move(this, playerMove.pieceMove!!.first, playerPiece, playerMove.pieceMove!!.second)
            } catch (e: InvalidMoveException) {
                println("Cpu4 made a wrong move - ${e.message}")
            }
            
            val opponentMoves = super.getPlayerMoves(playerPiece.opponent, vBoard4)
            when (opponentMoves[0].moveType) {
                MoveType.winning -> {
                    playerMove.moveType = MoveType.losing
                    losingMoves.add(playerMove)
                }
                MoveType.losing -> {
                    playerMove.moveType = MoveType.winning
                    winningMoves.add(playerMove)
                }
                else -> {
                    playerMove.moveType = MoveType.other
                    otherMoves.add(playerMove)
                }
            }
        }

        if (winningMoves.isNotEmpty()) {
            board.printIfVisible("Player ${playerPiece.mark} has a winning move")
            return winningMoves
        }

        if (otherMoves.isEmpty()) {
            board.printIfVisible("Player ${playerPiece.mark} forced to make a losing move")
            return losingMoves
        }

        if (losingMoves.isNotEmpty()) {
            board.printIfVisible("Player ${playerPiece.mark} is avoiding ${losingMoves.size} losing moves")
        }
        
        return otherMoves
    }
}