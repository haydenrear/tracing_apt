package com.hayden.tracing.observation_aspects

import com.hayden.tracing.model.*
import com.hayden.tracing.props.TracingConfigurationProperties
import com.hayden.tracing_apt.Logged
import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationRegistry
import org.aspectj.lang.ProceedingJoinPoint
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.function.Supplier
import kotlin.math.log

@Component
class ObservationBehavior(
    private val observationRegistry: ObservationRegistry,
    private val loggedObservabilityUtility: ObservationUtility<LoggedObservationArgs>,
    private val observabilityUtility: ObservationUtility<DiObservationArgs>,
    private val tracingProps: TracingConfigurationProperties
) {

    data class LoggedObservationArgs(override val joinPoint: ProceedingJoinPoint, val logged: Logged): ObservationUtility.ObservationArgs {
        override val id: String
            get() = logged.logId
    }

    data class DiObservationArgs(override val joinPoint: ProceedingJoinPoint, override val id: String): ObservationUtility.ObservationArgs


    fun doObservation(observationArgs: ObservationUtility.ObservationArgs): Any? {
        return when (observationArgs) {
            is LoggedObservationArgs -> doUtility(observationArgs, loggedObservabilityUtility)
            is DiObservationArgs -> doUtility(observationArgs, observabilityUtility)
            else -> null
        }
    }

    private fun <T: ObservationUtility.ObservationArgs> doUtility(
        observationArgs: T, observationUtility: ObservationUtility<T>
    ): Any? {
        observationUtility.serializer(observationArgs)

        val trace = Trace(
            Instant.now(),
            MessageMetadata(TraceMetadata(tracingProps.toServiceIds()), LogType.MESSAGE),
            Message(observationUtility.extract(observationArgs, observationArgs.joinPoint), observationArgs.id)
        )

        observationUtility.consumer(observationArgs, trace)

        val out = Observation.createNotStarted(observationArgs.id, observationRegistry)
            .lowCardinalityKeyValue("trace", trace.toString())

        if (observationArgs.joinPoint is ProceedingJoinPoint) {
            return out.observe(Supplier { (observationArgs.joinPoint as ProceedingJoinPoint).proceed() });
        }

        return null
    }

}