package aoc.year2024

import DailyProblem

class Day17Problem : DailyProblem<String>() {

    class Computer(
        var a: Long = 0L, var b: Long = 0L, var c: Long = 0L,
        var iptr: Int = 0, var program: List<Int> = listOf(),
        var output: MutableList<Int> = mutableListOf() ) {

        fun output(): String {
            return (output.joinToString(",") + "\n")
        }

        // div by 2^x is same as bitwise right shift by x bits
        fun div(combo: Int): Long {
            val shift = when (combo) {
                0, 1, 2, 3 -> combo
                4 -> a
                5 -> b
                6 -> c
                else -> 0
            }
            return (a shr shift.toInt())
        }

        val opcodes =
            listOf(
                // 0 - adv
                fun(combo: Int) {
                    a = div(combo)
                    iptr += 2
                },
                // 1 - bxl
                fun(literal: Int) {
                    b = b.xor(literal.toLong())
                    iptr += 2
                },
                // 2 - bst
                fun(combo: Int) {
                    b = when (combo) {
                        0, 1, 2, 3 -> combo.toLong() % 8
                        4 -> a % 8
                        5 -> b % 8
                        6 -> c % 8
                        else -> -1
                    }
                    iptr += 2
                },
                // 3 - jnz
                fun(literal: Int) {
                    if (a != 0L) {
                        iptr = literal
                    } else {
                        iptr += 2
                    }
                },
                // 4 - bxc
                fun(_: Int) {
                    b = b.xor(c)
                    iptr += 2
                },
                // 5 - out
                fun(combo: Int) {
                    when (combo) {
                        0, 1, 2, 3 -> output.add(combo % 8)
                        4 -> output.add((a % 8).toInt())
                        5 -> output.add((b % 8).toInt())
                        6 -> output.add((c % 8).toInt())
                        else -> println("BAD OPERAND FOUND!!!")
                    }
                    iptr += 2
                },
                // 6 - bdv
                fun(combo: Int) {
                    b = div(combo)
                    iptr += 2
                },
                // 7 - cdv
                fun(combo: Int) {
                    c = div(combo)
                    iptr += 2
                }
            )
    }

    override val number = 17
    override val year = 2024
    override val name = "Chronospatial Computer"

    private lateinit var data: List<String>

    override fun commonCode() {
        data = getInputText().lines().filter { it.isNotEmpty() }
    }

    private fun initComputer(): Computer {

        val computer = Computer()

        // set registers
        computer.a = data[0].substringAfter("Register A: ").toLong()
        computer.b = data[1].substringAfter("Register B: ").toLong()
        computer.c = data[2].substringAfter("Register C: ").toLong()

        // extract program listing
        computer.program = data[3].substringAfter("Program: ").split(",").map { it.toInt() }

        return computer
    }

    // Basically, we plug in our guess for A, then iterate through the program to check that it is generating
    // output that correctly matches the slice of the program listing that we are checking for
    private fun check(regA: Long, program: List<Int>, outs: List<Int>, computer: Computer): Boolean {
        computer.a = regA
        var iptr = 0
        var outpos = 0
        while (iptr in 0..<program.lastIndex) {
            val opcode = computer.program[iptr++]
            val op = computer.opcodes[opcode]
            val operand = computer.program[iptr++]
            op(operand)
            if (opcode == 3 && computer.a != 0L) iptr = operand // we jumped, so set instruction pointer
            if (opcode == 5 && (outs[outpos++] != computer.output.last())) return false // output didn't match
        }
        return true // output from calculated Reg A matched partial program listing
    }

    // To reverseEngineer, we left shift Reg A by 3 bits to undo the (0,3) (i.e. a shr 3) program command
    // Then we increment through the "lower 3 bit" values (by adding values from 0 to 7) for this shifted value,
    // so we can check to see if the output from the calculated value of Reg A will match the partial program listing
    // If it does, and the "partial" output == the full program, we have successfully found our target Reg A value
    private fun reverseEngineer(
        a: Long,
        sliceSize: Int,
        program: List<Int>,
        targetOutput: List<Int>,
        computer: Computer
    ): Long {
        // take a slice from the end of the program list, we then "reverse the reverse", to put the partial listing
        // back into the same order as the original listing
        val partialOutput = targetOutput.take(sliceSize).reversed()

        // increment through "lower 3 bit" values
        for (i in 0L..7L) {
            if (check((a shl 3) + i, program, partialOutput, computer)) {
                // if this guess for A produces the correct output, and our "slice" is the full program output,
                // we have our number
                if (sliceSize == targetOutput.size) return (a shl 3) + i
                // otherwise, we need to find A for the next largest slice of the program list
                val result = reverseEngineer((a shl 3) + i, sliceSize + 1, program, targetOutput, computer)
                // if we have a non-zero result, we've found a good value of Reg A
                if (result != 0L) return result
            }
        }
        return 0 // didn't find a valid Reg A value
    }

    override fun part1(): String {
        val computer = initComputer()

        // while our instruction pointer is still within the bounds of the *opcodes* (allowing for last value as operand)
        while (computer.iptr in 0..<computer.program.lastIndex) {

            //get the opcode function from the program list
            val op = computer.opcodes[computer.program[computer.iptr]]

            //get the next value from the program list as the operand
            val operand = computer.program[computer.iptr + 1]

            // execute the operation - Note: instruction pointer is updated within op
            op(operand)
        }

        // all done, printout the stored output
        return computer.output()
    }

    override fun part2(): String {
        // init the computer
        val computer = initComputer()

        // get the program listing in reverse order (as we need to check partial listings starting from the end)
        val targetOutput = computer.program.reversed()

        // reverse engineer the program, starting with a Reg A value of 0 and trying to match just the very
        // last value of the program listing
        return reverseEngineer(0L, 1, computer.program, targetOutput, computer).toString()
    }
}

val day17Problem = Day17Problem()

fun main() {
    day17Problem.testData = true
    day17Problem.runBoth(100)
}