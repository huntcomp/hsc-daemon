package app.hsc

import io.github.jan.supabase.SupabaseClient

data class HscContext(
    val supabase: SupabaseClient,
    val sender: AttributesSenderImpl,
    val tracker: FileTracker
)
