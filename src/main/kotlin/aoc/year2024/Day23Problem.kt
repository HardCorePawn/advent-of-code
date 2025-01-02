package aoc.year2024

import DailyProblem

class Day23Problem : DailyProblem<String>() {

    override val number = 23
    override val year = 2024
    override val name = "LAN Party"

    private lateinit var graph: MutableMap<String, MutableList<String>>

    private fun buildGraph(input: List<String>) {
        input.forEach { line ->
            val (comp1, comp2) = line.split("-")

            if (!graph.containsKey(comp1)) graph[comp1] = mutableListOf()
            graph[comp1]!!.add(comp2)
            if (!graph.containsKey(comp2)) graph[comp2] = mutableListOf()
            graph[comp2]!!.add(comp1)
        }
    }

    override fun commonCode() {
        graph = mutableMapOf()
        buildGraph(getInputText().lines().filter { it.isNotEmpty() })
    }

    override fun part1(): String {
        val sets = mutableSetOf<List<String>>()

        // Iterate through all the computers and their neighbours looking for "Sets of 3"
        graph.forEach { (first, neighbours) ->
            neighbours.forEach { second ->
                // exclude our original computer, when looking at the 2nd's neighbours
                graph[second]!!.filter { it != first }.forEach { third ->
                    // if the 3rd comp is a neighbour of the 1st computer, we have a "Set of 3"
                    if (graph[third]!!.contains(first)) {
                        // sort the list before adding, so we automagically avoid duplicates
                        sets.add(listOf(first, second, third).sorted())
                    }
                }
            }
        }
        return sets.count { it.any { it.startsWith("t") } }.toString() //only sets, where a computer starts with "t"
    }

    override fun part2(): String {
        // Function to recursively look along the chain of neighbours, finding sets of computers
        // that all have each other as neighbours
        fun findAllSets(neighbour: String, prevNeighbours: MutableList<String>): List<List<String>> {

            val foundSets = mutableSetOf<List<String>>()

            if (graph[neighbour]!!.containsAll(prevNeighbours)) {
                // this computer has all previous computers in the chain as neighbours, so add it to the set
                // and continue the search on unvisited neighbours
                prevNeighbours.add(neighbour)
                graph[neighbour]!!.forEach { nextNeighbour ->
                    if (!prevNeighbours.contains(nextNeighbour)) {
                        foundSets += findAllSets(nextNeighbour, prevNeighbours)
                    }
                }
                return foundSets.toList()
            } else {
                // This computer doesn't have all previous computers in the set as neighbours, so the set is complete.
                foundSets += prevNeighbours
                return foundSets.toList()
            }
        }

        val allSets = mutableListOf<List<String>>()

        // Iterate through all the computers and their neighbours looking for "Sets of connected computers"
        // Have since learned, these sets are called "cliques" in Graph theory.
        graph.forEach { (comp, neighbours) ->
            val prevNeighbours = mutableListOf(comp)
            neighbours.forEach { neighbour ->
                allSets += findAllSets(neighbour, prevNeighbours)
            }
        }
        // return the largest set as a comma separated list i.e. the LAN Party password
        return allSets.maxBy { it.size }.sortedBy { it }.joinToString(",")
    }
}

val day23Problem = Day23Problem()

fun main() {
    //day23Problem.testData = true
    day23Problem.runBoth(100)
}