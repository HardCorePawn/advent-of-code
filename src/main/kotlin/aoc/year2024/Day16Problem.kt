package aoc.year2024

import DailyProblem
import aoc.utils.*
import java.util.*

class Day16Problem : DailyProblem<Int>() {

    override val number = 16
    override val year = 2024
    override val name = "Reindeer Maze"

    private lateinit var maze: Map<Coord, Char>
    private lateinit var graph: MutableMap<Coord, List<Pair<Coord, Int>>>
    private lateinit var start: Coord
    private lateinit var end: Coord
    private lateinit var distances: MutableMap<Coord, Int>
    private lateinit var paths: MutableMap<Coord, MutableList<Coord>>

    // Implementation of Dijkstra's Algorithm for finding the shortest paths between a given start point and all other
    // points in the given graph.
    //
    // Notes:
    // Algorithm has been modified slightly, so that each point tracks all parent nodes which are on any shortest
    // path back to the start point. This enables us to retrace *all* the shortest paths from a given end point back
    // to the start
    //
    // There is also a dirty "hack" to modify the edge weight on the fly when we're "turning" through a crossroad
    // section, as it was not possible to account for these situations in the graph creation
    private fun dijkstraWithLoopsAndPaths() {
        distances = mutableMapOf<Coord, Int>().withDefault { Int.MAX_VALUE }
        val priorityQueue = PriorityQueue<Pair<Coord, Int>>(compareBy { it.second })

        // need to know if we've visited this node before to prevent infinite looping (and help with multiple pathing)
        val visited = mutableSetOf<Pair<Coord, Int>>()

        // track the parents, so we can identify actual path points, not just the total distance on the path
        paths = mutableMapOf()

        // we start searching from the 'S' point
        priorityQueue.add(start to 0)
        distances[start] = 0

        while (priorityQueue.isNotEmpty()) {
            val (node, currentDist) = priorityQueue.poll()
            if (visited.add(node to currentDist)) {
                graph[node]?.forEach { (adjacent, weight) ->
                    var adjustedWeight = weight
                    if (graph[node]?.size == 4) {
                        // funky handling of edge weight required when traversing a crossroad
                        // because I couldn't figure out how to include it in the actual graph :P
                        graph[node]?.forEach { (previous, _) ->
                            // find the adjacent node of the crossroad that we entered from
                            if (visited.find { it.first == previous } != null) {
                                if (previous.x != adjacent.x && previous.y != adjacent.y) {
                                    // if both x AND y co-ords of previous node and curr adjacent node are different,
                                    // we have "turned a corner", so adjust the weight of the edge accordingly
                                    adjustedWeight += 1000
                                }
                            }
                        }
                    }
                    val totalDist = currentDist + adjustedWeight
                    // if new dist <= existing distance to this adjacent node, we've found a (new) shortest path
                    if (totalDist <= distances.getValue(adjacent)) {
                        distances[adjacent] = totalDist
                        priorityQueue.add(adjacent to totalDist)

                        // Add node as a parent of adjacent node, so we can retrace our short paths
                        if (paths[adjacent] == null) {
                            paths[adjacent] = mutableListOf()
                        }
                        paths[adjacent]!!.add(node)
                    }
                }
            }
        }
    }

    // are we at a T-junction?
    private fun isTJunction(coord: Coord, spaces: List<Coord>): Boolean {
        var count = 0
        cardinals.forEach { dir ->
            val neighbour = spaces.find { it == coord + dir.coord }
            if (neighbour != null) {
                count++
            }
        }
        // if we have 3 adjacent nodes, we're in a T Junction
        return count == 3
    }

    // could we continue in current direction or would we hit a wall?
    private fun oneMoreStep(coord: Coord, dir: Direction, spaces: List<Coord>): Boolean {
        val nextStep = spaces.find { it == coord + dir.coord }
        return nextStep != null
    }

    // check to see if there was a wall behind us
    // Used when in a T Junction to figure out if we're crossing the T, or have turned
    private fun onePreviousStep(coord: Coord, dir: Direction, spaces: List<Coord>): Boolean {
        val previousDir = dir.turn180()
        val previousStep = spaces.find { it == coord + previousDir.coord }
        return previousStep != null
    }

    // Builds a directed, weighted graph of all the points in the maze
    //
    // Note: I couldn't work out how to handle crossroads during graph creation, so added a hack in the
    // Dijkstra's Algorithm code (see dijkstraWithLoopsAndPaths() function) to adjust the edge weight on the fly
    // if "turning" at a crossroad
    private fun buildGraph() {

        graph = mutableMapOf()

        // only interested in non-wall points
        val spaces = maze.filter { it.value == '.' || it.value == 'S' || it.value == 'E' }.keys.toList()

        // for each point in the maze, get the neighbours, calculate the movement cost to that neighbour
        spaces.forEach { coord ->
            val neighbours = mutableListOf<Pair<Coord, Int>>()
            val isTee = isTJunction(coord, spaces)
            cardinals.forEach { dir ->
                val neighbour = spaces.find { it == coord + dir.coord }
                if (neighbour != null) {
                    if (coord == start && dir == Direction.UP) {
                        // assuming we always start lower left, if we're going UP, it involves the 90degree turn,
                        // so add the turn penalty of 1000 to the move
                        neighbours.add(neighbour to 1001)
                    } else if (neighbour != end && !oneMoreStep(neighbour, dir, spaces)) {
                        // if we haven't found the End point, and we won't be able to move in the same direction
                        // next turn, we will need to turn,
                        // so add the turn penalty of 1000 to the move
                        neighbours.add(neighbour to 1001)
                    } else if (isTee && !onePreviousStep(coord, dir, spaces)) {
                        // if we're at a T Junction, and we've not travelling across the top of the T, we've turned
                        // so add the turn penalty of 1000 to the move
                        neighbours.add(neighbour to 1001)
                    } else {
                        // otherwise, it's just a simple step (or crossroad which we'll "hack" later),
                        // add move weight of 1
                        neighbours.add(neighbour to 1)
                    }
                }
            }
            graph[coord] = neighbours
        }
    }

    // Gets a Map containing all the points and their parent nodes on shortest paths
    // Returns a Set of the Points on any of the shortest paths from start to end Point
    private fun getParents(allPoints: Map<Coord, MutableList<Coord>>, start: Coord, end: Coord): MutableSet<Coord> {
        val uniquePoints = mutableSetOf<Coord>()
        uniquePoints.add(end)

        // if we are back at the start, we're done tracing all the paths
        if (end == start) {
            uniquePoints.add(start)
            return uniquePoints
        }

        // beginning at the end point, recursively find all the parent nodes that form part of any shortest path
        allPoints[end]!!.forEach { parent ->
            uniquePoints.add(parent)
            uniquePoints.addAll(getParents(allPoints, start, parent))
        }
        return uniquePoints
    }

    override fun commonCode() {
        maze = char2DArrayToMap(parseCharArray(getInputText()))
        start = maze.keys.find { maze[it] == 'S' }!!
        end = maze.keys.find { maze[it] == 'E' }!!

        // find all the shortest paths from the Start Point using Dijkstra's Algorithm
        buildGraph()
        dijkstraWithLoopsAndPaths()
    }

    override fun part1(): Int {
        // we want the lowest score from 'S' point to 'E' point
        return distances[end]!!
    }

    override fun part2(): Int {
        // retrieve all the unique points that are on the shortest path(s) from 'S' to 'E'
        return getParents(paths, start, end).size
    }
}

val day16Problem = Day16Problem()

fun main() {
    //day16Problem.testData = true
    day16Problem.runBoth(1)
}