package app.hsc

import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.Discord

class Supabase(
    private val context: HscContext
) {
    suspend fun logIn(): SessionStatus {
        if (context.supabase.gotrue.loadFromStorage()) {
            return context.supabase.gotrue.sessionStatus.value
        }
        context.supabase.gotrue.loginWith(Discord)
        return context.supabase.gotrue.sessionStatus.value
    }
}
