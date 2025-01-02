package aoc.year2024

import DailyProblem
import aoc.utils.*

class Day20Problem : DailyProblem<Int>() {

    override val number = 20
    override val year = 2024
    override val name = "Race Condition"

    private lateinit var pathPoints: Map<Coord, Char>
    private lateinit var pathGraph: Map<Coord, Int>

    private var minSave = 0

    // Finds all points on the path
    private fun getPathPoints(input: List<String>): Map<Coord, Char> {
        val path = mutableMapOf<Coord, Char>()

        input.forEachIndexed { row, line ->
            line.forEachIndexed { col, c ->
                if (c == 'S' || c == '.' || c == 'E') {
                    path[Coord(col, row)] = c
                }
            }
        }
        return path
    }

    // for a given point, find all the neighbouring points on the path
    private fun getNeighbours(point: Coord): List<Coord> {
        val neighbours = mutableListOf<Coord>()
        cardinals.forEach {
            if (pathPoints[point + it.coord] != null) {
                neighbours.add(point + it.coord)
            }
        }
        return neighbours
    }

    // find next point on the path that we haven't visited
    private fun getNextPoint(currPoint: Coord, prevPoint: Coord): Coord {
        val neighbours = getNeighbours(currPoint)
        return neighbours.filter { it != prevPoint }[0]
    }

    // builds a graph to track the distances travelled for each point on the path
    private fun createPathGraph(): Map<Coord, Int> {
        val graph = mutableMapOf<Coord, Int>()

        // start at end and work backwards
        var currPoint = pathPoints.filterValues { it == 'S' }.keys.first()
        var dist = 0
        graph[currPoint] = dist++

        var prevPoint = Coord(-1, -1)
        var nextPoint: Coord
        do {
            nextPoint = getNextPoint(currPoint, prevPoint)
            graph[nextPoint] = dist++
            prevPoint = currPoint
            currPoint = nextPoint
        } while (pathPoints[currPoint] != 'E')

        return graph
    }

    // find points where we can take a shortcut that results in a minSave of steps
    // Essentially we check if we can move 2 steps in a direction and end up back on the path, with the first
    // step being through a wall
    // Then we check that the distance saved
    private fun findShortCuts(point: Coord, dist: Int, minSave: Int): List<Pair<Coord, Coord>> {
        // return list of <start, end> Pairs for shortcuts
        return pathGraph.filter {
            it.value < dist - minSave && (
                        (it.key.x == point.x && it.key.y == point.y - 2 &&
                            !pathGraph.containsKey(point + Direction.UP.coord)) ||
                        (it.key.x == point.x && it.key.y == point.y + 2 &&
                            !pathGraph.containsKey(point + Direction.DOWN.coord)) ||
                        (it.key.x == point.x - 2 && it.key.y == point.y &&
                            !pathGraph.containsKey(point + Direction.LEFT.coord)) ||
                        (it.key.x == point.x + 2 && it.key.y == point.y &&
                            !pathGraph.containsKey(point + Direction.RIGHT.coord))
                    )
        }.map { point to it.key }.toList()
    }

    // from a given point, find all the points on the path that are within a Manhattan Distance of 20
    // Then we check that the distance already travelled + cheat path length puts us on a point
    // that has the required amount of savings
    private fun findLongShortCuts(point: Coord, dist: Int, minSave: Int): List<Pair<Coord, Coord>> {
        return pathGraph.filter {
            it.key.manhattanDistTo(point) <= 20
                    && dist + it.key.manhattanDistTo(point) <= it.value - minSave
        }.map { point to it.key }.toList()
    }

    override fun commonCode() {
        pathPoints = getPathPoints(getInputText().lines().filter { it.isNotEmpty() })
        pathGraph = createPathGraph()
    }

    override fun part1(): Int {
        minSave = when (testData) {
            true -> 10
            false -> 100
        }
        val shortcuts = mutableSetOf<Pair<Coord, Coord>>()
        pathGraph.forEach { (point, dist) ->
            shortcuts += findShortCuts(point, dist, minSave)
        }
        return shortcuts.size
    }

    override fun part2(): Int {
        minSave = when (testData) {
            true -> 72
            false -> 100
        }
        val longShortcuts = mutableSetOf<Pair<Coord, Coord>>()
        pathGraph.forEach { (point, dist) ->
            longShortcuts += findLongShortCuts(point, dist, minSave)
        }
        return longShortcuts.size
    }
}

val day20Problem = Day20Problem()

fun main() {
    //day20Problem.testData = true
    day20Problem.runBoth(1)
}