package app.hsc

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

class App {

    private val logger = KotlinLogging.logger {}

    @OptIn(DelicateCoroutinesApi::class)
    fun run(args: Array<String>) {
        val huntAttributesPath = AttributesPathRetriever.getHuntAttributesPath(args)
        val playerName = PlayerNameRetriever.getPlayerName(args)
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
