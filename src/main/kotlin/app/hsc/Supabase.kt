package app.hsc

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.Discord
import io.ktor.http.*
import mu.KotlinLogging

class Supabase(
    private val client: SupabaseClient
) {
    private val logger = KotlinLogging.logger {}
    suspend fun logIn(): SessionStatus {
        if (client.gotrue.loadFromStorage()) {
            logger.info { "Logged with last session" }
            return client.gotrue.sessionStatus.value
        }

        logger.info { "Logging with discord" }
        client.gotrue.loginWith(Discord)
        logger.info { "Logged with discord" }
        return client.gotrue.sessionStatus.value
    }

    suspend fun resolveInvalidJWTError(): SessionStatus {
        client.gotrue.invalidateSession()
        logger.info { "Session invalidated" }
        logger.info { "Logging with discord" }
        client.gotrue.loginWith(Discord)
        logger.info { "Logged with discord" }
        return client.gotrue.sessionStatus.value
    }

    suspend fun loadMatch(body: String, player: String) = client.functions(
        function = "load-match",
        body = body,
        headers = Headers.build {
            append(HttpHeaders.ContentType, "application/xml")
            append("X-Played-As", player)
        }
    )


}
