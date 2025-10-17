# syntax=docker/dockerfile:1.4

FROM gradle:8.10.2-jdk21 AS build
WORKDIR /workspace
COPY . .
RUN chmod +x gradlew
RUN ./gradlew :sdk:jar --no-daemon

FROM eclipse-temurin:21-jre
ENV LIB_HOME=/opt/sdk
WORKDIR ${LIB_HOME}
COPY --from=build /workspace/sdk/build/libs/sdk-*.jar sdk.jar
CMD ["sh", "-c", "echo 'SDK library image built. Mount or copy /opt/sdk/sdk.jar for client usage.'"]
