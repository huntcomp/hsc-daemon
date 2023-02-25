package app.hsc.sender

import app.hsc.exceptions.ExceptionHandler
import app.hsc.exceptions.HandlerResult
import io.ktor.client.statement.*
import mu.KotlinLogging

class AttributesSenderExceptionHandler(
    private val decorate: AttributesSender,
    private val handlers: List<ExceptionHandler>
) : AttributesSender {

    private val logger = KotlinLogging.logger {}
    override suspend fun sendMatch(player: String, body: String): HttpResponse? {
        while (true) {
            return try {
                decorate.sendMatch(player, body)
            } catch (ex: Exception) {
                logger.warn {"Exception: ${ex.message}"  }
                val shouldRetry = handlers.map { it.handle(ex) }.any { it == HandlerResult.RETRY }
                if (shouldRetry) {
                    logger.info { "Retrying sending" }
                    continue
                }
                logger.info { "The match has not been sent, skipping" }
                null
            }
        }
    }
}