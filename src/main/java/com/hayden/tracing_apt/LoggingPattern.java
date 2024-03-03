package com.hayden.tracing_apt;

import com.hayden.tracing.observation_aspects.*;
import org.aspectj.lang.annotation.*;

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

    MonitoringTypes[] monitoringTypes() default {};

    String aspectName();
    String aspectFunctionName();

    String logId();


}