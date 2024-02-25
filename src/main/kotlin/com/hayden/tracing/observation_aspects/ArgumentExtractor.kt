package com.hayden.tracing.observation_aspects

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint

interface ArgumentExtractor {

    class DefaultArgumentExtractor: ArgumentExtractor {

        override fun extract(proceeding: ObservationUtility.ObservationArgs,
                             utility: ObservationUtility<out ObservationUtility.ObservationArgs>): Map<String, *> {
            return mutableMapOf<String, String>()
        }
    }


    fun extract(proceeding: ObservationUtility.ObservationArgs,
                utility: ObservationUtility<out ObservationUtility.ObservationArgs>): Map<String, *>

}