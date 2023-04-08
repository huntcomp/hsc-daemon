package app.hsc.sender


import io.ktor.client.statement.*
import io.ktor.http.*
import mu.KotlinLogging
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.time.TimeMark
import kotlin.time.TimeSource

private val interval = 2.minutes

@OptIn(ExperimentalTime::class)
class CooldownAttributesSender(
    private val decorate: AttributesSender
) : AttributesSender {

    private val lastTimeSent = AtomicReference<TimeMark>()
    private val logger = KotlinLogging.logger {}
    override suspend fun sendMatch(player: String, body: String): HttpResponse? {
        val mark = lastTimeSent.get()
        if (mark != null && mark.plus(interval).hasNotPassedNow()) {
            val elapsedSeconds = mark.elapsedNow().inWholeSeconds
            val remaining = interval.inWholeSeconds - elapsedSeconds
            logger.info { "Still on cooldown. Remaining $remaining seconds, skipping" }
            return null
        }
        logger.info { "The cooldown no longer exists, sending" }
        val response = decorate.sendMatch(player, body)
        if (response != null && response.status.isSuccess()) {
            lastTimeSent.set(TimeSource.Monotonic.markNow())
        }
        return response
    }
}