package com.hayden.tracing_apt;

import com.hayden.tracing.observation_aspects.*;
import lombok.ToString;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Logged {

    /**
     * Extract the arguments from the method call.
     * @return
     */
    Class<? extends ArgumentExtractor> argumentExtractor() default ArgumentExtractor.DefaultArgumentExtractor.class;

    /**
     * Perform some operation on the Trace after it is extracted.
     * @return
     */
    Class<? extends MessageCapture> messageCapture() default MessageCapture.DefaultMessageCapture.class;
    /**
     * Filter dynamically at runtime whether the trace is called.
     * @return
     */
    Class<? extends BehaviorMatcher>[] behaviorMatcher() default {BehaviorMatcher.DefaultBehaviorMatcher.class};

    Class<? extends ClassSerializer>[] classSerializers() default {};

    String logId() default "";

}