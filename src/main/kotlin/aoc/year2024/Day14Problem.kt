package aoc.year2024

import DailyProblem
import aoc.utils.Coord

class Day14Problem : DailyProblem<Int>() {

    // pos = (x,y) position of Robot
    // move = (x,y) movement vector of Robot
    class Robot(var pos: Coord, var moveVector: Coord) {

        // update the Robot's (x,y) position using its (x,y) movement vector
        fun move(maxX: Int, maxY: Int) {
            val newPos = pos + moveVector

            if (newPos.x < 0) {
                newPos.x += maxX
            } else if (newPos.x >= maxX) {
                newPos.x -= maxX
            }
            if (newPos.y < 0) {
                newPos.y += maxY
            } else if (newPos.y >= maxY) {
                newPos.y -= maxY
            }

            this.pos = newPos
        }
    }

    override val number = 14
    override val year = 2024
    override val name = "Restroom Redoubt"

    private lateinit var robots: MutableList<Robot>

    // Use a regex to parse each robot's (x,y) position and (x,y) movement vector
    private fun parseInput(input: String) {
        robots = mutableListOf()

        input.lines().filter { it.isNotEmpty() }.forEach { line ->
            val botRegex = Regex("p=(\\d+),(\\d+).v=(-?\\d+),(-?\\d+)")
            val match = botRegex.find(line)

            val pos = Coord(match!!.groupValues[1].toInt(), match.groupValues[2].toInt())
            val move = Coord(match.groupValues[3].toInt(), match.groupValues[4].toInt())

            robots.add(Robot(pos, move))
        }
    }

    // generates a map of the board of current robot locations
    private fun displayBoard(maxX: Int, maxY: Int) {
        val board = mutableMapOf<Coord, Char>()

        // fill the board with '.'s
        for (y in 0..<maxY) {
            for (x in 0..<maxX) {
                board[Coord(x,y)] = '.'
            }
        }

        // mark each robot location with a '#'
        robots.forEach { robot ->
            board[robot.pos] = '#'
        }

        // print the board
        for (y in 0..<maxY) {
            for (x in 0..<maxX) {
                print(board[Coord(x,y)])
            }
            println()
        }
    }

    override fun commonCode() {
        parseInput(getInputText())
    }

    override fun part1(): Int {
        // set max board size
        //val maxX = 11 // test values
        //val maxY = 7  // test values
        val maxX = 101
        val maxY = 103

        // find the middle row and column so we can ignore them
        val midX = (maxX - 1) / 2
        val midY = (maxY - 1) / 2

        // move each robot 100 times
        robots.forEach { robot ->
            repeat(100) { robot.move(maxX, maxY) }
        }

        // create the quadrant lists
        val firstQuad = robots.filter { it.pos.x < midX && it.pos.y < midY }
        val secondQuad = robots.filter { it.pos.x > midX && it.pos.y < midY }
        val thirdQuad = robots.filter { it.pos.x < midX && it.pos.y > midY }
        val fourthQuad = robots.filter { it.pos.x > midX && it.pos.y > midY }

        // return the total safety factor by multiplying all the quadrant counts
        return firstQuad.size * secondQuad.size * thirdQuad.size * fourthQuad.size
    }

    override fun part2(): Int {
        // max board size
        val maxX = 101
        val maxY = 103

        var seconds = 100 // we already moved the robots for 100 seconds in Part 1

        while (true) {
            // move each robot
            robots.forEach { robot ->
                robot.move(maxX, maxY)
            }
            // time passes...
            seconds++

            // We continue until there are no positions occupied by multiple robots
            // This should mean the robots have moved into the tree pattern
            if (robots.size == robots.distinctBy { it.pos }.size) break
        }

        // output the pretty picture
        displayBoard(maxX, maxY)

        // return the total number of seconds it took for the robots to align
        return seconds
    }
}

val day14Problem = Day14Problem()

fun main() {
    //day14Problem.testData = true
    day14Problem.runBoth(1)
}