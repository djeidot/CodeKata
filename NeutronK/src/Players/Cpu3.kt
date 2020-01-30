package Players

import Board
import Enums.Direction
import Enums.MoveType
import Enums.Piece
import Exceptions.InvalidMoveException
import PlayerMove

open class Cpu3(name: String, playerPiece: Piece, board: Board) : Cpu2(name, playerPiece, board) {

    private lateinit var playerMove: PlayerMove
    
    override fun chooseNeutronDirection(): Direction {
        this.playerMove = choice(getPlayerMoves(playerPiece, board))
        return this.playerMove.neutronMove
    }

    override fun choosePlayerPositionAndDirection() = this.playerMove.pieceMove ?: super.choosePlayerPositionAndDirection()

    open fun getPlayerMoves(playerPiece: Piece, board: Board): List<PlayerMove> {
        // Returns a list of possible moves
        // If there is a list of winning moves only that list is returned
        // Otherwise it returns a list of normal moves
        // Losing moves are avoided unless there is no other choice

        val neutronMoves = getPossibleMoves(board.neutron, board)
        val winningNeutronMoves = mutableListOf<PlayerMove>()
        val losingNeutronMoves = mutableListOf<PlayerMove>()
        val otherNeutronMoves = mutableListOf<PlayerMove>()

        for (move in neutronMoves) {
            when {
                canMoveNeutronToPlayersBackLine(board, playerPiece.opponent, move) -> {
                    winningNeutronMoves.add(PlayerMove(this, move, null, MoveType.winning))
                }
                canMoveNeutronToPlayersBackLine(board, playerPiece, move) -> {
                    losingNeutronMoves.add(PlayerMove(this, move, null, MoveType.losing))
                }
                else -> {
                    otherNeutronMoves.add(PlayerMove(this, move, null, MoveType.other))
                }
            }
        }

        if (winningNeutronMoves.isNotEmpty()) {
            board.printIfVisible("Player ${playerPiece.mark} has a winning move")
            return winningNeutronMoves
        } else if (otherNeutronMoves.isEmpty()) {
            board.printIfVisible("Player ${playerPiece.mark} forced to make a losing moves")
            return losingNeutronMoves
        }

        val winningPieceMoves = mutableListOf<PlayerMove>()
        val otherPieceMoves = mutableListOf<PlayerMove>()

        for (neutronMove in otherNeutronMoves) {
            val vBoard3 = Board (board)
            try {
                vBoard3.move(this, vBoard3.neutron, Piece.Neutron, neutronMove.neutronMove)
            } catch (e: InvalidMoveException) {
                println("Cpu3 made a wrong move - ${e.message}")
            }
            
            val positions = getPlayerPositions(vBoard3, playerPiece)
            for (pos in positions) {
                val moves = getPossibleMoves(pos, vBoard3)
                for (move in moves) {
                    if (canTrapNeutron(pos, move, playerPiece, vBoard3)) {
                        winningPieceMoves.add(PlayerMove(this, neutronMove.neutronMove, pos, move, MoveType.winning))
                    } else {
                        otherPieceMoves.add(PlayerMove(this, neutronMove.neutronMove, pos, move, MoveType.other))
                    }
                }
            }
        }

        return if (winningPieceMoves.isNotEmpty()) {
            board.printIfVisible("Player ${playerPiece.mark} has a winning move")
            winningPieceMoves
        } else {
            otherPieceMoves
        }
    }
}