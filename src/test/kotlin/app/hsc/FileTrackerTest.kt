package app.hsc

import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.*
import org.awaitility.Awaitility
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.time.Duration
import kotlin.io.path.writeText


@OptIn(DelicateCoroutinesApi::class)
class FileTrackerTest : FunSpec({

    //disabled since this tests only works with external changes to file during test
    test("file tracker detects change").config(enabled = false) {

        //given
        val fileName = "data.txt"
        val file = File(fileName).toPath().toAbsolutePath()
        file.writeText("abc")
        val fileTracker = FileTracker()
        val events = mutableListOf<String>()
        withContext(Dispatchers.IO) {
            fileTracker.track(file) { event ->
                println(event.kind.kind)
                events.add(event.kind.kind)
            }
        }


        //when this is not working t
        GlobalScope.launch {
            Files.writeString(file, "adfggg")
        }


        //then
        Awaitility.await().atMost(Duration.ofSeconds(30)).pollInterval(Duration.ofSeconds(5))
            .until {
                println("File Content: ${Files.readString(file)}")
                println("checking if contains modified $events")
                events.contains("modified")
            }

        //cleanup
        withContext(Dispatchers.IO) {
            Files.delete(file)
        }


    }


})