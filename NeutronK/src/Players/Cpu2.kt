package Players

import Board
import Enums.Direction
import Enums.Piece
import Enums.Position
import Exceptions.InvalidMoveException
import java.lang.RuntimeException

// Cpu2 will choose a direct winning move if there is one available and avoids a losing move unless it's the only choice.
// However it treats neutron and piece moves separately.
open class Cpu2(name: String, playerPiece: Piece, board: Board) : Cpu1(name, playerPiece, board) {
    override fun chooseNeutronDirection(): Direction {
        val moves = getPossibleMoves(board.getNeutron(), board)
        val winningMoves = mutableListOf<Direction>()
        val losingMoves = mutableListOf<Direction>()
        val otherMoves = mutableListOf<Direction>()

        for (move in moves) {
            if (canMoveNeutronToPlayersBackline(board, playerPiece.opponent(), move)) {
                winningMoves.add(move)
            } else if (canMoveNeutronToPlayersBackline(board, playerPiece, move)) {
                losingMoves.add(move)
            } else {
                otherMoves.add(move)
            }
        }

        if (!winningMoves.isEmpty()) {
            println("Player ${playerPiece.mark} has a winning move")
            return choice(winningMoves)
        } else if (!otherMoves.isEmpty()) {
            return choice(otherMoves)
        } else if (!losingMoves.isEmpty()) {
            println("Player ${playerPiece.mark} forced to make a losing move")
            return choice(losingMoves)
        } else {
            throw RuntimeException("No possible neutron moves - game should have been finished by now.")
        }
    }

    internal fun canMoveNeutronToPlayersBackline(board: Board, playerPiece: Piece, move: Direction): Boolean {
        val vBoard2 = Board(board)
        try {
            vBoard2.move(this, vBoard2.getNeutron(), Piece.Neutron, move)
        } catch (e: InvalidMoveException) {
            println("Cpu2 made a wrong move - ${e.message}")
        }
        return vBoard2.getNeutronBackLine() == playerPiece
    }

    override fun choosePlayerPositionAndDirection(): Pair<Position, Direction> {
        val positions = getPlayerPositions(board, playerPiece)
        val winningMoves = mutableListOf<Pair<Position, Direction>>()
        val otherMoves = mutableListOf<Pair<Position, Direction>>()

        for (pos in positions) {
            val moves = getPossibleMoves(pos, board)
            for (move in moves) {
                if (canTrapNeutron(pos, move, playerPiece, board)) {
                    winningMoves.add(Pair(pos, move))
                } else {
                    otherMoves.add(Pair(pos, move))
                }
            }
        }

        if (!winningMoves.isEmpty()) {
            board.printIfVisible("Player ${playerPiece.mark} has a winning move")
            return choice(winningMoves)
        } else {
            return choice(otherMoves)
        }
    }

    internal fun canTrapNeutron(pos: Position, move: Direction, playerPiece: Piece, board: Board): Boolean {
        val vBoard2 = Board(board)
        try {
            vBoard2.move(this, pos, playerPiece, move)
        } catch (e: InvalidMoveException) {
            println("Cpu2 made a wrong move - ${e.message}")
        }
        
        return vBoard2.isNeutronBlocked()
    }
}