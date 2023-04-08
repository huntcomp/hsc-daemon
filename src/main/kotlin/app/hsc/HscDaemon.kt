package app.hsc

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import com.sun.jna.platform.win32.Advapi32Util.*
import dev.vishna.watchservice.asWatchChannel
import io.kotest.common.runBlocking
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.Duration.Companion.seconds

class HscDaemon : CliktCommand() {

    private val huntAttributesPath: Path by option(help="Path to hunt attributes file").path(
        mustExist = true,
        canBeDir = false,
        canBeFile = true,
        mustBeReadable = true
    ).default( AttributesPathRetriever.getHuntAttributesPath())
    private val playerName: String by option(help="Last steam username").default(PlayerNameRetriever.getPlayerName())
    private val logger = KotlinLogging.logger {}

    @OptIn(DelicateCoroutinesApi::class)
    override fun run() {
        val config = Config()
        val context = config.configureContext()
        logger.info("Start")
        runBlocking { context.supabase.logIn() }
        val attributesListAtomicReference = AtomicReference<List<String>>(emptyList())


        val watchingJob = GlobalScope.launch {
            val watchChannel = huntAttributesPath.toFile().asWatchChannel()
            watchChannel.consumeEach { event ->
                logger.info("File Change: ${event.kind.kind}")
                if (event.kind.kind == "modified") {
                    val body = getAttributesString(huntAttributesPath)
                    if (SignatureChecker.hasChanged(body)) {
                        attributesListAtomicReference.getAndUpdate {
                            it + listOf(body)
                        }
                    }
                }
            }
        }
        while (watchingJob.isActive) {
            runBlocking { delay(5.seconds) }
            val attributesList = attributesListAtomicReference.getAndUpdate { emptyList() }
            if (attributesList.isNotEmpty()) {
                val bodyToSend = attributesList.last()
                runBlocking { context.sender.sendMatch(playerName, bodyToSend) }
            }
        }
        logger.info { "Finished" }
    }

    private fun getAttributesString(huntAttributesPath: Path): String = runBlocking {
        withContext(Dispatchers.IO) {
            Files.readString(huntAttributesPath)
        }
    }


}
