import Enums.Direction
import Enums.MoveType
import Enums.Piece
import Enums.Position
import Players.Player
import java.lang.RuntimeException

class MoveList {
    private var player1: Player? = null
    private var player2: Player? = null

    class MoveLine(val player1Move: PlayerMove, var player2Move: PlayerMove?)
    
    private val moves = ArrayList<MoveLine>()
    
    private val spacer = "     "

    fun setPlayers(playerO: Player, playerX: Player, startingPlayer: Player) {
        when (startingPlayer) {
            playerO -> {
                this.player1 = playerO
                this.player2 = playerX
            }
            playerX -> {
                this.player1 = playerX
                this.player2 = playerO
            }
            else -> throw RuntimeException("Invalid starting player")
        }
    }

    fun addMove(player: Player, piece: Piece, pos: Position, dir: Direction, moveType: MoveType) {
        if (piece == Piece.Neutron) {
            val move = PlayerMove(player, dir, moveType)
            addToList(move)
        } else {
            val move = getLastMove()
            move.setPieceMove(player, pos, dir, moveType)
        }
    }
    
    fun getHeaders() = "$spacer ${Format.center(player1!!.name, 12, true)}|${Format.center(player2!!.name, 12, false)}"
    
    fun getSeparator() = spacer + "-------------|-------------"

    fun getMoveString(lineNr: Int): String {
        // ex:    * NE, A1 SW  | * SE, E5 NW
        if (lineNr >= moves.size) {
            return ""
        }
        
        val line = moves.get(lineNr)
        val builder = StringBuilder()
        builder.append(spacer)
        builder.append(" * " + String.format("%2s", line.player1Move.neutronMove))
        val piece1Move = line.player1Move.pieceMove
        if (piece1Move != null) {
            builder.append(", ${piece1Move.first} ${String.format("%2s", piece1Move.second)} |")
        } else {
            builder.append("        |")
        }
        val player2Move = line.player2Move
        if (player2Move != null) {
            builder.append(" * " + String.format("%2s", player2Move.neutronMove))
            val piece2Move = player2Move.pieceMove
            if (piece2Move != null) {
                builder.append(", ${piece2Move.first} ${String.format("%2s", piece2Move.second)}")
            }
        }
        return builder.toString()
    }
    
    fun getMoveLineCount() = moves.size

    private fun addToList(move: PlayerMove) {
        if (moves.isEmpty()) {
            addNewMoveLine(move)
        } else {
            val moveLine = moves.get(moves.size - 1)
            if (moveLine.player2Move == null) {
                moveLine.player2Move = move
            } else {
                addNewMoveLine(move)
            }
        }
    }

    private fun addNewMoveLine(move: PlayerMove) {
        val newMoveLine = MoveLine(move, null)
        moves.add(newMoveLine)
    }

    private fun getLastMove() : PlayerMove {
        val moveLine = moves.get(moves.size - 1)
        return moveLine.player2Move ?: moveLine.player1Move
    }
}