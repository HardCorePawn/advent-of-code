package aoc.year2024

import DailyProblem
import aoc.utils.parseIntLists

class Day2Problem : DailyProblem<Int>() {

    override val number = 2
    override val year = 2024
    override val name = "Red-Nosed Reports"

    private lateinit var reports: List<List<Int>>

    override fun commonCode() {
        // parse the Int lists from the input
        reports = parseIntLists(getInputText(), " ")
    }

    override fun part1(): Int {
        //Return the count of valid lines
        return reports.filter { validLine(it) }.size
    }

    override fun part2(): Int {
        // return the count of valid lines, but now allowing for exactly one error per line
        return reports.filter { line -> validLine(line) || dropOneFromList(line).any { validLine(it) } }.size
    }

    // build all combinations of a given list with 1 element removed
    private fun dropOneFromList(line: List<Int>): List<List<Int>> {
        val newList = buildList {
            for (i in line.indices) {
                add(line.take(i) + line.drop(i+1))
            }
        }
        return newList
    }

    // A line is valid if:
    // - The levels are either all increasing or all decreasing.
    // - Any two adjacent levels differ by at least one and at most three.
    private fun validLine(line: List<Int>): Boolean {
        val diffs = line.zipWithNext { a, b -> a - b }
        return diffs.all { it in 1..3 } || diffs.all { it in -3..-1 }
    }
}

val day2Problem = Day2Problem()

fun main() {
    //day2Problem.testData = true
    day2Problem.runBoth(100)
}