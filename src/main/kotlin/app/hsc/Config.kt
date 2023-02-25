package app.hsc

import app.hsc.exceptions.GameAlreadyRegisteredHandler
import app.hsc.exceptions.InvalidJwtHandler
import app.hsc.sender.AttributesSenderExceptionHandler
import app.hsc.sender.AttributesSenderImpl
import app.hsc.sender.DiffAttributesSender
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.gotrue.GoTrue

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
        val supabase = Supabase(client)
        val sender = DiffAttributesSender(AttributesSenderImpl(supabase))
        val exceptionHandlers = listOf(
            GameAlreadyRegisteredHandler,
            InvalidJwtHandler(supabase)
        )
        val senderExceptionHandler = AttributesSenderExceptionHandler(sender, exceptionHandlers)
        return HscContext(
            supabase,
            senderExceptionHandler
        )
    }
}
