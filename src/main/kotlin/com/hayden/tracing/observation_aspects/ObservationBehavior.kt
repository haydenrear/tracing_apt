package com.hayden.tracing.observation_aspects

import com.fasterxml.jackson.databind.ObjectMapper
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
    private val tracingProps: TracingConfigurationProperties,
    private val om: ObjectMapper
) {


    data class LoggedObservationArgs(
        override val joinPoint: ProceedingJoinPoint,
        val logged: Logged,
        override val monitoringTypes: List<MonitoringTypes>
    ) : ObservationUtility.JoinPointObservationArgs {
        override val id: String
            get() = logged.logId
    }

    data class DiObservationArgs(
        override val joinPoint: ProceedingJoinPoint,
        override val id: String,
        override val monitoringTypes: List<MonitoringTypes>
    ) : ObservationUtility.JoinPointObservationArgs

    data class AgentObservationArgs(
        override val id: String,
        override val monitoringTypes: List<MonitoringTypes>,
        val joinPoint: AdviceJoinPoint
    ): ObservationUtility.ObservationArgs {

        data class AdviceJoinPoint(val advice: AgentAdvice,
                                   val args: Map<String, *>)

        enum class AgentAdvice {
            Enter, Exit
        }

        override fun args(): Map<String, *> {
            return joinPoint.args
        }

    }


    fun doObservation(observationArgs: ObservationUtility.ObservationArgs): Any? {
        return when (observationArgs) {
            is LoggedObservationArgs -> doDelegateObserve(observationArgs, loggedObservabilityUtility)
            is DiObservationArgs -> doDelegateObserve(observationArgs, observabilityUtility)
            is AgentObservationArgs  -> doAgentObservationArgs(observationArgs)
            else -> null
        }
    }

    private fun doAgentObservationArgs(observationArgs: AgentObservationArgs) {
        if (observationArgs.joinPoint.advice == AgentObservationArgs.AgentAdvice.Enter) {

        } else if (observationArgs.joinPoint.advice == AgentObservationArgs.AgentAdvice.Exit) {

        } else {

        }
    }

    private fun <T: ObservationUtility.JoinPointObservationArgs> doDelegateObserve(
        observationArgs: T, observationUtility: ObservationUtility<T>
    ): Any? {

        val trace = Trace(
            Instant.now(),
            MessageMetadata(TraceMetadata(tracingProps.toServiceIds()), LogType.MESSAGE),
            Message(observationUtility.extractTrace(observationArgs), observationArgs.id)
        )

        observationUtility.consumer(observationArgs, trace)

        val out = Observation.createNotStarted(observationArgs.id, observationRegistry)
            .highCardinalityKeyValue("trace", om.writeValueAsString(trace))
            .highCardinalityKeyValue("data", om.writeValueAsString(observationUtility.extractData(observationArgs)))

        return if (observationArgs.joinPoint is ProceedingJoinPoint) {
            out.observe(Supplier { (observationArgs.joinPoint as ProceedingJoinPoint).proceed() })
        } else {
    //            ContextRegistry.getInstance().registerContextAccessor(ReactorContextAccessor())
    //            ContextRegistry.getInstance().registerThreadLocalAccessor("UUID", ThreadLocal.withInitial({"hello"}))
    //            val context = Context.empty();
    //            val snapshot = ContextSnapshotFactory.builder()
    //                .contextRegistry(ContextRegistry.getInstance())
    //                .build().captureAll(context)
    //            val get = context.get<String>("")
    //            snapshot.setThreadLocals()

            null
        }

    }

}