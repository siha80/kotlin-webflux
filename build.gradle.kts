import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.2"
    id("io.spring.dependency-management") version "1.1.0"

    val kotlinVersion = "1.8.22"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("org.flywaydb.flyway") version "9.8.1"
}

group = "com.siha"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

object Version {
    const val kotest = "5.6.2"
    const val kotestSpringExtension = "1.1.3"
    const val mockk = "1.12.3"
    const val arrow = "1.2.0"

    const val springCloud = "2022.0.3"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")

    implementation("io.github.microutils:kotlin-logging:3.0.5")

    implementation("io.arrow-kt:arrow-core:${Version.arrow}")
    implementation("io.arrow-kt:arrow-fx-coroutines:${Version.arrow}")

    implementation("net.logstash.logback:logstash-logback-encoder:6.6")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")

    testImplementation("io.kotest:kotest-runner-junit5-jvm:${Version.kotest}")
    testImplementation("io.kotest:kotest-assertions-core:${Version.kotest}")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:${Version.kotestSpringExtension}")
    testImplementation("io.kotest:kotest-property:${Version.kotest}")
    testImplementation("io.mockk:mockk:${Version.mockk}")

    implementation("io.r2dbc:r2dbc-h2")
    testImplementation("org.flywaydb:flyway-core")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
