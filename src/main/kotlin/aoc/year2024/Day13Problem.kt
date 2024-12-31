package aoc.year2024

import DailyProblem

class Day13Problem : DailyProblem<Long>() {

    // buttonA = (X offset, Y offset)
    // buttonB = (X offset, Y offset)
    // prize = (x,y) location of prize
    data class ClawMachine(var buttonA: Pair<Long, Long>, var buttonB: Pair<Long, Long>, var prize: Pair<Long, Long>)

    override val number = 13
    override val year = 2024
    override val name = "Claw Contraption"

    private lateinit var machines: MutableList<ClawMachine>

    private fun parseInput(input: String) {

        machines = mutableListOf()

        var buttonA = -1L to -1L
        var buttonB = -1L to -1L
        var prize: Pair<Long, Long>

        input.lines().filter { it.isNotEmpty() }.forEach { string ->
            if (string.startsWith("Button A")) {
                // parse Button A
                val xRegex = Regex("X\\+(\\d+), Y\\+(\\d+)")
                val match = xRegex.find(string)
                buttonA = match!!.groupValues[1].toLong() to match.groupValues[2].toLong()
            } else if (string.startsWith("Button B")) {
                // parse Button B
                val xRegex = Regex("X\\+(\\d+), Y\\+(\\d+)")
                val match = xRegex.find(string)
                buttonB = match!!.groupValues[1].toLong() to match.groupValues[2].toLong()
            } else if (string.startsWith("Prize")) {
                // parse prize location
                val xRegex = Regex("X=(\\d+), Y=(\\d+)")
                val match = xRegex.find(string)
                prize = match!!.groupValues[1].toLong() to match.groupValues[2].toLong()
                // add machine to list
                machines.add(ClawMachine(buttonA, buttonB, prize))
            }
        }
    }

    private fun solveMachine(machine: ClawMachine, offset: Long): Long {
        val aCost = 3L
        val bCost = 1L

        // The claw machines are 2 variable linear equations
        // effectively:
        // ButtonAX(x) + ButtonBX(y) = prizeX
        // ButtonAY(x) + ButtonBY(y) = prizeY
        // we just need to solve for x and y

        val buttonAX = machine.buttonA.first
        val buttonAY = machine.buttonA.second

        val buttonBX = machine.buttonB.first
        val buttonBY = machine.buttonB.second

        // adjust the prize location (if required)
        val prizeX = machine.prize.first + offset
        val prizeY = machine.prize.second + offset

        // calculate the determinant
        //
        // | (de)  (ant) | = (de)(ter) - (min)(ant)
        // | (min) (ter) |
        //
        // | ButtonAX  ButtonBX | => (ButtonAX * ButtonBY) - (ButtonAY * ButtonBX)
        // | ButtonAY  ButtonBY |
        //
        val determinant = (buttonAX * buttonBY) -
                (buttonAY * buttonBX)

        // Need to solve:
        //
        //    | PrizeX ButtonBX |      | ButtonAX PrizeX |
        // x= | PrizeY ButtonBY |   y= | ButtonAY PrizeY |
        //    -------------------      -------------------
        //        determinant              determinant
        //
        val topX = (prizeX * buttonBY) - (prizeY * buttonBX)
        val topY = (buttonAX * prizeY) - (buttonAY * prizeX)

        // Check tops are evenly divisible by determinant, otherwise there is no solution
        if (topX % determinant == 0L) {
            if (topY % determinant == 0L) {
                // everything checks out, so we can solve for x & y
                val x = topX / determinant
                val y = topY / determinant

                // multiply x & y by appropriate button costs and add to running total
                return (x * aCost) + (y * bCost)
            }
        }
        return 0L
    }

    override fun commonCode() {
        parseInput(getInputText())
    }

    override fun part1(): Long {
        return machines.sumOf { machine -> solveMachine(machine, 0) }
    }

    override fun part2(): Long {
        return machines.sumOf { machine -> solveMachine(machine, 10000000000000) }
    }
}

val day13Problem = Day13Problem()

fun main() {
    //day13Problem.testData = true
    day13Problem.runBoth(100)
}