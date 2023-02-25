package app.hsc

import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.Path

class AttributesSenderTest : FunSpec({

    test("send attributes without logging return error").config(enabled = false) {

        //given
        val hscContext = Config().configureContext()
//        hscContext.supabase.gotrue.invalidateAllRefreshTokens()
//        hscContext.supabase.gotrue.invalidateSession()
        val sender = hscContext.sender
        val body = withContext(Dispatchers.IO) {
            Files.readString(Path.of("/Users/tharasim/IdeaProjects/hsc-deamon/src/test/resources/attributes.xml"))
        }

        //when
        val response = sender.sendMatch("Rumcajs", body)

        //then
//        println(response.request.headers)
//        println(response.request.url)
        println(response.toString())
//        response.status shouldBe HttpStatusCode.Forbidden

    }

})
