import java.io.File

const val DEBUG = false

fun readFile(filename: String) =
    File("src/main/kotlin", filename).readLines()