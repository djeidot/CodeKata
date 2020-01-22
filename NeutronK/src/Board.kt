import Enums.Piece
import javax.swing.text.Position

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

    constructor(pojo: GamePojo) : this() {
        for (r in 0..4) {
            for (c in 0..4) {
                this.board[r][c] = Piece.fromMark(pojo.board[r][c])
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
}