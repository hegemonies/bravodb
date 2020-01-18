import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
//    id("org.springframework.boot") version "2.1.12.BUILD-SNAPSHOT"
//    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    kotlin("jvm") version "1.3.61"
    kotlin("plugin.spring") version "1.3.61"
    id("com.google.cloud.tools.jib") version "1.8.0"
}

group = "org.bravo"
version = "0.1"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
//    maven { url = uri("https://repo.spring.io/milestone") }
//    maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("io.rsocket:rsocket-core:1.0.0-RC5")
    implementation("io.rsocket:rsocket-transport-netty:1.0.0-RC5")
//    implementation("org.springframework.boot:spring-boot-starter")
//    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}
