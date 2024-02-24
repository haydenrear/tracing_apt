package com.hayden.tracing.observation_aspects

interface ClassSerializer {

    fun doSerialize(value: Any): String

    fun matches(value: Class<*>): Boolean


}