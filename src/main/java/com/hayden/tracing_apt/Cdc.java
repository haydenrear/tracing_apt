package com.hayden.tracing_apt;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
public @interface Cdc {

    LoggingPattern[] value() default  {};

}
