package klogger

import klogger.Dispatcher.setDispatchers
import klogger.clef.dispatchClef
import klogger.clef.toClef
import klogger.context.logContext
import klogger.events.LogEvent
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

fun main() = runBlocking {

    fun LogEvent.format(fmt: String) =
        LogEvent(id, timestamp, host, logger, level, template, message, stackTrace, items + mapOf("format" to fmt))

    setDispatchers(
        { e -> dispatchClef(e.format("CLEF").toClef()) },
//        { e -> println(e.toClef()) },
    )

    val logger = logger("main")
    launch(logContext("run" to UUID.randomUUID().toString())) {
        logger.info { "Start" }
        repeat(2) { c ->
            logger.info { ">> ${c + 1}" }
            launch(logContext("counter" to (c + 1).toString())) {
                repeat(2) { i ->
                    logger.info { "Event ${i + 1} at ${LocalDateTime.now(ZoneId.of("Australia/Brisbane"))}" }
                }
            }
            logger.info { "<< ${c + 1}" }
            functionWithException(logger)
        }
        logger.info { "Finish" }
    }
    // There must be at least one statement outside the coroutine scope.
    logger.info { "All done" }
}

suspend fun functionWithException(logger: Klogger) {
    try {
        throw RuntimeException("Oops! Something went wrong")
    } catch (e: Exception) {
        logger.warn(e) { e.message!! }
    }
}

