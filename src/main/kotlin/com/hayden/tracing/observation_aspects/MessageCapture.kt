package com.hayden.tracing.observation_aspects

import com.hayden.tracing.model.Trace

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