/*

   Copyright 2021-2024 Michael Strasser.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       https://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

package io.klogging.impl

import io.klogging.Level
import io.klogging.NoCoLogger
import io.klogging.events.EventItems
import io.klogging.events.LogEvent
import io.klogging.events.threadContext
import io.klogging.events.timestampNow
import io.klogging.internal.Emitter
import io.klogging.internal.KloggingEngine
import io.klogging.internal.kloggingParentContext
import io.klogging.templating.templateItems
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Standard implementation of [NoCoLogger].
 * @property name logger name
 * @property loggerContextItems context items belonging to this logger
 */
public class NoCoLoggerImpl(
    override val name: String,
    override val loggerContextItems: EventItems = mapOf(),
) : CoroutineScope, NoCoLogger {

    /**
     * Context in which this logger will launch coroutines
     */
    public override val coroutineContext: CoroutineContext
        get() = kloggingParentContext

    /**
     * Emit an event to be dispatched and sent.
     * @param level logging level for the event
     * @param throwable any [Throwable] associated with the event
     * @param event something to emit: a [LogEvent] or other object
     * @param contextItems context items to include in the event
     */
    public override fun emitEvent(
        level: Level,
        throwable: Throwable?,
        event: Any?,
        contextItems: EventItems,
    ) {
        val eventToLog = eventFrom(threadContext(), level, throwable, event, contextItems + otherItems())
        if (eventToLog.level < KloggingEngine.minDirectLogLevel()) {
            launch(CoroutineName("NoCoLogger")) {
                Emitter.emit(eventToLog)
            }
        } else {
            Emitter.emitDirect(eventToLog)
        }
    }

    private fun otherItems(): EventItems = KloggingEngine.otherItemExtractors
        .fold(mutableMapOf()) { items, extractor ->
            items.putAll(extractor())
            items
        }

    /**
     * Construct a [LogEvent] from a template and values.
     * @param template [Message template](https://messagetemplates.org) to interpret
     * @param values values corresponding to holes in the template
     * @return a [LogEvent] with context items mapped to the template
     */
    @Suppress("IDENTIFIER_LENGTH")
    public override fun e(template: String, vararg values: Any?): LogEvent {
        val items = templateItems(template, *values)
        return LogEvent(
            timestamp = timestampNow(),
            logger = this.name,
            level = minLevel(),
            template = template,
            message = template,
            stackTrace = null,
            items = items,
        )
    }
}
