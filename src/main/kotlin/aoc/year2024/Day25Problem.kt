package aoc.year2024

import DailyProblem

class Day25Problem : DailyProblem<Int>() {

    data class Lock(val pins: MutableList<Int>)
    data class Key(val heights: MutableList<Int>)

    override val number = 25
    override val year = 2024
    override val name = "Code Chronicle"

    private lateinit var locks: MutableList<Lock>
    private lateinit var keys: MutableList<Key>

    private fun parseInput(input: List<String>) {
        var inLock = false
        var lock = Lock(mutableListOf(0, 0, 0, 0, 0))
        var key = Key(mutableListOf(5, 5, 5, 5, 5))
        input.forEachIndexed { i, s ->
            if (i % 8 == 0) {
                // first line of block has no '.' == Lock, else Key.
                if (!s.contains(".")) {
                    inLock = true
                    lock = Lock(mutableListOf(0, 0, 0, 0, 0))
                } else {
                    key = Key(mutableListOf(5, 5, 5, 5, 5))
                }
            } else if (i % 8 == 6) {
                // reached the end, add the current lock or key to the appropriate list
                if (inLock) locks.add(lock)
                else keys.add(key)
            } else if (i % 8 == 7) {
                // reset key/lock flag for next block
                inLock = false
            } else {
                s.forEachIndexed { index, c ->
                    if (c == '#') {
                        // for locks, we increment the pin height if the current column has a #
                        if (inLock) lock.pins[index]++
                    } else {
                        // for keys, we decrement the column height it the current column has .
                        if (!inLock) key.heights[index]--
                    }
                }
            }
        }
    }

    override fun commonCode() {
        locks = mutableListOf()
        keys = mutableListOf()
        parseInput(getInputText().lines())
    }

    override fun part1(): Int {
        var count = 0
        var fit = true
        // check all keys against all locks. if the combined length of a given lock pin and column height is
        // greater than the available space (5), then the key won't fit
        locks.forEach { lock ->
            keys.forEach { key ->
                key.heights.forEachIndexed { k, height ->
                    if (height + lock.pins[k] > 5) fit = false
                }
                if (fit) count++
                fit = true
            }
        }
        return count // of key/lock combinations that work
    }

    override fun part2(): Int {
        return 1
    }
}

val day25Problem = Day25Problem()

fun main() {
    //day25Problem.testData = true
    day25Problem.runBoth(100)
}