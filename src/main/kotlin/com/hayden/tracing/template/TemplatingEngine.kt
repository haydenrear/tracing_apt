package com.hayden.tracing.template

import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Paths

class TemplatingEngine {

    companion object {
        fun replace(values: Map<String, String>, path: String): String {
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