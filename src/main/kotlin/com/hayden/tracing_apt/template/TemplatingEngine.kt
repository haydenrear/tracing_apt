package com.hayden.tracing_apt.template

import com.hayden.tracing_apt.observation_aspects.MonitoringTypes
import java.io.File
import java.io.StringWriter
import java.nio.file.Path
import java.util.*


class TemplatingEngine {

    companion object {
        fun replace(values: MutableMap<String, String>, path: String): String {
            val outBuilder = StringBuilder()
            TemplatingEngine::class.java.classLoader.getResourceAsStream(path)?.bufferedReader().use {
                it?.readLines()?.forEach {
                    var read = it
                    for (value in values) {
                        read = read.replace("{{${value.key}}}", value.value.strip().replace(System.lineSeparator(), ""))
                    }

                    if (read.isNotEmpty())
                        outBuilder.append(System.lineSeparator()).append(read)
                }
            }
            return outBuilder.toString();
        }
    }

}