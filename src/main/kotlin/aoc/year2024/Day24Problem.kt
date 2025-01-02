package aoc.year2024

import DailyProblem

class Day24Problem : DailyProblem<String>() {

    enum class GateType {
        AND,
        OR,
        XOR
    }

    data class Wire(val name: String, var value: Int)
    data class Gate(val input1: Wire, val input2: Wire, var output: Wire, val type: GateType)

    override val number = 24
    override val year = 2024
    override val name = "Crossed Wires"

    private lateinit var gates: MutableSet<Gate>
    private lateinit var wires: MutableSet<Wire>

    private val debug = false

    private fun parseInput(input: List<String>) {
        input.forEach { line ->
            if (line.contains(":")) {
                // wires
                wires.add(Wire(line.substringBefore(":"), line.substringAfter(": ").toInt()))
            } else if (line.contains("->")) {
                // gates
                val gateStrings = line.split(" ")

                // if the inputs or output are known wires (i.e. X's or Y's or previously seen), get them from
                // the wire list, otherwise create a new wire and initialise it to -1
                val inWire1 = if (wires.any { it.name == gateStrings[0] }) wires.find { it.name == gateStrings[0] }
                else Wire(gateStrings[0], -1)
                val inWire2 = if (wires.any { it.name == gateStrings[2] }) wires.find { it.name == gateStrings[2] }
                else Wire(gateStrings[2], -1)
                val outWire = if (wires.any { it.name == gateStrings[4] }) wires.find { it.name == gateStrings[4] }
                else Wire(gateStrings[4], -1)

                // ordering the inWires to make sure all xWires input1, and all yWires input2
                val ordered = listOf(inWire1, inWire2).sortedBy { it!!.name }
                wires.add(ordered[0]!!)
                wires.add(ordered[1]!!)
                wires.add(outWire!!)

                // create a gate with the parsed input wires, output wire and type and add it to the list
                gates.add(Gate(ordered[0]!!, ordered[1]!!, outWire, GateType.valueOf(gateStrings[1])))
            }
        }
    }

    private fun processGates(toProcess: List<Gate>) {
        toProcess.forEach { gate ->
            when (gate.type) {
                GateType.AND -> gate.output.value = gate.input1.value and gate.input2.value
                GateType.OR -> gate.output.value = gate.input1.value or gate.input2.value
                GateType.XOR -> gate.output.value = gate.input1.value xor gate.input2.value
            }
        }
    }

    override fun commonCode() {
        gates = mutableSetOf()
        wires = mutableSetOf()
        parseInput(getInputText().lines())
    }

    override fun part1(): String {
        // if there are gates that still haven't got a valid output value (0 or 1),
        // process ones with valid inputs
        while (gates.count { it.output.value == -1 } > 0) {
            processGates(gates.filter { it.input1.value != -1 && it.input2.value != -1 && it.output.value == -1 })
        }

        // all gates should have been processed, so get all the zWires which represent the bits of the answer
        val zWires = wires.filter { it.name.startsWith("z") }.sortedBy { it.name }

        // construct the answer from the output bits
        var num = 0L
        zWires.forEachIndexed { i, wire ->
            if (wire.value == 1) num += 1L shl i
        }
        return num.toString()
    }

    override fun part2(): String {
        if (!testData) {
            val badGates = mutableListOf<Gate>()

            // sort by x value and gate type reversed, so we get the XOR gate, then the AND gate for each X,Y pair
            val sortedX = gates.filter { it.input1.name.startsWith("x") }.sortedBy { it.type }.asReversed()
                .sortedBy { it.input1.name }
            var i = 0
            var carryBit = ""
            while (i in 0..<sortedX.size / 2) {
                if (i == 0) {
                    // First bit is a Half Adder, just check we have XOR with output of z00
                    if (sortedX[0].type == GateType.XOR && sortedX[0].output.name != "z00") {
                        if (debug) println("Bad wires at 00")
                        badGates.add(sortedX[0])
                        if (debug) println(sortedX[0])
                        val swap = gates.find { it.output.name == "z00" }!!
                        badGates.add(swap)
                        if (debug) print("Swap")
                        if (debug) println(swap)
                        val tempOut = swap.output
                        swap.output = sortedX[0].output
                        sortedX[0].output = tempOut
                    }
                    carryBit = sortedX[1].output.name
                } else {
                    val bitPair = i.toString().padStart(2, '0')
                    val nameX = "x$bitPair"
                    val nameY = "y$bitPair"
                    val nameZ = "z$bitPair"
                    var firstResult =
                        sortedX.find { it.input1.name == nameX && it.input2.name == nameY && it.type == GateType.XOR }!!.output.name
                    var firstCarry =
                        sortedX.find { it.input1.name == nameX && it.input2.name == nameY && it.type == GateType.AND }!!.output.name

                    val xorCheck = gates.find { it.output.name == nameZ }!!
                    if (xorCheck.type != GateType.XOR) {
                        // Not an XOR gate, so the sum is not coming from the correct gate
                        if (debug) println("Bad wires at $bitPair - $nameZ is not on XOR gate")
                        badGates.add(xorCheck)
                        if (debug) println(xorCheck)
                        // find the gate where firstResult is being XOR'd... that's the one generating the value for the zWire
                        val swap =
                            gates.find { it.type == GateType.XOR && (it.input1.name == firstResult || it.input2.name == firstResult) }
                        badGates.add(swap!!)
                        if (debug) print("Swap")
                        if (debug) println(swap)
                        // swap the output wires of the 2 incorrect gates
                        val tempOut = swap.output
                        swap.output = xorCheck.output
                        xorCheck.output = tempOut
                        // We swapped some things around, so we recalculate the gate outputs
                        firstResult =
                            sortedX.find { it.input1.name == nameX && it.input2.name == nameY && it.type == GateType.XOR }!!.output.name
                        firstCarry =
                            sortedX.find { it.input1.name == nameX && it.input2.name == nameY && it.type == GateType.AND }!!.output.name
                    } else if (xorCheck.input1.name != firstResult && xorCheck.input2.name != firstResult) {
                        // firstResult is wrong, so x0 XOR y0 gate output is wrong
                        if (debug) println("Bad wires at $bitPair - x XOR y gate output is wrong")
                        val badGate =
                            sortedX.find { it.input1.name == nameX && it.input2.name == nameY && it.type == GateType.XOR }!!
                        badGates.add(badGate)
                        if (debug) println(badGate)
                        // the input that isn't the previous carryBit value, is what the x XOR y gate should be connected to
                        val realFirstResult =
                            if (xorCheck.input1.name == carryBit) xorCheck.input2.name else xorCheck.input1.name
                        val swap = gates.find { it.output.name == realFirstResult }
                        badGates.add(swap!!)
                        if (debug) print("Swap")
                        if (debug) println(swap)
                        // swap the output wires of the 2 incorrect gates
                        val tempOut = swap.output
                        swap.output = badGate.output
                        badGate.output = tempOut
                        // We swapped some things around, so we recalculate the gate outputs
                        firstResult =
                            sortedX.find { it.input1.name == nameX && it.input2.name == nameY && it.type == GateType.XOR }!!.output.name
                        firstCarry =
                            sortedX.find { it.input1.name == nameX && it.input2.name == nameY && it.type == GateType.AND }!!.output.name
                    }

                    val secondCarry = gates.find {
                        (it.input1.name == firstResult || it.input2.name == firstResult) &&
                                (it.input1.name == carryBit || it.input2.name == carryBit) && it.type == GateType.AND
                    }!!.output.name

                    // update the carry value for the next iteration
                    carryBit = gates.find {
                        (it.input1.name == firstCarry || it.input2.name == firstCarry) &&
                                (it.input1.name == secondCarry || it.input2.name == secondCarry) && it.type == GateType.OR
                    }!!.output.name
                }
                i++
            }

            // return the bad output wires, sorted and comma separated
            return badGates.sortedBy { it.output.name }.joinToString(",", transform = { it.output.name })
        }
        else {
            return "No TestData for Part 2"
        }
    }
}

val day24Problem = Day24Problem()

fun main() {
    //day24Problem.testData = true
    day24Problem.runBoth(100)
}