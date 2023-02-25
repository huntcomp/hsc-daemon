package app.hsc.sender

import app.hsc.Supabase
import io.ktor.client.statement.*
import mu.KotlinLogging

class AttributesSenderImpl(
    private val supabase: Supabase
) : AttributesSender {

    private val logger = KotlinLogging.logger {}
    override suspend fun sendMatch(player: String, body: String): HttpResponse {
        logger.info("Sending data to supabase")
        val response = supabase.loadMatch(body, player)
        logger.info("Request: ${response.request}")
        logger.info("Response: $response")
        return response
    }


}
