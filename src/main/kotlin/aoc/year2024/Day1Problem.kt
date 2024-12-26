package aoc.year2024

import DailyProblem
import aoc.utils.parseIntPairs
import kotlin.math.absoluteValue

class Day1Problem : DailyProblem<Int>() {

    override val number = 1
    override val year = 2024
    override val name = "Historian Hysteria"

    private lateinit var leftList: List<Int>
    private lateinit var rightList: List<Int>

    override fun commonCode() {
        // parse the Int pairs from the input and split into the left and right lists.
        val lists = parseIntPairs(getInputText(), "   ").unzip()
        leftList = lists.first
        rightList = lists.second
    }

    override fun part1(): Int {
        // Total Distance between the sorted lists
        return leftList.sorted().zip(rightList.sorted()).sumOf { (a, b) -> (a - b).absoluteValue }
    }

    override fun part2(): Int {
        return leftList.sumOf { l -> l * rightList.count { it == l } }
    }
}

val day1Problem = Day1Problem()

fun main() {
    //day1Problem.testData = true
    day1Problem.runBoth(100)
}