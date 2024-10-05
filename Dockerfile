FROM docker.io/library/eclipse-temurin:21-jdk as builder
COPY . .
RUN ./gradlew hermes:bootJar

FROM docker.io/library/eclipse-temurin:21-jdk
WORKDIR /.
COPY --from=builder /hermes/build/libs/hermes-1.0.0.jar app.jar

ENTRYPOINT ["java", "-jar", "./app.jar"]
