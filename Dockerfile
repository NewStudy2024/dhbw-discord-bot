FROM eclipse-temurin:21

WORKDIR dhbw-bot-build

COPY gradle/ ./gradle/
COPY gradlew *.gradle.kts ./
COPY discord/*.gradle.kts ./discord/
RUN ["./gradlew"]

COPY src/ ./src
COPY discord/ ./discord
RUN ["./gradlew", "shadowJar"]


FROM eclipse-temurin:21

WORKDIR /dhbw-bot-lib
COPY --from=0 /dhbw-bot-build/discord/build/libs/discord-1.0-all.jar ./

WORKDIR /config-volume
CMD ["java", "-jar", "/dhbw-bot-lib/discord-1.0-all.jar"]