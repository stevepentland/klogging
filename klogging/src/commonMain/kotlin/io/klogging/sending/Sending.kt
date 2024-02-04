/*

   Copyright 2021-2024 Michael Strasser.

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

package io.klogging.sending

import io.klogging.events.LogEvent
import io.klogging.rendering.RenderString

/** Functional type used for sending a string to a target somewhere. */
public typealias SendString = (String) -> Unit

/** Interface for sending log events somewhere. */
public interface EventSender {
    /**
     * Send a batch of log events somewhere.
     *
     * @param batch list of events to send.
     */
    public operator fun invoke(batch: List<LogEvent>)

    /**
     * Send a single log event somewhere.
     *
     * @param event a single log event to send.
     */
    public operator fun invoke(event: LogEvent) {
        invoke(listOf(event))
    }
}

/**
 * Convert a [RenderString] and [SendString] into an [EventSender].
 *
 * @param renderer the [RenderString] that renders a log event into a string
 * @param sender the [SendString] that sends the rendered event string somewhere
 */
public fun senderFrom(renderer: RenderString, sender: SendString): EventSender = object : EventSender {
    override fun invoke(batch: List<LogEvent>) {
        sender(batch.joinToString("\n") { renderer(it) })
    }
}
