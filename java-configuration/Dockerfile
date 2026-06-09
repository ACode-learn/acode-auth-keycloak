# syntax=docker/dockerfile:1

# ---- Build stage ----------------------------------------------------------
# Builds the shaded executable JAR for the java-configuration module using the
# multi-module Maven project. Build context is expected to be the repo root.
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Copy the reactor POMs first to leverage Docker layer caching for dependencies.
# The parent POM declares both the "keycloak" and "java-configuration" modules,
# so Maven needs to read keycloak/pom.xml to assemble the reactor, even though
# the keycloak module itself is not built here (java-configuration does not
# depend on it, so `-am` skips it).
COPY pom.xml ./pom.xml
COPY keycloak/pom.xml keycloak/pom.xml
COPY java-configuration/pom.xml java-configuration/pom.xml

# Warm up the local Maven repository (cached as long as the POMs don't change).
RUN --mount=type=cache,target=/root/.m2 \
    mvn -pl java-configuration -am dependency:go-offline -DskipTests -B || true

# Copy the module sources and build the shaded JAR.
COPY java-configuration/src java-configuration/src

RUN --mount=type=cache,target=/root/.m2 \
    mvn -pl java-configuration -am package -DskipTests -B

# ---- Runtime stage --------------------------------------------------------
FROM eclipse-temurin:21-jre

WORKDIR /jars

COPY --from=builder /build/java-configuration/target/java-configuration.jar /jars/java-configuration.jar

CMD ["java", "-jar", "java-configuration.jar"]
