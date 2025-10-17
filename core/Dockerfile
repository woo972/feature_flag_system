# syntax=docker/dockerfile:1.4

FROM gradle:8.10.2-jdk21 AS build
WORKDIR /workspace
COPY . .
RUN chmod +x gradlew
RUN ./gradlew :core:bootJar --no-daemon

FROM eclipse-temurin:21-jre
ENV APP_HOME=/app
ENV JAVA_OPTS=""
WORKDIR ${APP_HOME}
COPY --from=build /workspace/core/build/libs/core-*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
