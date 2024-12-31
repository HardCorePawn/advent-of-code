package aoc.year2024

import DailyProblem
import aoc.utils.Coord
import aoc.utils.Direction
import aoc.utils.cardinals

class Day12Problem : DailyProblem<Int>() {

    // data class for each square on the board
    // Notes:
    // fences was originally an Int but converted to a List to make identifying "sides" easier
    data class Plot(var loc: Coord, var type: String, var fences: MutableList<Boolean?>, var region: Int)

    override val number = 12
    override val year = 2024
    override val name = "Garden Groups"

    private lateinit var plots: MutableList<Plot>
    private lateinit var regions: Map<Int, List<Plot>>

    private fun parseGrid(input: String) {
        // set fences to null initially, and region to -1 to indicate it hasn't been calculated yet
        input.lines().filter { it.isNotEmpty() }.forEachIndexed { row, line ->
            line.forEachIndexed { column, plot ->
                plots.add(Plot(Coord(column,row), plot.toString(), mutableListOf(null, null, null, null), -1))
            }
        }
    }

    // create regions, starting at (0,0)
    private fun processPlots() {

        var currRegion = 0

        plots.forEach { plot ->
            val neighbours = getNeighbours(plot.loc)

            if (plot.region == -1) plot.region = currRegion
            processNeighbours(plot, neighbours)
            currRegion++
        }
    }

    // recursively finds and sets neighbouring plots of same type to same region as current plot
    // and adds fences when different region found
    private fun processNeighbours(plot: Plot, neighbours: List<Plot?>) {

        neighbours.forEachIndexed { index, neighbour ->
            if (neighbour != null) {
                if (plot.type == neighbour.type) {
                    if (neighbour.region == -1) {
                        neighbour.region = plot.region
                        processNeighbours(neighbour, getNeighbours(neighbour.loc))
                    }
                } else {
                    // different plot type, need a fence
                    plot.fences[index] = true
                }
            } else {
                // no neighbour, need a fence
                plot.fences[index] = true
            }
        }
    }

    // helper function to return all the neighbouring plots of a given plot
    private fun getNeighbours(loc: Coord): List<Plot?> {
        val neighbours = mutableListOf<Plot?>(null, null, null, null)

        neighbours[0] = plots.find { it.loc == loc + Direction.UP.coord }       // Top
        neighbours[1] = plots.find { it.loc == loc + Direction.RIGHT.coord }    // Right
        neighbours[2] = plots.find { it.loc == loc + Direction.DOWN.coord }     // Bottom
        neighbours[3] = plots.find { it.loc == loc + Direction.LEFT.coord }     // Left

        return neighbours
    }

    // returns all plots in a given region that have a fence on the specified side
    private fun getFences(region: List<Plot>, side: Int): List<Plot> {
        return region.filter { it.fences[side] == true }
    }

    // Given a region, calculate the total number of sides it has
    // Start by grouping all plots in the region into "fence side" groups
    // for each side group, group by specific row (top/bottom) or specific column (left/right)
    // for each plot in a row/column group, check if the current plot is adjacent to previous plot, if not, new side
    // finally, return sum of all the sides
    private fun calcRegionSides(region: List<Plot>): Int {

        var totalSides = 0

        // iterate through each side
        cardinals.forEachIndexed { i, _ ->
            val fences = getFences(region, i)

            val rowOrColumn: Map<Int, List<Plot>> = if (i % 2 == 0) {
                // if top or bottom fences, group by row
                fences.groupBy { it.loc.y }
            } else {
                // if left or right fences, group by column
                fences.groupBy { it.loc.x }
            }

            rowOrColumn.forEach { (key, _) ->
                // if this group only has 1 member == 1 side
                if (rowOrColumn[key]!!.size == 1) {
                    totalSides++
                } else {
                    rowOrColumn[key]!!.forEachIndexed { index, plot ->
                        if (index == 0) {
                            // first member of the group, create a side
                            totalSides++
                        } else {
                            if (i % 2 == 0) {
                                // if the plot is not adjacent to the previous plot, we have a new side
                                if (plot.loc.x - 1 != rowOrColumn[key]!![index - 1].loc.x) {
                                    totalSides++
                                }
                            } else {
                                // if the plot is not adjacent to the previous plot, we have a new side
                                if (plot.loc.y - 1 != rowOrColumn[key]!![index - 1].loc.y) {
                                    totalSides++
                                }
                            }

                        }
                    }
                }
            }
        }

        return totalSides
    }

    override fun commonCode() {
        plots = mutableListOf()
        parseGrid(getInputText())
        processPlots()

        // group plots into their regions
        regions = plots.groupBy { it.region }
    }

    override fun part1(): Int {
        return regions.values.sumOf { regionPlots ->
            // for part 1, Region price = area of region * # of fences in region
            regionPlots.sumOf { plot -> plot.fences.count { it != null } } * regionPlots.size
        }
    }

    override fun part2(): Int {
        return regions.values.sumOf { regionPlots ->
            // for part 2, region price = area of region * # of sides of region
            calcRegionSides(regionPlots) * regionPlots.size
        }
    }

}

val day12Problem = Day12Problem()

fun main() {
    //day12Problem.testData = true
    day12Problem.runBoth(1)
}