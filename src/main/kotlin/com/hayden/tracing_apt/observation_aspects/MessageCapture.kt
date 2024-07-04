package com.hayden.tracing_apt.observation_aspects

import com.hayden.tracing_apt.model.Trace

/**
 * Save to the database.
 */
interface MessageCapture {

    fun mapMessage(trace: Trace): Trace

    class DefaultMessageCapture: MessageCapture {
        override fun mapMessage(trace: Trace): Trace {
            return trace
        }

    }

}