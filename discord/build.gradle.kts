plugins {
    id("application")
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "2.1.10"
    id("com.gradleup.shadow") version "9.0.0-beta8"
}

group = "dhbw.mos.bot.discord"
version = "1.0"

repositories {
    mavenCentral()
    maven { url = uri("https://m2.chew.pro/releases") }
}

dependencies {
    implementation(project(":"))
    implementation("net.dv8tion:JDA:5.2.3")
    implementation("pw.chew:jda-chewtils:2.0")
    implementation("ch.qos.logback:logback-classic:1.5.6")
}

application {
    mainClass.set("dhbw.mos.bot.discord.DiscordBackend")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "dhbw.mos.bot.discord.DiscordBackend"
    }
}