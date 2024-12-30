package aoc.year2024

import DailyProblem
import aoc.utils.Coord
import aoc.utils.cardinals
import aoc.utils.parseIntArray

class Day10Problem : DailyProblem<Int>() {

    override val number = 10
    override val year = 2024
    override val name = "Hoof It"

    private lateinit var grid: Array<IntArray>
    private lateinit var heightMap: Map<Int, Set<Coord>>

    // maps every (x,y) location for a given height
    private fun mapHeights() {
        val map = mutableMapOf<Int, MutableSet<Coord>>()

        for (i in 0..9) map[i] = mutableSetOf() // initialise Map

        grid.forEachIndexed { row, ia ->
            ia.forEachIndexed { col, i ->
                map[i]!!.add(Coord(col, row))
            }
        }
        heightMap = map
    }

    override fun commonCode() {
        grid = parseIntArray(getInputText())
        mapHeights()
    }

    // finds every reachable unique (x,y) location at the next height
    private fun findNextSteps(currHeightList: Set<Coord>,
                              heightMap: Map<Int, Set<Coord>>,
                              currHeight: Int): MutableSet<Coord> {

        val nextSteps = mutableSetOf<Coord>()

        currHeightList.forEach { currLoc ->
            cardinals.forEach { offset ->
                val nextPos = currLoc + offset.coord
                if (heightMap[currHeight]!!.contains(nextPos)) {
                    nextSteps.add(nextPos)
                }
            }
        }
        return nextSteps
    }

    // finds each (x,y) location that can be reached from previous steps
    // Note: non-unique locations are included
    private fun findPaths(currHeightList: List<Coord>,
                          heightMap: Map<Int, Set<Coord>>,
                          currHeight: Int): MutableList<Coord> {

        val nextSteps = mutableListOf<Coord>()

        currHeightList.forEach { currLoc ->
            cardinals.forEach { offset ->
                val nextPos = currLoc + offset.coord
                if (heightMap[currHeight]!!.contains(nextPos)) {
                    nextSteps.add(nextPos)
                }
            }
        }
        return nextSteps
    }

    override fun part1(): Int {
        val trailHeadPeaks = mutableMapOf<Coord, MutableSet<Coord>>()

        var nextSteps: Set<Coord>

        // for each trailhead, find each unique peak that can be reached
        heightMap[0]!!.forEach { trailHead ->
            var currHeight = 0
            nextSteps = setOf(trailHead)
            while (++currHeight <= 9) {
                nextSteps = findNextSteps(nextSteps, heightMap, currHeight)
            }
            trailHeadPeaks[trailHead] = nextSteps.toMutableSet()
        }
        return trailHeadPeaks.values.sumOf { it.size } // count of peaks = trail score
    }

    override fun part2(): Int {
        val trailHeadPaths = mutableMapOf<Coord, MutableList<Coord>>()

        var nextSteps: List<Coord>

        // for each trailhead, find all the unique paths to reachable peaks
        heightMap[0]!!.forEach { trailHead ->
            var currHeight = 0
            nextSteps = listOf(trailHead)
            while (++currHeight <= 9) {
                nextSteps = findPaths(nextSteps, heightMap, currHeight)
            }
            trailHeadPaths[trailHead] = nextSteps.toMutableList()
        }
        return trailHeadPaths.values.sumOf { it.size } // count of paths = trail rating
    }
}

val day10Problem = Day10Problem()

fun main() {
    //day10Problem.testData = true
    day10Problem.runBoth(100)
}