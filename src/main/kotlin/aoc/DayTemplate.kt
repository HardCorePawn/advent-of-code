package aoc

import DailyProblem

class Day999999Problem : DailyProblem<Int>() {

    override val number = 999999
    override val year = 2024
    override val name = "Problem name"

    private lateinit var data: Any

    override fun commonCode() {
        data = getInputText()
    }

    override fun part1(): Int {
        return 1
    }

    override fun part2(): Int {
        return 1
    }
}

val day999999Problem = Day999999Problem()

fun main() {
    day999999Problem.testData = true
    day999999Problem.runBoth(100)
}