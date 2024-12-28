package aoc.year2024

import DailyProblem
import aoc.utils.Coord
import aoc.utils.parseCharArray
import aoc.utils.withinBounds

class Day8Problem : DailyProblem<Int>() {

    override val number = 8
    override val year = 2024
    override val name = "Resonant Collinearity"

    private lateinit var grid: Array<CharArray>
    private lateinit var antennaMap: Map<Char, List<Coord>>
    private lateinit var antennaPairs: Map<Char, List<Pair<Coord, Coord>>>

    override fun commonCode() {
        grid = parseCharArray(getInputText())
        antennaMap = mapAntennas((grid))
        antennaPairs = mapAntennaPairs(antennaMap)
    }

    //Traverse the board and create a Map containing the co-ords of each antenna type (char symbol)
    private fun mapAntennas(grid: Array<CharArray>): Map<Char, List<Coord>> {
        val antennaMap = mutableMapOf<Char, MutableList<Coord>>()

        grid.forEachIndexed { row, ca ->
            ca.forEachIndexed { col, c ->
                if (c.isLetterOrDigit()) {
                    if (antennaMap[c] != null) {
                        antennaMap[c]!!.add(Coord(col,row))
                    } else {
                        antennaMap[c] = mutableListOf(Coord(col,row))
                    }
                }
            }
        }
        return antennaMap
    }

    //Creates a map of all the unique pairs of antennas of a given type
    private fun mapAntennaPairs(map: Map<Char, List<Coord>>): Map<Char, List<Pair<Coord, Coord>>> {
        val antennaPairs = mutableMapOf<Char, MutableList<Pair<Coord, Coord>>>()

        map.forEach { (c, coords) ->
            antennaPairs[c] = mutableListOf()
            coords.forEachIndexed { i, coordi ->
                coords.drop(i + 1).forEach { coordj ->
                    antennaPairs[c]!!.add(coordi to coordj)
                }
            }
        }

        return antennaPairs
    }

    override fun part1(): Int {
        val antiNodes = mutableSetOf<Coord>()
        antennaPairs.forEach { (_, pairs) ->
            pairs.forEach { pair ->
                val delta = pair.second - pair.first
                val invDelta = delta * -1
                if (withinBounds(pair.second + delta, grid)) {
                    antiNodes.add(pair.second + delta)
                }
                if (withinBounds(pair.first + invDelta, grid)) {
                    antiNodes.add(pair.first + invDelta)
                }
            }
        }
        return antiNodes.size
    }

    override fun part2(): Int {
        val antiNodes = mutableSetOf<Coord>()
        antennaPairs.forEach { (_, pairs) ->
            pairs.forEach { pair ->
                val delta = pair.second - pair.first
                val invDelta = delta * -1

                var nodeLoc = pair.first
                while (withinBounds(nodeLoc + delta, grid)) {
                    nodeLoc += delta
                    antiNodes.add(nodeLoc)
                }
                nodeLoc = pair.second
                while (withinBounds(nodeLoc + invDelta, grid)) {
                    nodeLoc += invDelta
                    antiNodes.add(nodeLoc)
                }
            }
        }
        return antiNodes.size
    }
}

val day8Problem = Day8Problem()

fun main() {
    //day8Problem.testData = true
    day8Problem.runBoth(100)
}