package app.hsc

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.ktor.client.statement.*
import io.ktor.http.*

class AttributesSenderImpl(
    private val client: SupabaseClient
) : AttributesSender {

    override suspend fun sendMatch(player: String, body: String): HttpResponse {
        println("Sending data to supabase")
        val response =  client.functions(
            function = "load-match",
            body = body,
            headers = Headers.build {
                append(HttpHeaders.ContentType, "application/xml")
//                append(HttpHeaders.Authorization, "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImNtY2V1ZHJwZXRoaGNlc3pnYWtjIiwicm9sZSI6ImFub24iLCJpYXQiOjE2NzQ2MDE0MDcsImV4cCI6MTk5MDE3NzQwN30.EcRogZd23__jDYyh0hSjricCJ4lqSXaV7JeQFrNZBs4")
                append("X-Played-As", player)
//                append("X-User", userId)
            }
        )
        println("Request: ${response.request}" )
        println("Response: $response" )
        return response
    }

}