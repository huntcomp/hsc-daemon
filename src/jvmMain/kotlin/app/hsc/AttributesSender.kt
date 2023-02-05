package app.hsc

import io.ktor.client.statement.*
import io.ktor.http.*

interface AttributesSender {

    suspend fun sendMatch(player: String, userId: String, body: String): HttpResponse
}
