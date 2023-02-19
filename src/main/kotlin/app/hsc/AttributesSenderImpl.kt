package app.hsc

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.UnauthorizedRestException
import io.github.jan.supabase.functions.functions
import io.ktor.client.statement.*
import io.ktor.http.*
import mu.KotlinLogging

class AttributesSenderImpl(
    private val client: SupabaseClient
) : AttributesSender {

    private val logger = KotlinLogging.logger {}
    override suspend fun sendMatch(player: String, body: String): HttpResponse? =
        try {
            logger.info("Sending data to supabase")
            val response = client.functions(
                function = "load-match",
                body = body,
                headers = Headers.build {
                    append(HttpHeaders.ContentType, "application/xml")
                    append("X-Played-As", player)
                }
            )
            logger.info("Request: ${response.request}")
            logger.info("Response: $response")
            response
        } catch (ex: UnauthorizedRestException) {
            logger.info(ex.toString())
            if (ex.message?.contains("Game was already registered") != true) {
                throw ex
            }
            null
        }

}
