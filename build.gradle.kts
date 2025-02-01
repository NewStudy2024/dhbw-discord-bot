plugins {
    id("java")
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

dependencies {}