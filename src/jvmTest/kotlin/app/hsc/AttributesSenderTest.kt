package app.hsc

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.Path

class AttributesSenderTest : FunSpec({

    test("send attributes return 200") {
        //given
        val hscContext = Config().configureContext()
        val sender = hscContext.sender
        val body = withContext(Dispatchers.IO) {
            Files.readString(Path.of("/Users/tharasim/IdeaProjects/hsc-deamon/src/jvmTest/resources/attributes.xml"))
        }

        //when
        val response = sender.sendMatch("viters","2ab6596a-422b-4144-9b60-121fc922030b", body)


        //then
        println(response.toString())
        response.status shouldBe HttpStatusCode.OK

    }

})
