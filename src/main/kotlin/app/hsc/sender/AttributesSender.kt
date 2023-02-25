package app.hsc.sender

import io.ktor.client.statement.*

interface AttributesSender {
    suspend fun sendMatch(player: String, body: String): HttpResponse?
}
