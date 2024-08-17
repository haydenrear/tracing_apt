import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.hayden.apt")
    id("com.hayden.kotlin")
    id("com.hayden.spring")
    id("com.hayden.no-main-class")
}

tasks.register("prepareKotlinBuildScriptModel")


dependencies {

}


tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "21"
    }
}


tasks.test {
    enabled = true
}

repositories {
    mavenCentral()
}