# syntax=docker/dockerfile:1.4

FROM gradle:8.10.2-jdk21 AS build
WORKDIR /workspace
COPY . .
RUN chmod +x gradlew
RUN ./gradlew :shared:jar --no-daemon

FROM eclipse-temurin:21-jre
ENV LIB_HOME=/opt/shared
WORKDIR ${LIB_HOME}
COPY --from=build /workspace/shared/build/libs/shared-*.jar shared.jar
CMD ["sh", "-c", "echo 'Shared module library image built. Retrieve /opt/shared/shared.jar for reuse.'"]
