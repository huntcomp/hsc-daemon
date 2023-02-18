package app.hsc

import com.sun.jna.platform.win32.Advapi32Util
import com.sun.jna.platform.win32.WinReg

object PlayerNameRetriever {
    const val STEAM_USER_REGISTRY = "SOFTWARE\\Valve\\Steam"
    const val LAST_NAME_RECORD_NAME = "LastGameNameUsed"

    fun getPlayerName(args: Array<String>): String = getSteamName() ?: getUserDefinedPlayerName(args)

    private fun getUserDefinedPlayerName(args: Array<String>) =
        if (args.size < 2)
            throw RuntimeException("No registry '$STEAM_USER_REGISTRY' with value in '$LAST_NAME_RECORD_NAME'")
        else args[1]

    private fun getSteamName(): String? =
        if (registryExists())
            getRegistrySteamLastName()
        else
            null

    private fun registryExists() = steamNameRegistryExists() && steamNameRegistryValueExists()

    private fun getRegistrySteamLastName(): String? =
        Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, STEAM_USER_REGISTRY, LAST_NAME_RECORD_NAME)

    private fun steamNameRegistryExists() =
        Advapi32Util.registryKeyExists(WinReg.HKEY_CURRENT_USER, STEAM_USER_REGISTRY)

    private fun steamNameRegistryValueExists() =
        Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, STEAM_USER_REGISTRY, LAST_NAME_RECORD_NAME)
}
