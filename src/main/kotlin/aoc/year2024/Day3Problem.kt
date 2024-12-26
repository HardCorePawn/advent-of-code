package aoc.year2024

import DailyProblem

class Day3Problem : DailyProblem<Int>() {

    override val number = 3
    override val year = 2024
    override val name = "Mull It Over"

    private lateinit var data: String
    private val mulRegex = Regex("mul\\((\\d{1,3}),(\\d{1,3})\\)")
    private val excludeDontDoRegex = Regex("don't\\(\\).*?do\\(\\)")

    override fun commonCode() {
        data = getInputText()
    }

    private fun findAndEvalMuls(input: String): Int {
        // return the sum of all valid mul(x,y) blocks in the input
        val matches = mulRegex.findAll(input)
        return matches.sumOf { match ->
            match.groupValues[1].toInt() * match.groupValues[2].toInt()
        }
    }

    override fun part1(): Int {
        return findAndEvalMuls(data)
    }

    override fun part2(): Int {
        // Strip the \n chars from input data so the exclude regex can match across multiple lines
        // Add a "do()" to the end of the input so everything after the last don't() is excluded correctly.
        // Split input data using the (don't ... do) blocks as the delimiter to remove these blocks
        // Join the split data parts back into a new data string
        // Finally, eval the mul(x,y) blocks that remain in the new data string
        val newData = excludeDontDoRegex.splitToSequence(data.replace("\n","") + "do()").joinToString()
        return findAndEvalMuls(newData)
    }
}

val day3Problem = Day3Problem()

fun main() {
    //day3Problem.testData = true
    day3Problem.runBoth(100)
}