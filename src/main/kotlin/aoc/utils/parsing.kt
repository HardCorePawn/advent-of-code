package aoc.utils

/**
 * Parses a string where each line is a pair of Int values separated by a separator that is given as a constant string.
 * Any empty lines are ignored.
 *
 * Example.
 *
 * parseListOfPairs(s, ",") would parse the string
 * """1,2
 * 11,3
 * 1,4
 * """
 * returns listOf(Pair(1,2), Pair(11,3), Pair(1,4))
 */
fun parseIntPairs(inputText: String, separator: String):List<Pair<Int, Int>> {
    return inputText.lines().filter { it.isNotEmpty() }.map { line ->
        val (a, b) = line.split(separator)
        Pair(a.toInt(), b.toInt())
    }
}
