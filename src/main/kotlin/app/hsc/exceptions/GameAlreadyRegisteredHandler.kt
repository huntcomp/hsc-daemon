package app.hsc.exceptions

object GameAlreadyRegisteredHandler : ExceptionHandler {
    override fun handle(ex: Exception): HandlerResult = HandlerResult.BREAK

}