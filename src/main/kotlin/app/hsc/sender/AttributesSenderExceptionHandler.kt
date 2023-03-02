package app.hsc.sender

import app.hsc.exceptions.ExceptionHandler
import app.hsc.exceptions.HandlerResult
import io.ktor.client.statement.*
import mu.KotlinLogging
import java.util.concurrent.atomic.AtomicInteger

private const val RETRY_COUNT = 3

class AttributesSenderExceptionHandler(
    private val decorate: AttributesSender,
    private val handlers: List<ExceptionHandler>
) : AttributesSender {

    private val logger = KotlinLogging.logger {}
    private val retryCount = AtomicInteger()

    override suspend fun sendMatch(player: String, body: String): HttpResponse? {
        return try {
            decorate.sendMatch(player, body)
        } catch (ex: Exception) {
            logger.warn { "Exception: ${ex.message}" }
            val shouldRetry = handlers.map { it.handle(ex) }.any { it == HandlerResult.RETRY }
            if (shouldRetry) {
                logger.info { "Retrying sending" }
                if (retryCount.getAndUpdate { it + 1 } < RETRY_COUNT) {
                    return sendMatch(player, body)
                } else {
                    retryCount.getAndUpdate { 0 }
                    logger.info { "Retry count more than $RETRY_COUNT. Skipping" }
                }
            }
            logger.info { "The match has not been sent, skipping" }
            return null
        }
    }
}
