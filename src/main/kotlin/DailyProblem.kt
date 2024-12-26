import java.io.File
import kotlin.time.Duration
import kotlin.time.measureTime

abstract class DailyProblem<Res> {
    abstract val number: Int
    abstract val year: Int
    abstract val name: String

    // run with testData?
    var testData = false

    open fun commonCode() {}

    abstract fun part1(): Res
    abstract fun part2(): Res

    private fun getInputFile(): File {
        if (testData) return File("input/aoc$year/testinput/day$number.txt")
        return File("input/aoc$year/day$number.txt")
    }

    fun getInputText(): String {
        return getInputFile().readText()
    }

    fun runBoth(timesToRun: Int = 1): Duration {
        println("=== Day $number : $name ===")
        println("https://adventofcode.com/$year/day/$number")
        var result1: Res? = null
        var result2: Res? = null
        val runDuration = measureTime {
            repeat(timesToRun) {
                this.commonCode()
                result1 = this.part1()
                result2 = this.part2()
            }
        }
        val averageDuration = runDuration / timesToRun
        println("part 1: ${result1.toString()}")
        println("part 2: ${result2.toString()}")
        println("Average runtime for year $year day $number: $averageDuration")
        println("===========")
        println()

        return averageDuration
    }
}