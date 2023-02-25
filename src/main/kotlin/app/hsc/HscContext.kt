package app.hsc

import app.hsc.sender.AttributesSender

data class HscContext(
    val supabase: Supabase,
    val sender: AttributesSender,
//    val tracker: FileTracker
)
