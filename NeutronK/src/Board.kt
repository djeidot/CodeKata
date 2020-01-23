import Enums.Direction
import Enums.MoveType
import Enums.Piece
import Enums.Position
import Exceptions.InvalidMoveException
import Players.Player
import java.lang.RuntimeException

class Board() {

    private var board: Array<Array<Piece>> = arrayOf()
    private var moveList: MoveList = MoveList()
    private var invisible = false
    
    init {
        for (r in 0..4) {
            var row = arrayOf<Piece>()
            for (c in 0..4) {
                row += when (r) {
                    0 -> Piece.PlayerX
                    4 -> Piece.PlayerO
                    else -> Piece.Empty
                }
            }
            board += row
        }
    }
    
    constructor(other: Board) : this() {
        for (r in 0..4) {
            for (c in 0..4) {
                this.board[r][c] = other.board[r][c]
            }
        }
        invisible = true
    }

    private fun pieceAt(position: Position) = board[position.r][position.c]

    private fun movePieceTo(from: Position, to: Position) {
        val piece = pieceAt(from)
        board[from.r][from.c] = Piece.Empty
        board[to.r][to.c] = piece
    }

    fun show() {
        if (invisible) {
            return
        }

        println("       1 2 3 4 5 " + moveList.getHeaders())
        println("      +---------+" + moveList.getSeparator())
        for (r in 0..4) {
            print("    ${('A' + r)} |")
            for (c in 0..4) {
                print(board[r][c].mark)
                if (c < 4) {
                    print(" ")
                }
            }
            println("|" + moveList.getMoveString(r))
        }
        println("      +---------+" + moveList.getMoveString(6))
        for (i in 7 until moveList.getMoveLineCount()) {
            println("                 " + moveList.getMoveString(i))
        }
    }

    fun hasPiece(pos: Position, pieceType: Piece) = pieceAt(pos) == pieceType

    fun canMove(pos: Position, dir: Direction): Boolean {
        val pos2 = pos.copy()
        pos2.move(dir)
        return !pos2.isOffScreen() && pieceAt(pos2) == Piece.Empty
    }

    private fun moveInternal(pos: Position, dir: Direction): Position {
        val posEnd = pos.copy()
        while (canMove(posEnd, dir)) {
            posEnd.move(dir)
        }
        movePieceTo(pos, posEnd)
        return posEnd
    }

    fun move(player: Player, pos: Position, piece: Piece, dir: Direction) {
        if (!hasPiece(pos, piece)) {
            if (pieceAt(pos) == Piece.Empty) {
                throw InvalidMoveException("No piece in $pos to move.")
            } else {
                throw InvalidMoveException("Piece in $pos is not a $piece piece")
            }
        } else if (!canMove(pos, dir)) {
            throw InvalidMoveException("Can't move piece $pos in direction $dir")
        } else {
            moveInternal(pos, dir)
            if (!invisible) {
                moveList.addMove(player, piece, pos, dir, getLastMoveType(player))
                show()
            }
        }
    }

    private fun getLastMoveType(player: Player) = when {
        getNeutronBackLine() == player.playerPiece -> MoveType.losing
        getNeutronBackLine() == player.playerPiece.opponent() || isNeutronBlocked() -> MoveType.winning
        else -> MoveType.other
    }

    fun getNeutron(): Position {
        for (r in 0..4) {
            for (c in 0..4) {
                val pos = Position(r, c)
                if (hasPiece(pos, Piece.Neutron)) {
                    return pos
                }
            }
        }
        throw RuntimeException("Error - Neutron not found in board.")
    }

    fun getNeutronBackLine(): Piece {
        val posNeutron = getNeutron()
        return when (posNeutron.r) {
            0 -> Piece.PlayerX
            4 -> Piece.PlayerO
            else -> Piece.Empty
        }
    }

    fun isNeutronBlocked(): Boolean {
        val posNeutron = getNeutron()
        for (dir in Direction.values()) {
            if (canMove(posNeutron, dir)) {
                return false
            }
        }
        return true
    }

    fun printIfVisible(str: String) {
        if (!invisible) {
            println(str)
        }
    }

    fun setPlayers(playerO: Player, playerX: Player, startingPlayer: Piece) {
        moveList.setPlayers(playerO, playerX, startingPlayer)
    }
}