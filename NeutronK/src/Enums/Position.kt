package Enums

import java.lang.IndexOutOfBoundsException

data class Position(val r: Int, val c: Int) {
    
    constructor(posStr: String) : this(posStr[0] - 'A', posStr[1].toInt()) {
        if (this.isOffScreen()) {
            throw IndexOutOfBoundsException("$posStr is not a valid position.")
        }
    }

    fun move(Direction dir) {
        r += dir.v
        c += dir.h
    }

    override fun toString() = "${('A' + r).toChar()}${c + 1}"
        
    fun isOffScreen() = !(r in 0..4 && c in 0..4)
}