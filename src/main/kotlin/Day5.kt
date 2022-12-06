fun main() {
    val x = mutableListOf<String>("A", "B", "C", "D", "E", "F")

    val stacks: MutableMap<Int, List<String>> = mutableMapOf()

    val diagram = """
            [V] [G]             [H]        
        [Z] [H] [Z]         [T] [S]        
        [P] [D] [F]         [B] [V] [Q]    
        [B] [M] [V] [N]     [F] [D] [N]    
        [Q] [Q] [D] [F]     [Z] [Z] [P] [M]
        [M] [Z] [R] [D] [Q] [V] [T] [F] [R]
        [D] [L] [H] [G] [F] [Q] [M] [G] [W]
        [N] [C] [Q] [H] [N] [D] [Q] [M] [B]
         1   2   3   4   5   6   7   8   9 
    """.trimIndent()

    if (DEBUG) {
        println(diagram)
        println()
    }

    var cargoMap = loadCargoMap(diagram)
    printCargoLayout(cargoMap)
    println()

    val instructions = readFile("Day5_crane_instructions.txt")
    instructions.forEach {
        val task = it.split(" ")
        val count = task[1].toInt()
        val source = task[3].toInt()
        val dest = task[5].toInt()
        crateMover9001(count, source, dest, cargoMap)
    }

    printCargoLayout(cargoMap)
}


fun crateMover9001(count:Int, source: Int, dest: Int, cargoMap: MutableMap<Int, MutableList<Char>>) {
    cargoMap[source]?.takeLast(count)?.let { cargoMap[dest]?.addAll(it) }
    IntRange(1,count).forEach {
        cargoMap[source]?.removeLast()
    }
}


fun crateMover9000(count:Int, source: Int, dest: Int, cargoMap: MutableMap<Int, MutableList<Char>>) {
    IntRange(1, count).forEach {
        cargoMap[source]?.takeLast(1)?.let { cargoMap[dest]?.addAll(it) }
        cargoMap[source]?.removeLast()
    }
}


fun loadCargoMap(cargoDiagram: String): MutableMap<Int, MutableList<Char>> {
    // Build out cargo map container
    val cargoMap = mutableMapOf<Int, MutableList<Char>>()
    IntRange(1, 9).forEach { cargoMap.put(it, mutableListOf()) }

    // populate cargoMap
    val cargoDiagramList = cargoDiagram
        .replace("    ", "[_]")
        .replace(" [", "")
        .replace("] ", "")
        .replace("[", "")
        .replace("]", "")
        .replace("   ", "")
        .replace(" [0-9]+".toRegex(), "")
        .replace("_+\n".toRegex(), "\n")
        .replace("_", " ")
        .split("\n")
        .reversed()

    cargoDiagramList.forEach {
        it.toCharArray()
            .mapIndexed { index, crate ->
                if (crate != ' ') cargoMap.get(index + 1)?.add(crate)
            }
    }

    if (DEBUG) {
        println("cargoMap:")
        cargoMap.forEach {
            println(it)
        }
    }

    return cargoMap
}


fun printCargoLayout(cargoMap: MutableMap<Int, MutableList<Char>>) {
    val maxElement: Int = cargoMap.values.map{it.size}.max()
    if (DEBUG) println("maxElement: $maxElement")
    var grid = Array(maxElement) { Array<Char>(9) {' '} }

    cargoMap.forEach {column, stack ->
        if (DEBUG) println("column: $column; stack: $stack")
        stack.forEachIndexed{ i, crate ->
            if (DEBUG) println("index: $i; crate: $crate")
            grid[i][column-1] = crate
        }
    }

    grid.map { row -> row.asList().joinToString("  ").replace(",","") }.reversed().forEach { println(it) }
    println("-------------------------")
    println("1  2  3  4  5  6  7  8  9")
    println()
    println("Top Crates: ${getTopCrates(cargoMap)}")
}


fun getTopCrates(cargoMap: MutableMap<Int, MutableList<Char>>) =
    IntRange(1,9).map { cargoMap[it]?.last() }.joinToString().replace(", ","")



/*
--- Day 5: Supply Stacks ---
The expedition can depart as soon as the final supplies have been unloaded from the ships. Supplies are stored in stacks
of marked crates, but because the needed supplies are buried under many other crates, the crates need to be rearranged.

The ship has a giant cargo crane capable of moving crates between stacks. To ensure none of the crates get crushed or
fall over, the crane operator will rearrange them in a series of carefully-planned steps. After the crates are
rearranged, the desired crates will be at the top of each stack.

The Elves don't want to interrupt the crane operator during this delicate procedure, but they forgot to ask her which
crate will end up where, and they want to be ready to unload them as soon as possible so they can embark.

They do, however, have a drawing of the starting stacks of crates and the rearrangement procedure (your puzzle input).
For example:

    [D]
[N] [C]
[Z] [M] [P]
 1   2   3

move 1 from 2 to 1
move 3 from 1 to 3
move 2 from 2 to 1
move 1 from 1 to 2
In this example, there are three stacks of crates. Stack 1 contains two crates: crate Z is on the bottom, and crate N is
on top. Stack 2 contains three crates; from bottom to top, they are crates M, C, and D. Finally, stack 3 contains a
single crate, P.

Then, the rearrangement procedure is given. In each step of the procedure, a quantity of crates is moved from one stack
to a different stack. In the first step of the above rearrangement procedure, one crate is moved from stack 2 to stack
1, resulting in this configuration:

[D]
[N] [C]
[Z] [M] [P]
 1   2   3
In the second step, three crates are moved from stack 1 to stack 3. Crates are moved one at a time, so the first crate
to be moved (D) ends up below the second and third crates:

        [Z]
        [N]
    [C] [D]
    [M] [P]
 1   2   3
Then, both crates are moved from stack 2 to stack 1. Again, because crates are moved one at a time, crate C ends up
below crate M:

        [Z]
        [N]
[M]     [D]
[C]     [P]
 1   2   3
Finally, one crate is moved from stack 1 to stack 2:

        [Z]
        [N]
        [D]
[C] [M] [P]
 1   2   3
The Elves just need to know which crate will end up on top of each stack; in this example, the top crates are C in stack
1, M in stack 2, and Z in stack 3, so you should combine these together and give the Elves the message CMZ.

After the rearrangement procedure completes, what crate ends up on top of each stack?

Your puzzle answer was QGTHFZBHV.

--- Part Two ---
As you watch the crane operator expertly rearrange the crates, you notice the process isn't following your prediction.

Some mud was covering the writing on the side of the crane, and you quickly wipe it away. The crane isn't a CrateMover
9000 - it's a CrateMover 9001.

The CrateMover 9001 is notable for many new and exciting features: air conditioning, leather seats, an extra cup holder,
and the ability to pick up and move multiple crates at once.

Again considering the example above, the crates begin in the same configuration:

    [D]
[N] [C]
[Z] [M] [P]
 1   2   3
Moving a single crate from stack 2 to stack 1 behaves the same as before:

[D]
[N] [C]
[Z] [M] [P]
 1   2   3
However, the action of moving three crates from stack 1 to stack 3 means that those three moved crates stay in the same
order, resulting in this new configuration:

        [D]
        [N]
    [C] [Z]
    [M] [P]
 1   2   3
Next, as both crates are moved from stack 2 to stack 1, they retain their order as well:

        [D]
        [N]
[C]     [Z]
[M]     [P]
 1   2   3
Finally, a single crate is still moved from stack 1 to stack 2, but now it's crate C that gets moved:

        [D]
        [N]
        [Z]
[M] [C] [P]
 1   2   3
In this example, the CrateMover 9001 has put the crates in a totally different order: MCD.

Before the rearrangement process finishes, update your simulation so that the Elves know where they should stand to be
ready to unload the final supplies. After the rearrangement procedure completes, what crate ends up on top of each
stack?

Your puzzle answer was MGDMPSZTM.

Both parts of this puzzle are complete! They provide two gold stars: **

At this point, you should return to your Advent calendar and try another puzzle.

If you still want to see it, you can get your puzzle input.

You can also [Share] this puzzle.
 */