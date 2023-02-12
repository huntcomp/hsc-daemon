package app.hsc

import com.sun.jna.platform.win32.Advapi32Util
import com.sun.jna.platform.win32.WinReg
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

//Placeholder
class PlayerNameRetrieverTest : FunSpec({

    val originalRegister = if (registryExists()) getRegistry() else null


    test("getPlayerName returns registry value ") {
        //given
        val expectedName = "TestName"
        setRegistry(expectedName)

        //when
        val actualName = PlayerNameRetriever.getPlayerName(arrayOf())

        //then
        actualName shouldBe expectedName
    }

    test("getPlayerName returns args[1] when no registry") {
        //given
        val expectedName = "TestName"
        deleteRegistryValue()

        //when
        val actualName = PlayerNameRetriever.getPlayerName(arrayOf("first", expectedName))

        //then
        actualName shouldBe expectedName

        //cleanup
        setRegistry(originalRegister)
    }

    test("getPlayerName throws exception when args size less than 2") {
        //given
        deleteRegistryValue()

        //when
        val exception = shouldThrowExactly<RuntimeException> {
             PlayerNameRetriever.getPlayerName(arrayOf("first"))
        }

        exception.message shouldBe "No registry '${PlayerNameRetriever.STEAM_USER_REGISTRY}' with value in '${PlayerNameRetriever.LAST_NAME_RECORD_NAME}'"
    }

    afterSpec() {
        if (originalRegister != null) {
            setRegistry(originalRegister)
        }
    }

})

fun getRegistry(): String = Advapi32Util.registryGetStringValue(
    WinReg.HKEY_CURRENT_USER,
    PlayerNameRetriever.STEAM_USER_REGISTRY,
    PlayerNameRetriever.LAST_NAME_RECORD_NAME)

fun registryExists() = Advapi32Util.registryKeyExists(
    WinReg.HKEY_CURRENT_USER,
    PlayerNameRetriever.STEAM_USER_REGISTRY
)

fun setRegistry(value: String?) = Advapi32Util.registrySetStringValue(
    WinReg.HKEY_CURRENT_USER,
    PlayerNameRetriever.STEAM_USER_REGISTRY,
    PlayerNameRetriever.LAST_NAME_RECORD_NAME,
    value
)
fun deleteRegistryValue() {
    Advapi32Util.registryDeleteValue(
        WinReg.HKEY_CURRENT_USER,
        PlayerNameRetriever.STEAM_USER_REGISTRY,
        PlayerNameRetriever.LAST_NAME_RECORD_NAME
    )
}

