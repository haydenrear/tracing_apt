import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.hayden.apt")
    id("com.hayden.kotlin")
}

group = "com.hayden"
version = "0.0.1-SNAPSHOT"

tasks.register("prepareKotlinBuildScriptModel")

allprojects {

}

dependencies {
    implementation("com.squareup:javapoet:1.13.0")
    implementation("io.opentelemetry.instrumentation:opentelemetry-logback-appender-1.0:2.1.0-alpha")
    implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-api:2.1.0")
    implementation("io.opentelemetry.instrumentation:opentelemetry-spring-boot-starter:1.22.1-alpha")
    implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations:2.1.0")
    implementation("io.opentelemetry.javaagent:opentelemetry-javaagent:2.0.0")
    implementation("io.opentelemetry.instrumentation:opentelemetry-jdbc:2.1.0-alpha")

    implementation("io.micrometer:micrometer-tracing-bridge-brave")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.liquibase:liquibase-core")

    compileOnly("org.projectlombok:lombok")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly("org.postgresql:postgresql")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation(kotlin("stdlib-jdk8"))


}


tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "21"
    }
}


tasks.test {
    enabled = true;
}

repositories {
    mavenCentral()
}