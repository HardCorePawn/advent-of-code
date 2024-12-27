package aoc.year2024

import DailyProblem
import aoc.utils.Coord
import aoc.utils.cardinals
import aoc.utils.parseCharArray

class Day6Problem : DailyProblem<Int>() {

    override val number = 6
    override val year = 2024
    override val name = "Guard Gallivant"

    private lateinit var grid: Array<CharArray>
    private lateinit var xRange: IntRange
    private lateinit var yRange: IntRange
    private lateinit var visited: MutableSet<Coord>

    override fun commonCode() {
        grid = parseCharArray(getInputText())
        xRange = grid[0].indices
        yRange = grid.indices
        visited = mutableSetOf()
    }

    private fun findStart(): List<Coord> {
        val found = mutableListOf<Coord>()
        grid.forEachIndexed { y, chars ->
            chars.forEachIndexed { x, c ->
                if (c == '^') found.add(Coord(x,y))
            }
        }
        return found
    }

    override fun part1(): Int {
        var currDir = cardinals.first() // assume guard is always moving up to start
        var currPos = findStart()[0]

        while (currPos.x in xRange && currPos.y in yRange) {
            visited.add(currPos)

            val next = currPos + currDir.coord
            if ((next.x in xRange && next.y in yRange) && (grid[next.y][next.x] == '#')) {
                currDir = currDir.turnCW()
            } else {
                currPos = next
            }
        }

        return visited.size
    }

    override fun part2(): Int {
        var loopCount = 0

        visited.forEach skip@ { newObstacle ->

            var currDir = cardinals.first() // assume guard is always moving up to start
            var currPos = findStart()[0]

            if (newObstacle == currPos) return@skip // we can't put an obstacle at the guard's start pos

            val visitedPos = mutableSetOf<Pair<Coord, Coord>>()

            while (currPos.x in xRange && currPos.y in yRange) {
                //have we been in this location, moving in the same direction before?
                if (currPos to currDir.coord in visitedPos) {
                    //If so, we found a loop, count it and test next position
                    loopCount++
                    break
                }

                // track visited position + direction of travel to detect loops
                visitedPos.add(currPos to currDir.coord)

                val next = currPos + currDir.coord
                if ((next.x in xRange && next.y in yRange) && ((grid[next.y][next.x] == '#') || next == newObstacle)) {
                    currDir = currDir.turnCW()
                } else {
                    currPos = next
                }
            }
        }
        return loopCount
    }
}

val day6Problem = Day6Problem()

fun main() {
    //day6Problem.testData = true
    day6Problem.runBoth(1)
}