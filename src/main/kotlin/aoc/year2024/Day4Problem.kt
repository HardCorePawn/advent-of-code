package aoc.year2024

import DailyProblem
import aoc.utils.Coord
import aoc.utils.parseCharArray

class Day4Problem : DailyProblem<Int>() {

    override val number = 4
    override val year = 2024
    override val name = "Ceres Search"

    private lateinit var grid: Array<CharArray>
    private lateinit var xRange: IntRange
    private lateinit var yRange: IntRange

    private val dirs = listOf(
        Coord(1,-1), // up-right
        Coord(1,0),  // right
        Coord(1,1),  // down-right
        Coord(0,-1), // up
        Coord(0,1),  // down
        Coord(-1,-1),// up-left
        Coord(-1,0), // left
        Coord(-1,1)  // down-left
    )

    private val corners = listOf(
        dirs[0], // up-right
        dirs[7], // down-left
        dirs[5], // up-left
        dirs[2], // down-right
    )

    override fun commonCode() {
        grid = parseCharArray(getInputText())
        xRange = grid[0].indices
        yRange = grid.indices
    }

    // is the coord on the grid?
    private fun validCoord(coord: Coord): Boolean {
        return coord.x in xRange && coord.y in yRange
    }

    private fun findWord(index: Int, word: String, grid: Array<CharArray>, coord: Coord, move: Coord): Boolean {
        // found the whole word
        if (index == word.length) return true

        // check next step in direction to see if it matches the next letter in the word
        if (validCoord(coord) && word[index] == grid[coord.x][coord.y]) {
            return findWord(index + 1, word, grid, Coord(coord.x + move.x, coord.y + move.y), move)
        }

        // next char didn't match
        return false
    }

    private fun findXMAS(coord: Coord, grid: Array<CharArray>): Boolean {
        // don't want to start on edges
        if (coord.x != 0 && coord.x != xRange.last && coord.y != 0 && coord.y != yRange.last) {
            //Is this the middle of a potential X-MAS?
            if (grid[coord.x][coord.y] == 'A') {
                //if so, check the corners
                val topright = Coord((coord + corners[0]).x, (coord + corners[0]).y)
                val botleft = Coord((coord + corners[1]).x, (coord + corners[1]).y)
                val topleft = Coord((coord + corners[2]).x, (coord + corners[2]).y)
                val botright = Coord((coord + corners[3]).x, (coord + corners[3]).y)

                // diagonally opposite corners should be different:
                // MS     SM            MS     SM
                // SM and MS are valid  SM and MS are not
                val forwardSlash = listOf(grid[topright.x][topright.y], grid[botleft.x][botleft.y]).joinToString("")
                val backSlash = listOf(grid[topleft.x][topleft.y], grid[botright.x][botright.y]).joinToString("")
                val mas = setOf("MS", "SM")
                return mas.contains(forwardSlash) && mas.contains(backSlash)
            }
        }
        return false
    }

    override fun part1(): Int {
        val word = "XMAS"

        return xRange.sumOf { i ->
            yRange.sumOf { j ->
                dirs.filterIndexed { k, _ ->
                    findWord(0, word, grid, Coord(i, j), dirs[k])
                }.size
            }
        }
    }

    override fun part2(): Int {
        return xRange.sumOf { i ->
            yRange.filter { j ->
                findXMAS(Coord(i, j), grid)
            }.size
        }
    }
}

val day4Problem = Day4Problem()

fun main() {
    //day4Problem.testData = true
    day4Problem.runBoth(100)
}