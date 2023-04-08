package app.hsc

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.Discord
import io.ktor.http.*

class Supabase(
    private val client: SupabaseClient
) {
    suspend fun logIn(): SessionStatus {
        if (client.gotrue.loadFromStorage()) {
            return client.gotrue.sessionStatus.value
        }

        client.gotrue.loginWith(Discord)
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
