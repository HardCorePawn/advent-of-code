package aoc.utils

import kotlin.math.abs

enum class Direction(val coord: Coord) {
    UP(Coord(0,-1)),
    UPRIGHT(Coord(1,-1)),
    RIGHT(Coord(1,0)),
    DOWNRIGHT(Coord(1,1)),
    DOWN(Coord(0,1)),
    DOWNLEFT(Coord(-1,1)),
    LEFT(Coord(-1,0)),
    UPLEFT(Coord(-1,-1));

    fun turnCW(): Direction {
        return when (this) {
            UP -> RIGHT
            RIGHT -> DOWN
            DOWN -> LEFT
            LEFT -> UP
            else -> TODO()
        }
    }

    fun turnCCW(): Direction {
        return when (this) {
            UP -> LEFT
            RIGHT -> UP
            DOWN -> RIGHT
            LEFT -> DOWN
            else -> TODO()
        }
    }

    fun turn180(): Direction {
        return when (this) {
            UP -> DOWN
            RIGHT -> LEFT
            DOWN -> UP
            LEFT -> RIGHT
            else -> TODO()
        }
    }
}

val cardinals = listOf( Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT )

/**
 * Class to represent a (x,y) Coord in a 2D grid (because Pair<Int, Int> gets messy :P)
 */
data class Coord(var x: Int, var y: Int) {
    operator fun plus(second: Coord) = Coord(x + second.x, y + second.y)
    operator fun minus(second: Coord) = Coord(x - second.x, y - second.y)
    operator fun times(multiplier: Int) = Coord(x * multiplier, y * multiplier)

    fun manhattanDistTo(dest: Coord) = abs(x - dest.x) + abs(y - dest.y)
}

fun withinBounds(coord: Coord, grid: Array<CharArray>): Boolean {
    return coord.y in grid.indices && coord.x in grid[0].indices
}

fun withinBounds(coord: Coord, grid: Array<IntArray>): Boolean {
    return coord.y in grid.indices && coord.x in grid[0].indices
}