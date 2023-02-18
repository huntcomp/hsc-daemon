package app.hsc

import dev.vishna.watchservice.KWatchEvent
import dev.vishna.watchservice.asWatchChannel
import io.kotest.mpp.file
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import java.nio.file.Path

class FileTracker() {
    @OptIn(DelicateCoroutinesApi::class)
    suspend fun track(file: Path, action: (KWatchEvent) -> Unit) {

        val watchChannel = file.toFile().asWatchChannel()
        GlobalScope.launch {
            return@launch watchChannel.consumeEach(action)
        }


    }

}
