import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.io.File

suspend fun main() {

    val lines = File("input/downloadinfo.txt").readLines()

    val sessionId = lines.first()
    val dls = lines.drop(1).map { line ->
        val (year, date) = line.split(" ").map { it.toInt() }
        Pair(year, date)
    }

    HttpClient {
        defaultRequest {
            header(HttpHeaders.Cookie, "session=$sessionId")
            header(HttpHeaders.UserAgent, "Personal downloader script, HardCorePawn")
        }
    }.use { client ->
        dls.forEach {(year, date) ->
            val dir = File("input/aoc$year")
            if (!dir.exists()) {
                dir.mkdir()
            }

            for (day in (1..date).reversed()) {
                val fileName = "input/aoc$year/day$day.txt"
                val file = File(fileName)
                if (file.exists()) {
                    println("$fileName already exists, skipping.")
                    continue
                }
                val response: HttpResponse = client.get("https://adventofcode.com/$year/day/$day/input")
                val body = response.readRawBytes()
                file.writeBytes(body)
                println("$fileName downloaded.")
                Thread.sleep(1000)
            }
        }
    }
}