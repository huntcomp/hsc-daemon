package app.hsc

import com.sun.jna.platform.win32.Advapi32Util
import com.sun.jna.platform.win32.WinReg
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Pattern
import kotlin.io.path.name

private const val REGISTRY_KEY_64_BIT = "SOFTWARE\\Wow6432Node\\Valve\\Steam"
private const val REGISTRY_KEY_BIT = "SOFTWARE\\Valve\\Steam"
private const val INSTALL_PATH_KEY = "InstallPath"
private const val HUNT_APP_ID = 594650

object AttributesPathRetriever {

    private val logger = KotlinLogging.logger {}

    fun getHuntAttributesPath(args: Array<String>): Path = try {
        Path.of("${getSteamAppDirectory(HUNT_APP_ID)}/user/profiles/default/attributes.xml")
    } catch (ex: Exception) {
        logger.error{ ex.message }
        if(args.isEmpty()) {
            logger.error { "There is no program arguments and cannot find attributes.xml basing on steam registry entries" }
            throw ex
        }
        logger.info { "Taking attributes from program arguments ${args[0]}" }
        Path.of(args[0])
    }


    private fun getSteamAppDirectory(steamApp: Int) =
        Files.list(Path.of("${getSteamLibraries()}/steamapps/"))
            .filter { it.name == "appmanifest_$steamApp.acf" }
            .findFirst()
            .map { getValueFromValveFile("installdir", it) }
            .map { "${getSteamLibraries()}/steamapps/common/$it" }
            .orElseThrow { RuntimeException("Could not find steam libs. Is Steam installed properly") }

    private fun getSteamLibraries() =
        getValueFromValveFile("path", Path.of("${getSteamDirectory()}/steamapps/libraryfolders.vdf"))

    private fun getValueFromValveFile(key: String, valveFile: Path): String {
        if (Files.exists(valveFile)) {
            val pattern = Pattern.compile("^\\s+\"$key\"\\s+\"(?<value>.*)\"")
            return Files.readAllLines(valveFile)
                .map { pattern.matcher(it) }
                .firstOrNull { it.matches() }
                ?.group("value")
                ?: throw RuntimeException("Could not find value of key '$key' in '${valveFile.fileName}'")
        } else throw RuntimeException("Could not find valve file")
    }

    private fun getSteamDirectory() =
        get64BitSteamDirectory() ?: getNon64BitSteamDirectory()
        ?: throw RuntimeException("No Steam install found in registry.")

    private fun getNon64BitSteamDirectory() =
        if (registryNon64Exists())
            getNon64BitSteamRegistryValue()
        else null

    private fun getNon64BitSteamRegistryValue() = Advapi32Util.registryGetStringValue(
        WinReg.HKEY_LOCAL_MACHINE,
        REGISTRY_KEY_BIT,
        INSTALL_PATH_KEY
    )

    private fun registryNon64Exists() =
        registryKeyNon64BitExists() && registryValueNon64BitExists()

    private fun registryValueNon64BitExists() = Advapi32Util.registryKeyExists(
        WinReg.HKEY_LOCAL_MACHINE,
        REGISTRY_KEY_64_BIT
    )

    private fun registryKeyNon64BitExists() = Advapi32Util.registryValueExists(
        WinReg.HKEY_LOCAL_MACHINE,
        REGISTRY_KEY_BIT,
        INSTALL_PATH_KEY
    )

    private fun get64BitSteamDirectory() =
        if (registry64Exists())
            get64BitSteamRegistryValue()
        else null

    private fun registry64Exists() =
        registryKey64BitExists() && registryValue64BitExists()

    private fun get64BitSteamRegistryValue() =
        Advapi32Util.registryGetStringValue(
            WinReg.HKEY_LOCAL_MACHINE,
            REGISTRY_KEY_64_BIT,
            INSTALL_PATH_KEY
        )

    private fun registryValue64BitExists() = Advapi32Util.registryValueExists(
        WinReg.HKEY_LOCAL_MACHINE,
        REGISTRY_KEY_64_BIT,
        INSTALL_PATH_KEY
    )

    private fun registryKey64BitExists() = Advapi32Util.registryKeyExists(
        WinReg.HKEY_LOCAL_MACHINE,
        REGISTRY_KEY_64_BIT
    )
}
