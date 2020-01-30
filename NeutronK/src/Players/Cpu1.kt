package Players

import Board
import Enums.Direction
import Enums.Piece
import Enums.Position
import Exceptions.InvalidMoveException
import java.util.*

// Cpu1 is the most basic AI. It simply selects a random move from all possible ones
open class Cpu1(name: String, playerPiece: Piece, board: Board) : Player(name, playerPiece, board) {

    private val random = Random()
    
    override fun moveNeutron() {
        val dir = chooseNeutronDirection()
        
        println("Player ${playerPiece.mark} moving neutron to $dir")
        try {
            board.move(this, board.neutron, Piece.Neutron, dir)
        } catch (e: InvalidMoveException) {
            println("Cpu1 made a wrong move - ${e.message}")
        }
    }

    override fun movePlayerPiece() {
        val move = choosePlayerPositionAndDirection()
        val pos = move.first
        val dir = move.second

        println("Player ${playerPiece.mark} moving piece $pos to $dir")

        try {
            board.move(this, pos, playerPiece, dir)
        } catch (e: InvalidMoveException) {
            println("Cpu1 made a wrong move - ${e.message}")
        }
    }

    open fun chooseNeutronDirection(): Direction {
        val moves = getPossibleMoves(board.neutron, board)
        return choice(moves)
    }

    open fun choosePlayerPositionAndDirection(): Pair<Position, Direction> {
        val positions = getPlayerPositions(board, playerPiece)
        var moves = listOf<Direction>()
        var pos = Position(1, 1)

        while (moves.isEmpty()) {
            pos = choice(positions)
            moves = getPossibleMoves(pos, board)
        }
        val dir = choice(moves)
        return Pair(pos, dir)
    }

    fun getPossibleMoves(pos: Position, board: Board): List<Direction> =
        Direction.values().filter { board.canMove(pos, it) }

    fun getPlayerPositions(board: Board, playerPiece: Piece): List<Position> {
        val positions = mutableListOf<Position>()
        for (r in 0..4) {
            for (c in 0..4) {
                val pos = Position(r, c)
                if (board.hasPiece(pos, playerPiece)) {
                    positions += pos
                }
            }
        }
        return positions
    }

    fun <T> choice(possibleChoices: List<T>): T {
        return possibleChoices[random.nextInt(possibleChoices.size)]
    }
}