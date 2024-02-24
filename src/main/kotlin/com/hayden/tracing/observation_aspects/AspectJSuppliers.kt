package com.hayden.tracing.observation_aspects

import com.hayden.tracing_apt.TracingProcessor
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.junit.Before
import java.util.function.Supplier

open class SuppliedPointcut

class SuppliedBeforePointcut: SuppliedPointcut()
class SuppliedAfterPointcut: SuppliedPointcut()
class SuppliedAroundPointcut: SuppliedPointcut()

interface PointcutSupplier: Supplier<Set<SuppliedPointcut>>
interface AdviceSupplier: Supplier<String>


data class AspectMetadata(val aspectClassName: String, val aspectFunctionName: String);
interface AspectName: Supplier<AspectMetadata>

data class SuppliedAspect(val pointcut: PointcutSupplier, val advice: AdviceSupplier, val name: AspectName) {
    fun beforeId(): String = "before_pointcut"
    fun afterId(): String = "after_pointcut"
    fun aroundId(): String = "around_pointcut"
    fun adviceId(): String = "advice"
    fun aspectFunctionId(): String = "aspect_fn_name"
    fun aspectClassId(): String = "aspect_name"
}

class TracingAspectSupplier(
    val before: List<Before>?,
    val after: List<After>?,
    val around: List<Around>?,
    val pointCut: List<Pointcut>?,
    val aspect: List<Aspect>?,
): Supplier<SuppliedAspect> {
    override fun get(): SuppliedAspect {
        return null;
    }
}