package com.hayden.tracing.template

import com.hayden.tracing_apt.template.TemplatingEngine
import org.junit.jupiter.api.Test


class TemplatingEngineTest {

    @Test
    fun doTestTemplatingEngine() {
        val out = TemplatingEngine.replace(mutableMapOf(
            Pair("aspect_name", "TestAspect"),
            Pair("advice", "TestAdvice"),
            Pair("advice_methods", "Test"),
            Pair("before", "test"),
            Pair("after", "test"),
            Pair("around", "test"),
            Pair("aspect_fn_name", "test"),
            Pair("aspect_name", "test"),
            Pair("monitoring_types", "test")
        ), "src/main/resources/com/hayden/tracing_apt/template/observation_aspect_provided_template.txt");
        println(out);
    }

}