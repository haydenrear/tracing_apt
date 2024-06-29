package com.hayden.tracing;

import java.lang.annotation.*;

/**
 * Once the agent performs a byte-code transformation it will always add a function call.
 * Additional
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoAgent {
}
