package aoc.year2024

import DailyProblem
import aoc.utils.Coord
import aoc.utils.cardinals
import java.util.*

class Day18Problem : DailyProblem<String>() {

    override val number = 18
    override val year = 2024
    override val name = "RAM Run"

    private lateinit var input: List<String>
    private lateinit var gridMap: MutableMap<Coord, Char>
    private lateinit var graph: MutableMap<Coord, MutableList<Pair<Coord, Int>>>
    private lateinit var start: Coord
    private lateinit var end: Coord

    private var maxX = -1
    private var maxY = -1
    private var maxBytes = -1


    // Sets up the initial board, returns it as a MutableMap<Coord, Char>
    private fun setupGrid(input: List<String>, maxX: Int, maxY: Int, maxBytes: Int) {
        gridMap = mutableMapOf()

        // fill the board with empty space
        for (y in 0..maxY) {
            for (x in 0..maxX) {
                gridMap[Coord(x,y)] = '.'
            }
        }

        // Take maxBytes number of entries from the input list,
        // and update the Points at those co-ords as being blocked
        input.forEachIndexed { index, line ->
            val coords = line.split(",").map { it.toInt() }
            if (index < maxBytes) gridMap.keys.find { it == Coord(coords[0],coords[1]) }.also { gridMap[it!!] = '#' }
        }
    }

    override fun commonCode() {
        if (this.testData) {
            maxX = 6
            maxY = 6
            maxBytes = 12
        } else {
            maxX = 70
            maxY = 70
            maxBytes = 1024
        }
        input = getInputText().lines().filter { it.isNotEmpty() }
        setupGrid(input, maxX, maxY, maxBytes)
        buildGraph()
    }

    // build the graph for the current board layout, assume all edges have a weight of 1
    private fun buildGraph() {

        graph = mutableMapOf()

        // only interested in non-blocked spaces on the board
        val spaces = gridMap.keys.filter { gridMap[it] == '.' }

        // for each empty space, get the neighbours and add to graph
        spaces.forEach { coord ->
            val neighbours = mutableListOf<Pair<Coord, Int>>()
            cardinals.forEach { dir ->
                val neighbour = spaces.find { it == coord + dir.coord }
                if (neighbour != null) {
                    neighbours.add(neighbour to 1)
                }
            }
            graph[coord] = neighbours
        }
    }

    // Classic Dijkstra's Algorithm that allows for possible Loops
    // Returns the distance from given start Point to given end Point
    // If no valid path exists, returns null
    private fun dijkstraWithLoops(): Int? {
        val distances = mutableMapOf<Coord, Int>().withDefault { Int.MAX_VALUE }
        val priorityQueue = PriorityQueue<Pair<Coord, Int>>(compareBy { it.second })

        // need to know if we've visited this node before to prevent infinite looping (and help with multiple pathing)
        val visited = mutableSetOf<Pair<Coord, Int>>()

        // we start searching from the given start Point
        priorityQueue.add(start to 0)
        distances[start] = 0

        while (priorityQueue.isNotEmpty()) {
            val (node, currentDist) = priorityQueue.poll()
            if (visited.add(node to currentDist)) {
                graph[node]?.forEach { (adjacent, weight) ->
                    val totalDist = currentDist + weight
                    // if new dist <= existing distance to this adjacent node, we've found a (new) shortest path
                    if (totalDist <= distances.getValue(adjacent)) {
                        distances[adjacent] = totalDist
                        priorityQueue.add(adjacent to totalDist)
                    }
                }
            }
        }

        return distances[end]
    }

    override fun part1(): String {
        // start and end are top/left and bottom/right, respectively
        start = graph.keys.find { it.x == 0 && it.y == 0 }!!
        end = graph.keys.find { it.x == maxX && it.y == maxY }!!

        // use a simple version of Dijkstra's Algorithm to calculate the shortest path from Start to End
        return dijkstraWithLoops().toString()
    }

    override fun part2(): String {
        var currByte = maxBytes
        var coords = listOf<Int>()

        // While our Dijkstra's Algorithm can find a path from start to end, keep "dropping bytes" from the input list
        // We achieve this is an efficient way by simply removing the location from the node graph
        while (dijkstraWithLoops() != null) {
            coords = input[currByte++].split(",").map { it.toInt() }
            graph.remove(Coord(coords[0], coords[1]))
        }

        // Output Co-ord's that finally blocked the path from start to end
        return "${coords[0]},${coords[1]}"
    }
}

val day18Problem = Day18Problem()

fun main() {
    //day18Problem.testData = true
    day18Problem.runBoth(1)
}