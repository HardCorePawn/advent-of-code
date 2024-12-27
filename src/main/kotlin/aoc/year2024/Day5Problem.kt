package aoc.year2024

import DailyProblem
import java.util.*

class Day5Problem : DailyProblem<Int>() {

    override val number = 5
    override val year = 2024
    override val name = "Print Queue"

    private lateinit var rules: MutableMap<Int, MutableMap<String, MutableList<Int>>>
    private lateinit var updates: MutableList<MutableList<Int>>
    private lateinit var badUpdates: MutableList<MutableList<Int>>

    private fun parseRule(rule: String) {
        val (num1, num2) = rule.split("|").map { it.toInt() }

        if (rules.containsKey(num1)) {
            rules[num1]!!["Before"]!!.add(num2)
        } else {
            rules[num1] = mutableMapOf("Before" to mutableListOf(num2), "After" to mutableListOf())
        }
        if (rules.containsKey(num2)) {
            rules[num2]!!["After"]!!.add(num1)
        } else {
            rules[num2] = mutableMapOf("After" to mutableListOf(num1), "Before" to mutableListOf())
        }
    }

    private fun parseUpdate(update: String) {
        updates.add(update.split(",").map { it.toInt() }.toMutableList())
    }

    private fun parseInput(input: String) {
        input.lines().filter { it.isNotEmpty() }.forEach { line ->
            if (line.contains("|")) parseRule(line)
            else parseUpdate(line)
        }
    }

    override fun commonCode() {
        rules = mutableMapOf()
        updates = mutableListOf()
        badUpdates = mutableListOf()
        parseInput(getInputText())
    }

    override fun part1(): Int {
        val validUpdates = mutableListOf<MutableList<Int>>()

        updates.forEach next@ { update ->
            update.forEachIndexed { i, num ->
                if (rules[num]!!["Before"]!!.any { update.subList(0, i).contains(it) }) {
                    badUpdates.add(update) // stash bad for Part 2
                    return@next
                } else if (rules[num]!!["After"]!!.any { update.subList(i, update.lastIndex).contains(it) }) {
                    badUpdates.add(update) // stash bad for Part 2
                    return@next
                }
            }
            validUpdates.add(update) // no rules failed
        }
        return validUpdates.sumOf { it[it.indices.last/2] } // sum of middle pages
    }

    override fun part2(): Int {

        // correct the order of bad updates found in Part 1
        badUpdates.forEach { update ->
            for (x in update.indices.first..<update.indices.last) {
                //if current number is not "Before" the next number, swap them
                if (!rules[update[x]]!!["Before"]!!.contains(update[x + 1])) {
                    Collections.swap(update, x, x + 1)
                    for (y in x downTo 1) {
                        // check the newly moved number against previous numbers in the list
                        if (rules[update[y]]!!["Before"]!!.contains(update[y - 1])) {
                            Collections.swap(update, y, y - 1)
                        }
                    }
                }
            }
        }
        return badUpdates.sumOf { it[it.indices.last/2] } // sum of middle pages
    }
}

val day5Problem = Day5Problem()

fun main() {
    //day5Problem.testData = true
    day5Problem.runBoth(100)
}