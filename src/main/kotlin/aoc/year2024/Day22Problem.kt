package aoc.year2024

import DailyProblem

class Day22Problem : DailyProblem<Long>() {

    override val number = 22
    override val year = 2024
    override val name = "Monkey Market"

    private lateinit var data: List<String>

    /**
     * Calculates Monkey Exchange Market secret numbers
     * (Apologies to Phil Collins)
     * @param secretNum The current secret number
     * @returns the next Pseudorandom number in the sequence
     */
    private fun pseuPseuPseudio(secretNum: Long): Long {
        val step1Mult = secretNum * 64
        val step1Mix = step1Mult xor secretNum
        val step1Prune = step1Mix % 16777216

        val step2Div = step1Prune.floorDiv(32)
        val step2Mix = step2Div xor step1Prune
        val step2Prune = step2Mix % 16777216

        val step3Mult = step2Prune * 2048
        val step3Mix = step3Mult xor step2Prune
        val step3Prune = step3Mix % 16777216

        return step3Prune
    }

    private fun getSequences(priceList: List<List<Int>>): List<Map<List<Int>, Int>> {
        val sequences = mutableListOf<MutableMap<List<Int>, Int>>()
        priceList.forEachIndexed { i, list ->
            sequences.add(mutableMapOf())
            list.forEachIndexed { j, price ->
                if (j >= 4) {
                    val sequence = listOf(
                        list[j - 3] - list[j - 4],
                        list[j - 2] - list[j - 3],
                        list[j - 1] - list[j - 2],
                        list[j] - list[j - 1]
                    )
                    if (!sequences[i].containsKey(sequence)) sequences[i][sequence] = price
                }
            }
        }
        return sequences
    }

    private fun sequenceSums(sequences: List<Map<List<Int>, Int>>): Map<List<Int>, Int> {
        val sums = mutableMapOf<List<Int>, Int>()

        sequences.forEach { buyer ->
            buyer.forEach { (sequence, price) ->
                if (!sums.containsKey(sequence)) sums[sequence] = price
                else sums[sequence] = sums[sequence]!! + price
            }
        }

        return sums
    }

    override fun commonCode() {
        data = getInputText().lines().filter { it.isNotEmpty() }
    }

    override fun part1(): Long {
        var sum = 0L
        data.forEach { line ->
            var secNum = line.toLong()
            for (i in 0..<2000) {
                secNum = pseuPseuPseudio(secNum)
            }
            sum += secNum
        }
        return sum
    }

    override fun part2(): Long {
        if (testData) {
            data = listOf("1","2","3","2024")
        }
        var sum = 0L
        val priceList = mutableListOf<MutableList<Int>>()
        data.forEachIndexed { index, line ->
            var secNum = line.toLong()
            priceList.add(mutableListOf())
            priceList[index].add((secNum % 10).toInt())
            for (i in 0..<2000) {
                secNum = pseuPseuPseudio(secNum)
                priceList[index].add((secNum % 10).toInt())
            }
            sum += secNum
        }
        val sequences = getSequences(priceList)
        val sums = sequenceSums(sequences)
        sum = sums.maxBy { it.value }.value.toLong()
        return sum
    }
}

val day22Problem = Day22Problem()

fun main() {
    //day22Problem.testData = true
    day22Problem.runBoth(1)
}