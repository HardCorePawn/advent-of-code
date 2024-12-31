package aoc.year2024

import DailyProblem
import aoc.utils.parseIntLists

class Day11Problem : DailyProblem<Long>() {

    override val number = 11
    override val year = 2024
    override val name = "Plutonian Pebbles"

    private lateinit var initialStones: List<Int>
    //This Map will hold already "solved" (StartNum,Blinks), to avoid a LOT of repetition/recalculation
    private lateinit var solved: MutableMap<Pair<Long, Int>, Long>

    // Recursive function to find the number of stones created by specific stone number after
    // a specific number of blinks
    //
    // The "solved" Map tracks already calculated blinks, where key = (startNum, blinksLeft)
    // It returns the number of stones that this combination would generate to avoid a lot of repetition
    // during calculations
    private fun solveStartNum(startNum: Long, blinksLeft: Int): Long {

        if (blinksLeft == 0) {
            // no blinks left, so the stone can't split, this combination results in exactly 1 stone
            return 1
        } else if (startNum to blinksLeft !in solved) {
            // we haven't seen this combination, so we need to solve it
            val numString = startNum.toString() // convert to String to make the split easier, if required

            // add new solved value
            solved[startNum to blinksLeft] = when {
                // if the number is zero, next value to solve 1
                startNum == 0L -> solveStartNum(1, blinksLeft - 1)
                // if the num has an even amount of digits, split in half and solve the 2 new numbers
                numString.length % 2 == 0 -> {
                    //.toLong() helps remove leading 0's
                    val leftDigits = numString.substring(0 until numString.length / 2).toLong()
                    val rightDigits = numString.substring(numString.length / 2 until numString.length).toLong()
                    // now solve each half, sum of the solutions for the 2 halves is what we want for original startNum
                    solveStartNum(leftDigits, blinksLeft - 1) + solveStartNum(
                        rightDigits,
                        blinksLeft - 1
                    )
                }
                // otherwise, solve for the number * 2024
                else -> solveStartNum(startNum * 2024, blinksLeft - 1)
            }
        }
        // return the new solved value
        return solved.getValue(startNum to blinksLeft)
    }

    override fun commonCode() {
        initialStones = parseIntLists(getInputText(), " ")[0].toMutableList()
        solved = mutableMapOf()
    }

    // refactored using learnings from part2 to speed things up
    override fun part1(): Long {
        val totalBlinks = 25

        // get the total number of stones generated by each starting stone for the given number of blinks
        return initialStones.sumOf { solveStartNum(it.toLong(), totalBlinks) }
    }

    // Was forced to get a bit more creative here, as naively creating new stones, results in a LOT
    // of repetition/recalculation and long processing time.
    // Came up with a method to "remember" some combinations of stones/# of blinks to help reduce that
    override fun part2(): Long {
        val totalBlinks = 75

        // get the total number of stones generated by each starting stone for the given number of blinks
        return initialStones.sumOf { solveStartNum(it.toLong(), totalBlinks) }
    }
}

val day11Problem = Day11Problem()

fun main() {
    //day11Problem.testData = true
    day11Problem.runBoth(100)
}