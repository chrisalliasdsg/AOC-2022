import java.io.File

fun readFile(filename: String) =
    File("src/main/kotlin", filename).readLines()