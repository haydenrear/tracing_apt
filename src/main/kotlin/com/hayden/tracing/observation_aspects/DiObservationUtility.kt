package com.hayden.tracing.observation_aspects

import com.hayden.tracing.model.Trace
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class DiObservationUtility(
    val arguments: List<ArgumentExtractor>,
    val consumer: List<MessageCapture>,
    val serializers: List<ClassSerializer>,
    val matcher:  List<BehaviorMatcher>
) : ObservationUtility<ObservationBehavior.DiObservationArgs> {

    val serializersCache: MutableMap<KClass<*>, ClassSerializer> = mutableMapOf()

    override fun consumer(argumentExtractor: ObservationBehavior.DiObservationArgs, trace: Trace) {
        consumer.forEach { it.mapMessage(trace) }
    }

    override fun extractData(argumentExtractor: ObservationBehavior.DiObservationArgs): Map<String, *> {
        return arguments
            .flatMap { it.extract(argumentExtractor, this).entries }
            .associate { Pair(it.key, it.value) }

    }

    override fun getSerializer(value: Any): ClassSerializer? {
        return getSerializerCache(value)
    }

    override fun matchers(args: ObservationUtility.ObservationArgs): List<BehaviorMatcher> {
        return matcher
    }

    fun getSerializerCache(value: Any): ClassSerializer? {
        if (serializersCache.containsKey(value::class))  {
            return serializersCache[value::class]
        }

        serializers
            .filter { it.matches(value::class.java) }
            .take(1)
            .forEach { serializersCache[value::class] = it }

        return serializersCache[value::class];
    }

}