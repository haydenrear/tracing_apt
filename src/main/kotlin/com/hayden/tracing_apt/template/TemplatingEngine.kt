package com.hayden.tracing_apt.template


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