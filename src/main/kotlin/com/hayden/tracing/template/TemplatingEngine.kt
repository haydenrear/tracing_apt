package com.hayden.tracing.template

import java.io.FileInputStream
import java.nio.file.Path

class TemplatingEngine {

    companion object {
        fun replace(values: Map<String, String>, path: Path): String {
            path.toFile().bufferedReader().use {
                var read = it.readLine()
                values.forEach {
                    read = read.replace("{{${it.key}}}", it.value)
                }
                return read
            }
        }
    }

}