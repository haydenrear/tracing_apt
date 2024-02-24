package com.hayden.tracing.observation_aspects

import com.hayden.tracing.model.Trace
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint

interface ObservationUtility<T: ObservationUtility.ObservationArgs> {

    interface ObservationArgs {
        val joinPoint: JoinPoint
        val id: String
    }

    fun extract(
        argumentExtractor: T,
        proceeding: JoinPoint
    ): Map<String, *>?

    fun consumer(argumentExtractor: T, trace: Trace)
    fun matches(argumentExtractor: T)
    fun serializer(argumentExtractor: T)
    fun getSerializer(value: Any): ClassSerializer?
    fun matchers(): MutableCollection<BehaviorMatcher>
}