package aoc.year2024

import DailyProblem

class Day7Problem : DailyProblem<Long>() {

    override val number = 7
    override val year = 2024
    override val name = "Bridge Repair"

    private lateinit var data: Map<Long, List<Long>>
    private lateinit var goodequations: Map<Long, List<Long>>
    private lateinit var badequations: Map<Long, List<Long>>

    private fun <T> evaluate(target: Long, operands: List<T>, operators: List<(T, T) -> T>, depth: Int = 0): Boolean {
        // If we only have one operand, we've reached the end of the list, did we hit the target?
        if (operands.size == 1) {
            return (operands[0] == target)
        }

        // Try each operator between every pair of operands (working left to right)
        for (operator in operators) {
            // Combine the operands at index 0 and 1 with the operator.
            val combined = operator(operands[0], operands[1])

            // Create a new list with the combined result replacing the two operands.
            val newOperands = operands.toMutableList()
            newOperands[0] = combined
            newOperands.removeAt(1)

            // Recursively evaluate the new list of operands.
            if (evaluate(target, newOperands, operators, depth + 1)) return true
        }

        return false
    }

    private fun parseValuesAndOperands(input: String): Map<Long, List<Long>> {
        val valsAndOps = mutableMapOf<Long, List<Long>>()

        input.lines().filter { it.isNotEmpty() }.forEach { line ->
            val value = line.substringBefore(":").toLong()
            val opsString = line.substringAfter(": ")
            val opsList = opsString.split(" ").map { it.toLong() }
            valsAndOps[value] = opsList
        }
        return valsAndOps
    }

    override fun commonCode() {
        data = parseValuesAndOperands(getInputText())
        goodequations = mutableMapOf()
        badequations = mutableMapOf()
    }

    override fun part1(): Long {
        goodequations = data.filter { (target, operands) ->
            // We create our "operators" as lambda's
            val operators = listOf<(Long, Long) -> Long>(
                { a, b -> a + b }, // Addition
                { a, b -> a * b }  // Multiplication
            )
            when (evaluate(target, operands, operators)) {
                true -> true
                false -> { badequations += target to operands // track bad equations for Part 2
                    false
                }
            }
        }
        return goodequations.keys.sum()
    }

    override fun part2(): Long {
        // only attempt new concat operator on known "bad" equations
        return badequations.filter { (target, operands) ->
            // We create our "operators" as lambda's
            val operators = listOf<(Long, Long) -> Long>(
                { a, b -> a + b }, // Addition
                { a, b -> a * b }, // Multiplication
                { a, b -> (a.toString() + b.toString()).toLong() } // Concatenation
            )
            // if we successfully match our target value from some combination of operators, it's a good equation
            evaluate(target, operands, operators)
        }.keys.sum() + goodequations.keys.sum() // return sum of new and previous good equations
    }
}

val day7Problem = Day7Problem()

fun main() {
    //day7Problem.testData = true
    day7Problem.runBoth(10)
}