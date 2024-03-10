package com.hayden.tracing.observation_aspects

import com.hayden.tracing.model.Trace
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.reflect.CodeSignature
import java.util.*

interface ObservationUtility<T: ObservationUtility.ObservationArgs> {

    interface ObservationArgs {
        val id: String
        val monitoringTypes: List<MonitoringTypes>

        fun args(): Map<String, *>
    }

    interface JoinPointObservationArgs: ObservationArgs {
        val joinPoint: JoinPoint

        override fun args(): Map<String, *> {
            return joinPoint.args
                .mapIndexed{ i, nextArg ->  getParameterName(joinPoint, i)
                    ?.flatMap { if (nextArg == null) listOf() else listOf<Pair<String, *>>(Pair(it.toString(), nextArg)) }
                }
                .flatMap { it!! }
                .toMap()
        }

        fun getParameterName(joinPoint: JoinPoint, numArg: Int): String? {
            if (joinPoint.signature is CodeSignature && (joinPoint.signature as CodeSignature?)?.parameterNames?.any { it.length < numArg } == true) {
                return (joinPoint.signature as CodeSignature?)?.parameterNames?.get(numArg)
            }

            return null
        }
    }

    fun extractData(argumentExtractor: T): Map<String, *>?
    fun extractTrace(argumentExtractor: T): Map<String, *>?
    fun consumer(argumentExtractor: T, trace: Trace)
    fun getSerializer(value: Any): ClassSerializer?
    fun matchers(args: ObservationArgs): List<BehaviorMatcher>

}