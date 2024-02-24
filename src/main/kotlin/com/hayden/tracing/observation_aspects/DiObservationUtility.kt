package com.hayden.tracing.observation_aspects

import com.hayden.tracing.model.Trace
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.springframework.stereotype.Component

@Component
class DiObservationUtility(
    val arguments: List<ArgumentExtractor>,
    val consumer: List<MessageCapture>,
    val serializers: List<ClassSerializer>,
    val matcher:  List<BehaviorMatcher>
) : ObservationUtility<ObservationBehavior.DiObservationArgs> {

    override fun consumer(argumentExtractor: ObservationBehavior.DiObservationArgs, trace: Trace) {
        TODO("Not yet implemented")
    }

    override fun matches(argumentExtractor: ObservationBehavior.DiObservationArgs) {
        TODO("Not yet implemented")
    }

    override fun serializer(argumentExtractor: ObservationBehavior.DiObservationArgs) {
        TODO("Not yet implemented")
    }

    override fun extract(
        argumentExtractor: ObservationBehavior.DiObservationArgs,
        proceeding: JoinPoint
    ): Map<String, *>? {
        TODO("Not yet implemented")
    }

    override fun getSerializer(value: Any): ClassSerializer? {
        TODO("Not yet implemented")
    }

    override fun matchers(): MutableCollection<BehaviorMatcher> {
        TODO("Not yet implemented")
    }
}