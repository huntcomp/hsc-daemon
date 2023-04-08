package app.hsc.sender

import com.github.difflib.text.DiffRow
import com.github.difflib.text.DiffRowGenerator
import io.ktor.client.statement.*
import mu.KotlinLogging
import java.util.concurrent.atomic.AtomicReference


class DiffAttributesSender(
    private val decorate: AttributesSender
) : AttributesSender {

    private val logger = KotlinLogging.logger {}
    private val oldString = AtomicReference("")

    override suspend fun sendMatch(player: String, body: String): HttpResponse? {
        try {
            logBody(body)
        } catch (ex: Exception) {
            println(ex)
            println("Continuing")
        }
        return decorate.sendMatch(player, body)
    }

    private fun logBody(body: String) {
        val generator = DiffRowGenerator.create()
            .showInlineDiffs(true)
            .inlineDiffByWord(true)
            .oldTag { _: Boolean? -> "~" }
            .newTag { _: Boolean? -> "**" }
            .build()
        val oldLines = oldString.getAndUpdate {
            return@getAndUpdate body
        }.split(System.lineSeparator())

        val newLines = body.split(System.lineSeparator())
        logger.info { "counting diff" }
        val rows: List<DiffRow> = generator.generateDiffRows(
            oldLines,
            newLines
        )
        logger.info("|original|new|")
        logger.info("|--------|---|")
        for (row in rows) {
            if (row.oldLine != row.newLine) {
                logger.info("|" + row.oldLine + "|" + row.newLine + "|")
            }
        }
    }
}