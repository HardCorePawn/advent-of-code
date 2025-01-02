package aoc.year2024

import DailyProblem

class Day19Problem : DailyProblem<Long>() {

    override val number = 19
    override val year = 2024
    override val name = "Linen Layout"

    private lateinit var patterns: List<String>
    private lateinit var designs: List<String>
    private lateinit var validDesigns: List<String>
    // memoization? we've heard of it ;)
    private var cache = mutableMapOf<String, Long>()

    // recursively check the design to see if we can reconstruct it using the available towels patterns
    private fun checkDesign(design: String): Boolean {

        patterns.filter { it.length <= design.length }.forEach { pattern ->
            // can the design can be matched to a single towel pattern or a combination of patterns?
            if (design == pattern ||
                (design.startsWith(pattern) && checkDesign(design.substringAfter(pattern)))
            ) {
                return true
            }
        }
        return false // no matches possible for this design
    }

    // Returns a list of all the designs that can be constructed using any available towel patterns
    private fun checkDesigns(): List<String> {
        val validDesigns = mutableListOf<String>()

        designs.forEach { design ->
            if (checkDesign(design)) {
                validDesigns.add(design)
            }
        }
        return validDesigns
    }

    // Find the total number of ways a design can be constructed from any of the available towel patterns
    // We use memoization to cache the results, so this will actually finish before the heat death of the universe
    private fun validCombos(design: String): Long = cache.getOrPut(design) {
        var count = 0L

        patterns.filter { it.length <= design.length }.forEach { towel ->
            if (design == towel) count++
            else {
                if (design.startsWith(towel)) {
                    count += validCombos(design.substringAfter(towel))
                }
            }
        }
        count
    }

    override fun commonCode() {
        val lines = getInputText().lines().filter { it.isNotEmpty() }
        patterns = lines[0].split(", ").sortedByDescending { it.length }
        designs = lines.subList(1, lines.size)
        validDesigns = checkDesigns()
    }

    override fun part1(): Long {
        return validDesigns.size.toLong()
    }

    override fun part2(): Long {
        return validDesigns.sumOf { design -> validCombos(design) }
    }
}

val day19Problem = Day19Problem()

fun main() {
    //day19Problem.testData = true
    day19Problem.runBoth(100)
}