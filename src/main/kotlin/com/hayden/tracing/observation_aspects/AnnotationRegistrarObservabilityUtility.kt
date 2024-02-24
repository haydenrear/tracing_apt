package com.hayden.tracing.observation_aspects

import com.hayden.tracing.model.Trace
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.springframework.stereotype.Component
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

@Component
class AnnotationRegistrarObservabilityUtility : ObservationUtility<ObservationBehavior.LoggedObservationArgs> {

    val arguments: MutableMap<KClass<out ArgumentExtractor>, ArgumentExtractor> = mutableMapOf()
    val consumer: MutableMap<KClass<out MessageCapture>, MessageCapture> = mutableMapOf()
    val serializers: MutableMap<KClass<out ClassSerializer>, ClassSerializer> = mutableMapOf()
    val serializersCache: MutableMap<KClass<*>, ClassSerializer> = mutableMapOf()
    val matcher: MutableMap<KClass<out BehaviorMatcher>, BehaviorMatcher> = mutableMapOf()

    fun <T: Any> add(argumentExtractor: KClass<out T>, mutableMap: MutableMap<KClass<out T>, T>) {
        if (!mutableMap.containsKey(argumentExtractor))
            mutableMap[argumentExtractor] = argumentExtractor.createInstance()
    }

    override fun extract(argumentExtractor: ObservationBehavior.LoggedObservationArgs, proceeding: JoinPoint): Map<String, *>? {
        add(argumentExtractor.logged.argumentExtractor, arguments)
        return arguments[argumentExtractor.logged.argumentExtractor]?.extract(proceeding, this)
    }

    override fun consumer(argumentExtractor: ObservationBehavior.LoggedObservationArgs, trace: Trace) {
        add(argumentExtractor.logged.messageCapture, consumer)
        consumer[argumentExtractor.logged.messageCapture]?.mapMessage(trace)
    }

    override fun matches(argumentExtractor: ObservationBehavior.LoggedObservationArgs) {
        argumentExtractor.logged.behaviorMatcher
            .filter { !matcher.contains(it) }
            .map { Pair(it, it.createInstance()) }
            .forEach { matcher[it.first] = it.second }
    }

    override fun serializer(argumentExtractor: ObservationBehavior.LoggedObservationArgs) {
        argumentExtractor.logged.classSerializers
            .filter { !serializers.contains(it) }
            .map { Pair(it, it.createInstance()) }
            .forEach { serializers[it.first] = it.second }
    }

    override fun getSerializer(value: Any): ClassSerializer? {
        if (serializersCache.containsKey(value::class))  {
            return serializersCache[value::class]
        }

        serializers.values
            .filter { it.matches(value::class.java) }
            .take(1)
            .forEach { serializersCache[value::class] = it }

        return serializersCache[value::class];
    }

    override fun matchers(): MutableCollection<BehaviorMatcher> {
        return matcher.values
    }

}