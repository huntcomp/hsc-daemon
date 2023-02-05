package app.hsc

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.mpp.file
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.awaitility.Awaitility
import java.io.File
import java.nio.file.Files
import java.time.Duration
import kotlin.io.path.writeText


class FileTrackerTest : FunSpec({

//    test("Should detect change") {
//        //given
//        runBlocking {
//            println("given")
//            val fileName = "data.txt"
//            val file = File(fileName).toPath().toAbsolutePath()
//            file.writeText("abc")
//            val fileTracker = FileTracker()
//            val resultList = fileTracker.track(file)
//
//            //when
//            println("when")
//            file.writeText("adfggg")
//
//            //then
//            println("then")
//            Awaitility.await().atMost(Duration.ofMinutes(1)).pollInterval(Duration.ofSeconds(5))
//                .until {
//                    resultList.contains("modified")
//                }
//
//            //cleanup
//            withContext(Dispatchers.IO) {
//                Files.delete(file)
//            }
//        }

//    }


})