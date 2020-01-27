import Enums.Direction
import Enums.MoveType
import Enums.Position
import Players.Player


class PlayerMove(val player:Player, val neutronMove: Direction, var pieceMove: Pair<Position, Direction>?, var moveType: MoveType) {
    var neutronMovesAfter: Int? = null

    constructor(player: Player, neutronMove: Direction, piecePosition: Position, pieceDirection: Direction, moveType: MoveType) 
            : this(player, neutronMove, Pair(piecePosition, pieceDirection), moveType)

    constructor(player: Player, neutronMove: Direction, moveType: MoveType) 
            : this(player, neutronMove, null, moveType)

    fun setPieceMove(player: Player, piecePosition: Position, pieceDirection: Direction, moveType: MoveType) {
        assert(this.player == player)
        this.pieceMove = Pair(piecePosition, pieceDirection)
        this.moveType = moveType
    }    
}