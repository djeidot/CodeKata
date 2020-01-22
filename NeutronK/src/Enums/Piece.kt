package Enums

enum class Piece(val mark: String) {
    PlayerX("X"),
    PlayerO("O"),
    Neutron("*"),
    Empty(" ");

    fun opponent(): Piece {
        return when (this) {
            PlayerO -> PlayerX
            PlayerX -> PlayerO
            else -> this
        }
    }
    
    fun isPlayer() = this == PlayerX || this == PlayerO

    companion object {
        fun fromMark(mark: String) = values().firstOrNull { it.mark == mark } ?: Empty
    }
}