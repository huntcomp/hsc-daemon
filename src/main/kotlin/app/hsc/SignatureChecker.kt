package app.hsc

import mu.KotlinLogging
import java.security.MessageDigest
import java.util.concurrent.atomic.AtomicReference

object SignatureChecker {

    private val logger = KotlinLogging.logger {}
    private val signature = AtomicReference("")
    fun hasChanged(body: String?): Boolean {
        if (body.isNullOrEmpty()) {
            logger.info("Empty file")
            return false
        }
        val currentSignature = getSignature(body) ?: return false
        logger.info("Current signature $currentSignature")
        if (currentSignature == signature.get()) {
            logger.info("Signature has not been changed")
            return false
        }
        logger.info("Signature has been changed")
        signature.set(currentSignature)
        return true
    }

    private fun getSignature(body: String): String? {
        val stringToHash = body.split(System.lineSeparator())
            .filter { it.contains("MissionBagPlayer_") }
            .reduceOrNull { acc, string -> acc + string } ?: return null
        return getSha256(stringToHash)
    }

    private fun getSha256(body: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(body.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }


}