import java.io.File

fun main() {
    println("count: ${d4p1()}")
}

fun d4p1():Int {
    return File("src/main/kotlin", "day4_input.txt").readLines()
        .map {
            val a:List<Int> = it.split(",")[0].split("-").map{it.toInt()}
            val b:List<Int> = it.split(",")[1].split("-").map{it.toInt()}
            if (oneOverlapsTheOther(a[0], a[1], b[0], b[1])) {
                1
            } else {
                0
            }
        }.sum()
}

fun oneContainsTheOther(a1:Int, a2:Int, b1:Int, b2:Int) =
    (a1 <= b1 && a2 >= b2) || (a1 >= b1 && a2 <= b2)

fun oneOverlapsTheOther(a1:Int, a2:Int, b1:Int, b2:Int) =
    IntRange(a1,a2).toSet().intersect(IntRange(b1,b2).toSet()).size>0