package app.hsc.exceptions

interface ExceptionHandler {
    fun handle(ex: Exception) : HandlerResult
}
