package aoc.year2024

import DailyProblem

class Day9Problem : DailyProblem<Long>() {

    data class Block(var empty: Boolean, var fileIndex: Int?, var length: Int)

    override val number = 9
    override val year = 2024
    override val name = "Disk Fragmenter"

    private lateinit var diskmap: String
    private lateinit var expandedDisk: Array<Int?>
    private lateinit var blocks: MutableList<Block>

    override fun commonCode() {
        diskmap = getInputText().lines().filter { it.isNotEmpty() }[0]
        expandedDisk = Array(diskmap.sumOf { it.digitToInt() }) { null } // initialise the disk array to null

        blocks = mutableListOf()

        var diskIdx = 0
        var fileId = 0

        diskmap.forEachIndexed { i, c ->
            when (i % 2) {
                0 -> {
                    // file length
                    blocks.add(Block(empty = false, fileIndex = fileId, length = c.digitToInt()))
                    repeat(c.digitToInt()) {
                        expandedDisk[diskIdx++] = fileId
                    }
                    fileId++
                }
                1 -> {
                    // free space
                    diskIdx += c.digitToInt()
                    blocks.add(Block(empty = true, fileIndex = null, length = c.digitToInt()))
                }
            }
        }
    }

    // moves blocks, one at a time, from right most nonEmpty to left most empty space
    private fun compactFileSystem() {

        // first empty block is the first null element in disk
        var leftMostEmptyBlockIndex = expandedDisk.indexOfFirst { it == null }

        // last nonEmpty block is the last not null element in disk
        var rightMostNonEmptyBlockIndex = expandedDisk.indexOfLast { it != null }

        while (leftMostEmptyBlockIndex < rightMostNonEmptyBlockIndex) {
            // swap left most empty block with right most nonEmpty block
            expandedDisk[leftMostEmptyBlockIndex] = expandedDisk[rightMostNonEmptyBlockIndex]
            expandedDisk[rightMostNonEmptyBlockIndex] = null

            // recalculate left most empty and right most nonEmpty
            leftMostEmptyBlockIndex = expandedDisk.indexOfFirst { it == null }
            rightMostNonEmptyBlockIndex = expandedDisk.indexOfLast { it != null }
        }
    }

    // Checksum = sum of (block index * fileId) for nonEmpty blocks
    private fun calcCheckSum(): Long {
        var checkSum: Long = 0

        expandedDisk.forEachIndexed { index, l ->
            if (l != null) {
                checkSum += index * l
            }
        }

        return checkSum
    }

    // build disk array from blocks
    private fun createDiskFromBlocks(): Array<Int?> {
        //init disk array to null values
        val diskArray = Array<Int?>(diskmap.sumOf { it.digitToInt() }) { null }
        var i = 0

        //loop through blocks writing file index into disk array
        blocks.forEach { block ->
            repeat(block.length) {
                if (!block.empty) {
                    diskArray[i] = block.fileIndex
                }
                i++
            }
        }
        return diskArray
    }

    override fun part1(): Long {
        compactFileSystem()
        return calcCheckSum()
    }

    override fun part2(): Long {
        // get all the nonEmpty blocks
        val files = blocks.filter { !it.empty }.toMutableList()

        repeat(files.size) {
            // see if we can move the last file in the list
            val fileToMove = files.removeLast()

            val blockIndex = blocks.indexOf(fileToMove)
            val emptyBlockIndex = blocks.indexOfFirst { it.empty && it.length >= fileToMove.length }

            if (emptyBlockIndex != -1 && emptyBlockIndex < blockIndex) {
                //found an empty block to the left that is big enough

                // make the original location of the block we're moving empty
                blocks[blockIndex] = fileToMove.copy(empty = true, fileIndex = null)

                val emptyBlock = blocks[emptyBlockIndex]
                if (emptyBlock.length == fileToMove.length) {
                    // nice and easy, dump block we're moving into same sized empty space
                    blocks[emptyBlockIndex] = fileToMove
                } else {
                    // put block at start of empty space, and then create a new empty block in the remaining space
                    val sizeDiff = emptyBlock.length - fileToMove.length
                    blocks[emptyBlockIndex] = fileToMove
                    blocks.add(emptyBlockIndex + 1, Block(empty = true, fileIndex = null, length = sizeDiff))
                }
            } // else, no empty block big enough, do nothing
        }

        expandedDisk = createDiskFromBlocks()
        return calcCheckSum()
    }
}

val day9Problem = Day9Problem()

fun main() {
    //day9Problem.testData = true
    day9Problem.runBoth(1)
}