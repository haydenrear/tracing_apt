package com.hayden.tracing.observation_aspects

import com.hayden.tracing.model.Trace
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint

interface ObservationUtility<T: ObservationUtility.ObservationArgs> {

    interface ObservationArgs {
        val joinPoint: JoinPoint
        val id: String
        val monitoringTypes: List<MonitoringTypes>
    }

    fun extract(argumentExtractor: T): Map<String, *>?

    fun consumer(argumentExtractor: T, trace: Trace)
    fun getSerializer(value: Any): ClassSerializer?
    fun matchers(args: ObservationArgs): List<BehaviorMatcher>

}