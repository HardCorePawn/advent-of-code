package aoc.utils

import kotlin.math.abs

/**
 * Class to represent a (x,y) Coord in a 2D grid (because Pair<Int, Int> gets messy :P)
 */
data class Coord(val x: Int, val y: Int) {
    operator fun plus(second: Coord) = Coord(x + second.x, y + second.y)
    operator fun minus(second: Coord) = Coord(x - second.x, y - second.y)
    operator fun times(multiplier: Int) = Coord(x * multiplier, y * multiplier)

    fun manhattanDistTo(dest: Coord) = abs(x - dest.x) + abs(y - dest.y)
}