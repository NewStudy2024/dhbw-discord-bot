plugins {
    id("java")
}

group = "dhbw.mos.bot.discord"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.dv8tion:JDA:5.2.3")
    implementation("ch.qos.logback:logback-classic:1.5.6")
}