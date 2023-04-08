package app.hsc

import com.sun.jna.platform.win32.Advapi32Util
import com.sun.jna.platform.win32.WinReg
import mu.KotlinLogging
import java.nio.file.Path


object AutostartAdder {

    private val logger = KotlinLogging.logger {}

    private const val AUTOSTART_KEY = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run"
    private const val HSC_REGISTRY_NAME = "Hsc"

    fun addToAutostart() {
        val currentPath = Path.of("hsc-daemon.exe").toAbsolutePath()
        if(autoStartRegistryExists(currentPath)) {
            logger.info { "App already added to autostart" }
            return
        }
        logger.info { "Adding app to autostart" }
        addAutoStarToAutoStartRegistry(currentPath)
        logger.info { "Added app to autostart under path $AUTOSTART_KEY with key $HSC_REGISTRY_NAME with value $currentPath" }
    }

    private fun addAutoStarToAutoStartRegistry(currentPath: Path) {
        logger.info { "Adding app to autostart with value $currentPath" }
        Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, AUTOSTART_KEY, HSC_REGISTRY_NAME, currentPath.toString() )
    }

    private fun autoStartRegistryExists(currentPath: Path): Boolean {
        if( !Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, AUTOSTART_KEY, HSC_REGISTRY_NAME)){
            logger.info { "Registry does not exist" }
            return false
        }
        logger.info { "Key exists, checking value" }
        val registryValue = Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, AUTOSTART_KEY,HSC_REGISTRY_NAME)
        logger.info { "Registry value: $registryValue" }
        logger.info { "Current path value: $currentPath" }
        return Path.of(registryValue).equals(currentPath)
    }

}