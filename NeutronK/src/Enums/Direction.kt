package Enums

enum class Direction(val h: Int, val v: Int) {
    N  (0,-1),
    NE (1, -1),
    E  (1, 0),
    SE (1, 1),
    S  (0, 1),
    SW (-1, 1),
    W  (-1, 0),
    NW (-1, -1);
}