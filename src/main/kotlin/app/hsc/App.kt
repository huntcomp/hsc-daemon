package app.hsc

import com.sun.jna.platform.win32.Advapi32Util.*
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.Discord
import io.kotest.common.runBlocking
import kotlinx.coroutines.*
import mu.KotlinLogging
import org.awaitility.Awaitility
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest
import java.time.Duration

class App {

    private val logger = KotlinLogging.logger {}

    @OptIn(DelicateCoroutinesApi::class)
    fun run(args: Array<String>) {
        val huntAttributesPath = AttributesPathRetriever.getHuntAttributesPath(args)
        val playerName = PlayerNameRetriever.getPlayerName(args)
        val config = Config()
        val context = config.configureContext()
        logger.info("Start")
        val job = GlobalScope.launch {
            if (!context.supabase.gotrue.loadFromStorage()) {
                context.supabase.gotrue.loginWith(Discord)
            }
            logger.info("Start")
            context.tracker.track(huntAttributesPath) { event ->
                logger.info("File Change: ${event.kind.kind}")
                if (event.kind.kind == "modified") {
                    logger.info("File has been modified")
                    val body = getAttributesString(huntAttributesPath)
                    if (SignatureChecker.isTheSame(body)) return@track
                    runBlocking { context.sender.sendMatch(playerName, body) }
                }
            }

        }
        Awaitility.await().forever().pollInterval(Duration.ofSeconds(5)).until {
            job.isActive
        }
        logger.info("Finish")
    }

    private fun getAttributesString(huntAttributesPath: Path): String = runBlocking {
        withContext(Dispatchers.IO) {
            Files.readString(huntAttributesPath)
        }
    }


}
