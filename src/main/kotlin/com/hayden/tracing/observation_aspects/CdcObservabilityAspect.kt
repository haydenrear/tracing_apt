package com.hayden.tracing.observation_aspects

import com.hayden.tracing.model.*
import com.hayden.tracing.Logged
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
open class CdcObservabilityAspect(
    private val observation: ObservationBehavior
) {


    @Around("@annotation(logged)")
    @Throws(Throwable::class)
    public open fun doCdc(joinPoint: ProceedingJoinPoint, logged: Logged): Any? {
        return observation.doObservation(ObservationBehavior.LoggedObservationArgs(joinPoint, logged, logged.monitoringTypes.toList()))
    }

}
