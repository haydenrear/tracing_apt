package com.hayden.tracing.observation_aspects

import com.hayden.tracing.model.*
import com.hayden.tracing.props.TracingConfigurationProperties
import com.hayden.tracing_apt.Logged
import io.micrometer.context.ContextAccessor
import io.micrometer.context.ContextRegistry
import io.micrometer.context.ContextSnapshotFactory
import io.micrometer.context.ThreadLocalAccessor
import io.micrometer.observation.Observation
import io.micrometer.observation.Observation.CheckedFunction
import io.micrometer.observation.ObservationRegistry
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor
import io.micrometer.tracing.contextpropagation.ObservationAwareSpanThreadLocalAccessor
import org.aspectj.lang.ProceedingJoinPoint
import org.springframework.stereotype.Component
import reactor.netty.contextpropagation.ChannelContextAccessor
import reactor.util.context.Context
import reactor.util.context.ContextView
import reactor.util.context.ReactorContextAccessor
import java.time.Instant
import java.util.function.Supplier

@Component
class ObservationBehavior(
    private val observationRegistry: ObservationRegistry,
    private val loggedObservabilityUtility: ObservationUtility<LoggedObservationArgs>,
    private val observabilityUtility: ObservationUtility<DiObservationArgs>,
    private val tracingProps: TracingConfigurationProperties
) {

    data class LoggedObservationArgs(
        override val joinPoint: ProceedingJoinPoint,
        val logged: Logged,
        override val monitoringTypes: List<MonitoringTypes>
    ) : ObservationUtility.ObservationArgs {
        override val id: String
            get() = logged.logId
    }

    data class DiObservationArgs(
        override val joinPoint: ProceedingJoinPoint,
        override val id: String,
        override val monitoringTypes: List<MonitoringTypes>
    ) : ObservationUtility.ObservationArgs


    fun doObservation(observationArgs: ObservationUtility.ObservationArgs): Any? {
        return when (observationArgs) {
            is LoggedObservationArgs -> doDelegateObserve(observationArgs, loggedObservabilityUtility)
            is DiObservationArgs -> doDelegateObserve(observationArgs, observabilityUtility)
            else -> null
        }
    }

    private fun <T: ObservationUtility.ObservationArgs> doDelegateObserve(
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
        } else {
            ContextRegistry.getInstance().registerContextAccessor(ReactorContextAccessor())
            ContextRegistry.getInstance().registerThreadLocalAccessor("UUID", ThreadLocal.withInitial({"hello"}))
            val context = Context.empty();
            val snapshot = ContextSnapshotFactory.builder()
                .contextRegistry(ContextRegistry.getInstance())
                .build().captureAll(context)
            val get = context.get<String>("")
//            snapshot.setThreadLocals()

            return null
        }

    }

}