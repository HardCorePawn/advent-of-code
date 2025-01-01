package aoc.year2024

import DailyProblem
import aoc.utils.Coord
import aoc.utils.Direction
import aoc.utils.char2DArrayToMap
import aoc.utils.parseCharArray

class Day15Problem : DailyProblem<Int>() {

    enum class SquareType(val char: Char) {
        WALL('#'),
        BOX('O'),
        ROBOT('@'),
        EMPTY('.'),
        BOXLEFT('['),
        BOXRIGHT(']')
    }

    override val number = 15
    override val year = 2024
    override val name = "Warehouse Woes"

    private lateinit var grid: Array<CharArray>
    private lateinit var gridMap: MutableMap<Coord, Char>
    private lateinit var expandedMap: MutableMap<Coord, Char>
    private lateinit var moves: MutableList<Direction>

    // convert character to SquareType
    private fun getType(c: Char): SquareType {
        return when (c) {
            '#' -> SquareType.WALL
            'O' -> SquareType.BOX
            '@' -> SquareType.ROBOT
            else -> SquareType.EMPTY
        }
    }

    // convert movement character to Dir
    private fun getDir(c: Char): Direction {
        return when (c) {
            '^' -> Direction.UP
            'v' -> Direction.DOWN
            '<' -> Direction.LEFT
            else -> Direction.RIGHT
        }
    }

    private fun parseInput(input: String) {
        val gridLines = mutableListOf<String>()
        moves = mutableListOf()
        //split board grid and movement list
        input.lines().filter { it.isNotEmpty() }.forEach { line ->
            if (line.contains('#')) {
                gridLines.add(line)
            } else if (line.contains('v') || line.contains('^') ||
                line.contains('<') || line.contains('>')
            ) {
                // line containing movement instructions
                line.forEach { move ->
                    moves.add(getDir(move))
                }
            }
        }
        grid = parseCharArray(gridLines) // parse starting grid
        gridMap = char2DArrayToMap(grid).toMutableMap()
    }

    // Takes the standard board after Input parsing, and expands all the walls, boxes and empty spaces into 2 spaces
    // NOTE: Boxes now become BOXLEFT and BOXRIGHT objects
    // Robot stays as single space, and an extra empty space is put next to it.
    private fun expandMap() {
        expandedMap = mutableMapOf()

        var prevRow = 0
        var col = 0
        gridMap.forEach { (pos, value) ->
            if (pos.y != prevRow) {
                // found a new row, reset col count
                prevRow = pos.y
                col = 0
            }
            when (value) {
                SquareType.WALL.char -> {
                    expandedMap[Coord(col++,prevRow)] = SquareType.WALL.char
                    expandedMap[Coord(col++,prevRow)] = SquareType.WALL.char
                }
                SquareType.EMPTY.char -> {
                    expandedMap[Coord(col++,prevRow)] = SquareType.EMPTY.char
                    expandedMap[Coord(col++,prevRow)] = SquareType.EMPTY.char
                }
                SquareType.BOX.char -> {
                    // BOX becomes BOXLEFT and BOXRIGHT
                    expandedMap[Coord(col++,prevRow)] = SquareType.BOXLEFT.char
                    expandedMap[Coord(col++,prevRow)] = SquareType.BOXRIGHT.char
                }
                SquareType.ROBOT.char -> {
                    // ROBOT becomes ROBOT + EMPTY SPACE
                    expandedMap[Coord(col++,prevRow)] = SquareType.ROBOT.char
                    expandedMap[Coord(col++,prevRow)] = SquareType.EMPTY.char
                }
                else -> {}
            }
        }
    }

    // calculate the pos of the next square using the given direction of movement
    private fun nextSquare(pos: Coord, dir: Direction): Coord {
        return pos + dir.coord
    }

    // Recursive function to move 1 space boxes
    private fun moveBox(boxPos: Coord, dir: Direction): Boolean {

        val nextSquarePos = nextSquare(boxPos, dir)
        if (gridMap[nextSquarePos] == SquareType.EMPTY.char) {
            // empty space, move the box
            gridMap[nextSquarePos] = SquareType.BOX.char
            gridMap[boxPos] = SquareType.EMPTY.char
            return true
        } else if (gridMap[nextSquarePos] == SquareType.BOX.char) {
            // move the box next to us first
            if (moveBox(nextSquarePos, dir)) {
                // then move the box
                gridMap[nextSquarePos] = SquareType.BOX.char
                gridMap[boxPos] = SquareType.EMPTY.char
                return true
            }
        }
        return false
    }

    // recursive function to see if the "Big" box we're pushing can move in the direction we're trying to go
    private fun bigBoxCanMove(boxPos: Coord, dir: Direction): Boolean {

        var canMove = false

        // are we pushing on the left side of the box?
        if (expandedMap[boxPos] == SquareType.BOXLEFT.char) {
            val nextSquarePos = nextSquare(boxPos, dir)
            when (dir) {
                Direction.UP, Direction.DOWN -> {
                    val nextBoxRightPos = Coord(nextSquarePos.x + 1, nextSquarePos.y)
                    if (expandedMap[nextSquarePos] == SquareType.EMPTY.char &&
                        expandedMap[nextBoxRightPos] == SquareType.EMPTY.char
                    ) {
                        // both sides of the box are moving into empty space, we're good.
                        canMove = true
                    } else {
                        canMove =
                            if (expandedMap[nextSquarePos] == SquareType.WALL.char ||
                                expandedMap[nextBoxRightPos] == SquareType.WALL.char) {
                                // box is blocked on one side or the other by a wall
                                false
                            } else if (expandedMap[nextSquarePos] == SquareType.EMPTY.char) {
                                // right side, is against a box, see if that box can move
                                bigBoxCanMove(nextBoxRightPos, dir)
                            } else if (expandedMap[nextBoxRightPos] == SquareType.EMPTY.char) {
                                // left side, is against a box, see if that box can move
                                bigBoxCanMove(nextSquarePos, dir)
                            } else {
                                // both sides are against a box, see if the box(es) can be moved
                                bigBoxCanMove(nextBoxRightPos, dir) &&
                                        bigBoxCanMove(nextSquarePos, dir)
                            }
                    }
                }

                Direction.LEFT -> {
                    canMove = if (expandedMap[nextSquarePos] == SquareType.EMPTY.char) {
                        // nothing to the left of the box, so move
                        true
                    } else if (expandedMap[nextSquarePos] == SquareType.BOXRIGHT.char) {
                        // see if the box next to us can move
                        bigBoxCanMove(nextSquarePos, dir)
                    } else {
                        false
                    }
                }

                Direction.RIGHT -> {
                    canMove = if (expandedMap[nextSquarePos] == SquareType.BOXRIGHT.char) {
                        // move to the right side of the box and see if it can be moved
                        bigBoxCanMove(nextSquarePos, dir)
                    } else {
                        false
                    }
                }

                else -> {}
            }
        } else { //SquareType.BOXRIGHT
            // or on the right side of the box?
            val nextSquarePos = nextSquare(boxPos, dir)
            when (dir) {
                Direction.UP, Direction.DOWN -> {
                    val nextBoxLeftPos = Coord(nextSquarePos.x - 1, nextSquarePos.y)
                    if (expandedMap[nextSquarePos] == SquareType.EMPTY.char &&
                        expandedMap[nextBoxLeftPos] == SquareType.EMPTY.char
                    ) {
                        // both sides of the box are moving into empty space, we're good.
                        canMove = true
                    } else {
                        canMove =
                            if (expandedMap[nextSquarePos] == SquareType.WALL.char ||
                                expandedMap[nextBoxLeftPos] == SquareType.WALL.char) {
                                // box is blocked on one side or the other by a wall
                                false
                            } else if (expandedMap[nextSquarePos] == SquareType.EMPTY.char) {
                                // left side, is against a box, see if that box can move
                                bigBoxCanMove(nextBoxLeftPos, dir)
                            } else if (expandedMap[nextBoxLeftPos] == SquareType.EMPTY.char) {
                                // right side, is against a box, see if that box can move
                                bigBoxCanMove(nextSquarePos, dir)
                            } else {
                                // both sides are against a box, see if the box(es) can be moved
                                bigBoxCanMove(nextBoxLeftPos, dir) &&
                                        bigBoxCanMove(nextSquarePos, dir)
                            }
                    }
                }

                Direction.LEFT -> {
                    canMove = if (expandedMap[nextSquarePos] == SquareType.BOXLEFT.char) {
                        // move to the left side of the box and see if it can be moved
                        bigBoxCanMove(nextSquarePos, dir)
                    } else {
                        false
                    }
                }

                Direction.RIGHT -> {
                    canMove = if (expandedMap[nextSquarePos] == SquareType.EMPTY.char) {
                        // nothing to the right of the box, so move
                        true
                    } else if (expandedMap[nextSquarePos] == SquareType.BOXLEFT.char) {
                        // see if the box next to us can move
                        bigBoxCanMove(nextSquarePos, dir)
                    } else {
                        false
                    }
                }

                else -> {}
            }
        }

        return canMove
    }

    // Recursive function to move 2 space boxes
    private fun moveBigBox(boxPos: Coord, dir: Direction) {
        val boxType = expandedMap[boxPos]
        val nextSquarePos = nextSquare(boxPos, dir)

        // This section will move both box parts when pushing left and right
        if (expandedMap[nextSquarePos] == SquareType.EMPTY.char) {
            // empty space, move the box part
            expandedMap[nextSquarePos] = boxType!!
            expandedMap[boxPos] = SquareType.EMPTY.char
        } else if (expandedMap[nextSquarePos] == SquareType.BOXLEFT.char ||
            expandedMap[nextSquarePos] == SquareType.BOXRIGHT.char) {
            // move the box part next to us first
            moveBigBox(nextSquarePos, dir)
            // then move the current box part
            expandedMap[nextSquarePos] = boxType!!
            expandedMap[boxPos] = SquareType.EMPTY.char
        }

        // But we need some special handling when pushing up or down, so the other half of the box moves
        if (dir == Direction.UP || dir == Direction.DOWN) {
            val otherHalfType = if (boxType == SquareType.BOXLEFT.char) {
                SquareType.BOXRIGHT
            } else {
                SquareType.BOXLEFT
            }

            val otherHalfPos = if (boxType == SquareType.BOXLEFT.char) {
                Coord(boxPos.x + 1, boxPos.y)
            } else {
                Coord(boxPos.x - 1, boxPos.y)
            }

            val otherHalfNext = nextSquare(otherHalfPos, dir)

            if (expandedMap[otherHalfNext] == SquareType.EMPTY.char) {
                // empty space above/below the other half of box, so easy move
                expandedMap[otherHalfNext] = otherHalfType.char
                expandedMap[otherHalfPos] = SquareType.EMPTY.char
            } else if (expandedMap[otherHalfNext] == SquareType.BOXLEFT.char ||
                expandedMap[otherHalfNext] == SquareType.BOXRIGHT.char) {
                // there is a box above/below the other half of the box
                // move that new box first
                moveBigBox(otherHalfNext, dir)
                // then we can move the other half of the box
                expandedMap[otherHalfNext] = otherHalfType.char
                expandedMap[otherHalfPos] = SquareType.EMPTY.char
            }
        }
    }

    // Calculates the total sum of GPS co-ords for Boxes
    //
    // "The GPS coordinate of a box is equal to 100 times its distance from the top edge of the map plus its
    // distance from the left edge of the map. (This process does not stop at wall tiles; measure all the way
    // to the edges of the map.)"
    private fun calcSumGPSCoords(theMap: Map<Coord, Char>, boxType: SquareType): Int {
        // get just the box objects we are interested in counting
        val boxes = theMap.filterValues { it == boxType.char }

        return boxes.keys.sumOf { key ->
            key.y * 100 + key.x
        }
    }

    // Outputs the given board
    // Useful as a "Visual Debugging Tool" ;)
    private fun displayBoard(theMap: Map<Coord, Char>) {
        var prevRow = 0

        theMap.forEach { (pos, value) ->
            if (pos.y != prevRow) {
                println()
                prevRow = pos.y
            }
            print(value)
        }
        println()
        println()
    }

    override fun commonCode() {
        parseInput(getInputText())
        expandMap() //generate expanded grid Map for Part 2
    }

    override fun part1(): Int {
        moves.forEach { move ->
            val robotSquare = gridMap.filterValues { getType(it) == SquareType.ROBOT }.keys.first()
            val nextSquarePos = nextSquare(robotSquare, move)

            if (gridMap[nextSquarePos] == SquareType.EMPTY.char) {
                // move the robot
                gridMap[nextSquarePos] = SquareType.ROBOT.char
                gridMap[robotSquare] = SquareType.EMPTY.char
            } else if (gridMap[nextSquarePos] == SquareType.BOX.char) {
                // see if box can move
                if (moveBox(nextSquarePos, move)) {
                    //move robot
                    gridMap[nextSquarePos] = SquareType.ROBOT.char
                    gridMap[robotSquare] = SquareType.EMPTY.char
                }
            }
        }
        //displayBoard(gridMap)

        return calcSumGPSCoords(gridMap, SquareType.BOX)
    }

    override fun part2(): Int {
        moves.forEach { move ->
            val robotSquare = expandedMap.filterValues { it == SquareType.ROBOT.char }.keys.first()
            val nextSquarePos = nextSquare(robotSquare, move)

            if (expandedMap[nextSquarePos] == SquareType.EMPTY.char) {
                // move the robot
                expandedMap[nextSquarePos] = SquareType.ROBOT.char
                expandedMap[robotSquare] = SquareType.EMPTY.char
            } else if (expandedMap[nextSquarePos] == SquareType.BOXLEFT.char ||
                expandedMap[nextSquarePos] == SquareType.BOXRIGHT.char
            ) {
                // see if box can move
                if (bigBoxCanMove(nextSquarePos, move)) {
                    moveBigBox(nextSquarePos, move)
                    //move robot
                    expandedMap[nextSquarePos] = SquareType.ROBOT.char
                    expandedMap[robotSquare] = SquareType.EMPTY.char
                }
            }
        }
        //displayBoard(expandedMap)

        // NOTE: calculate the GPS co-ords using the left side of the box
        return calcSumGPSCoords(expandedMap, SquareType.BOXLEFT)
    }
}

val day15Problem = Day15Problem()

fun main() {
    //day15Problem.testData = true
    day15Problem.runBoth(1)
}