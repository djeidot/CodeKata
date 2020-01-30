package Enums

enum class Piece(val mark: String) {
    PlayerX("X"),
    PlayerO("O"),
    Neutron("*"),
    Empty(" ");

    val opponent: Piece
        get() = when (this) {
            PlayerO -> PlayerX
            PlayerX -> PlayerO
            else -> this
        }
    
    fun isPlayer() = this == PlayerX || this == PlayerO

    companion object {
        fun fromMark(mark: String) = values().firstOrNull { it.mark == mark } ?: Empty
    }
}