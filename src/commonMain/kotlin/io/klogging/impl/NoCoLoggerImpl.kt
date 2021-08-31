/*

   Copyright 2021 Michael Strasser.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

package io.klogging.impl

import io.klogging.Level
import io.klogging.NoCoLogger
import io.klogging.eventFrom
import io.klogging.events.LogEvent
import io.klogging.events.timestampNow
import io.klogging.template.templateItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

public class NoCoLoggerImpl(
    override val name: String,
) : NoCoLogger {
    override fun emitEvent(
        level: Level,
        throwable: Throwable?,
        event: Any?,
        contextItems: Map<String, Any?>,
    ) {
        val eventToLog = eventFrom(level, throwable, event, contextItems)
        CoroutineScope(Job()).launch {
            Logging.sendEvent(eventToLog)
        }
    }

    override fun e(template: String, vararg values: Any?): LogEvent {
        val items = templateItems(template, *values).mapValues { e -> e.value }
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
