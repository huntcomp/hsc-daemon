package app.hsc.exceptions

import app.hsc.Supabase
import io.kotest.common.runBlocking
import mu.KotlinLogging

class InvalidJwtHandler(
    private val supabase: Supabase
) : ExceptionHandler {

    private val logger = KotlinLogging.logger {}
    override fun handle(ex: Exception): HandlerResult {
        if (ex.message?.contains("Invalid JWT") == true) {
            runBlocking {
                logger.warn { "Invalid JWT. Trying to log in again" }
                supabase.resolveInvalidJWTError()
            }
            return HandlerResult.RETRY
        }
        return HandlerResult.BREAK
    }
}