import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.hayden.kotlin")
    id("com.hayden.apt")
    id("com.hayden.spring")
    id("com.hayden.no-main-class")
}

tasks.register("prepareKotlinBuildScriptModel")


dependencies {
    implementation(project(":tracing_aspect"))
}
