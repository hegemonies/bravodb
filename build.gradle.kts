import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.70"
    id("java")
    id("com.google.cloud.tools.jib") version "2.1.0"
}

group = "org.bravo"
version = "0.1"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-reactive", "1.3.3")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("io.rsocket:rsocket-core:1.0.0-RC6")
    implementation("io.rsocket:rsocket-transport-netty:1.0.0-RC6")

    implementation("org.apache.logging.log4j", "log4j-api", "2.13.0")
    implementation("org.apache.logging.log4j", "log4j-core", "2.13.0")
    implementation("org.apache.logging.log4j", "log4j-slf4j-impl", "2.13.0")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.1")

    testImplementation("org.junit.jupiter:junit-jupiter:5.6.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

// jar {
//     manifest {
//         attributes(
//             'Main-Class': 'com.mypackage.MyClass'
//         )
//     }
// }

jib {
    container {
        mainClass = "org.bravo.bravodb.BravodbApplicationKt"
        creationTime = "USE_CURRENT_TIMESTAMP"
    }
    to {
        image = "bravo/bravodb:${version}"
    }
}
