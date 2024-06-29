package com.hayden.tracing;


import java.lang.annotation.*;

/**
 * Aspects generated to decorate function calls with observations. These observations are then distributed to services with stack traces
 * to create decisions about further tracing. Then an agent decorates functions for reasons like performance or error monitoring, or
 * general explainability and observability and program introspection dynamically. The information from these is then distributed for
 * further monitoring decisions, program structure introspection, and performance tuning tips.
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
public @interface Cdc {

    LoggingPattern[] value() default  {};

}
