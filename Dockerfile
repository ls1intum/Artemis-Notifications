FROM docker.io/library/eclipse-temurin:17-jdk as builder
COPY . .
RUN ./gradlew hermes:bootJar

FROM docker.io/library/eclipse-temurin:17-jdk
WORKDIR /.
COPY --from=builder /hermes/build/libs/hermes-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "./app.jar"]
