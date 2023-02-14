package app.hsc

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions.*
import java.nio.file.Path

class AttributesPathRetrieverTest : FunSpec({

    test("get hunt path") {
        //when
        val path = AttributesPathRetriever.getHuntAttributesPath(arrayOf())

        //then
        path shouldBe Path.of("C:\\Program Files (x86)\\Steam\\steamapps\\common\\Hunt Showdown\\user\\profiles\\default\\attributes.xml")
    }
})