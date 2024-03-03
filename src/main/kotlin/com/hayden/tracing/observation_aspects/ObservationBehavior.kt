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

        val trace = Trace(
            Instant.now(),
            MessageMetadata(TraceMetadata(tracingProps.toServiceIds()), LogType.MESSAGE),
            Message(observationUtility.extract(observationArgs), observationArgs.id)
        )

        observationUtility.consumer(observationArgs, trace)

        val out = Observation.createNotStarted(observationArgs.id, observationRegistry)
            .highCardinalityKeyValue("trace", trace.toString())
//        AgentBuilder.Default().type(ElementMatchers.named("com.hayden.tracing.Logged"))
//            .transform()
//            .installOn()
        return if (observationArgs.joinPoint is ProceedingJoinPoint) {
            val o = out.observe(Supplier { (observationArgs.joinPoint as ProceedingJoinPoint).proceed() })
            return o
        } else {
    //            ContextRegistry.getInstance().registerContextAccessor(ReactorContextAccessor())
    //            ContextRegistry.getInstance().registerThreadLocalAccessor("UUID", ThreadLocal.withInitial({"hello"}))
    //            val context = Context.empty();
    //            val snapshot = ContextSnapshotFactory.builder()
    //                .contextRegistry(ContextRegistry.getInstance())
    //                .build().captureAll(context)
    //            val get = context.get<String>("")
    ////            snapshot.setThreadLocals()

            null
        }

    }

}