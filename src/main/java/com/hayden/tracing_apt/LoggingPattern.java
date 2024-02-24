package com.hayden.tracing_apt;

import com.hayden.tracing.observation_aspects.*;
import org.aopalliance.aop.Advice;
import org.aspectj.lang.annotation.*;
import org.checkerframework.checker.units.qual.A;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
@Repeatable(Cdc.class)
public @interface LoggingPattern {

    Around[] around() default {};
    Before[] before() default {};
    After[] after() default {};
    Aspect[] aspect() default {};
    Pointcut[] pointcut() default {};

    String aspectName();
    String aspectFunctionName();

    String logId();

}