package app.hsc

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.gotrue.GoTrue
import io.ktor.client.engine.apache.*
import io.ktor.client.engine.cio.*

class Config {
    fun configureContext(): HscContext {
        val client = createSupabaseClient(
            supabaseUrl = "https://cmceudrpethhceszgakc.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImNtY2V1ZHJwZXRoaGNlc3pnYWtjIiwicm9sZSI6ImFub24iLCJpYXQiOjE2NzQ2MDE0MDcsImV4cCI6MTk5MDE3NzQwN30.EcRogZd23__jDYyh0hSjricCJ4lqSXaV7JeQFrNZBs4"
        ) {
            install(GoTrue) {
                httpPort = 9753
                autoLoadFromStorage = true
            }
            install(Functions)
        }
        val sender = AttributesSenderImpl(client)
//        val tracker = FileTracker()
        return HscContext(
            client,
            sender,
//            tracker
        )
    }
}
