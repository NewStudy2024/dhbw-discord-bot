plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "2.1.10"
    id("com.apollographql.apollo") version "4.1.1"
}

group = "dhbw.mos.bot"
version = "1.0"

repositories {
    mavenCentral()
}

allprojects {
    afterEvaluate {
        java {
            toolchain {
                languageVersion = JavaLanguageVersion.of(21)
            }
        }
    }
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("com.apollographql.java:client:0.0.2")
}

apollo {
    service("github") {
        generateKotlinModels.set(false)
        packageName.set("dhbw.mos.bot.github.graphql")
        srcDir(file("src/main/graphql"))
        outputDirConnection {
            connectToJavaSourceSet("main")
        }
    }
}

kotlin {
    compilerOptions {
        optIn.add("kotlinx.serialization.ExperimentalSerializationApi")
    }
}