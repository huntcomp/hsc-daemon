package app.hsc

import com.sun.jna.platform.win32.Advapi32Util.*
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.Discord
import io.kotest.common.runBlocking
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.awaitility.Awaitility
import java.nio.file.Files
import java.security.MessageDigest
import java.time.Duration
import java.util.stream.Collectors

class App {
    @OptIn(DelicateCoroutinesApi::class)
    fun run(args: Array<String>) {
        val huntAttributesPath = AttributesPathRetriever.getHuntAttributesPath(args)
        val playerName = PlayerNameRetriever.getPlayerName(args)
        val config = Config()
        val context = config.configureContext()
        println("Start")


        val job = GlobalScope.launch {
            if (!context.supabase.gotrue.loadFromStorage()) {
                context.supabase.gotrue.loginWith(Discord)
            }
            println("Start")
            var signature = ""
            context.tracker.track(huntAttributesPath!!) { event ->
                println("File Change: ${event.kind.kind}")
                if (event.kind.kind == "modified") {
                    println("File has been modified")
                    val body = runBlocking { Files.readString(huntAttributesPath) }
                    if(body.isEmpty()) {
                        return@track
                    }
                    val currentSignature = checkSignature(body)
                    println("Current signature $currentSignature")
                    if (signature == currentSignature) {
                        println("Signature has not been changed")
                        return@track
                    }
                    signature = currentSignature
                    runBlocking { context.sender.sendMatch(playerName, body) }
                }
            }

        }
        Awaitility.await().forever().pollInterval(Duration.ofSeconds(5)).until {
            job.isActive
        }
        println("Finish")
    }

    private fun checkSignature(body: String): String {
        val stringToHash = body.split(System.lineSeparator())
            .filter { it.contains("MissionBagPlayer_") }
            .reduce { acc, string -> acc + string }
        return getSha256(stringToHash)
    }

    private fun getSha256(body: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(body.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }


}
