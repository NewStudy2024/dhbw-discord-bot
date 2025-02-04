plugins {
    id("application")
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "2.1.10"
}

group = "dhbw.mos.bot.discord"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":"))
    implementation("net.dv8tion:JDA:5.2.3")
    implementation("ch.qos.logback:logback-classic:1.5.6")
}

application {
    mainClass.set("dhbw.mos.bot.discord.DiscordBackend")
}