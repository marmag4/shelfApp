# --- Stage 1: build the jar with Maven ---
# We use a full Maven+JDK image just for this stage - it's only needed to
# compile the code, so it doesn't end up in the final image.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy just the pom first and download dependencies - Docker caches this
# layer, so re-building after only changing Java code skips re-downloading
# every dependency from Maven Central.
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Now copy the actual source code and build the jar.
COPY src ./src
RUN mvn -B clean package -DskipTests

# --- Stage 2: run the jar with just a JRE (much smaller than the full JDK+Maven image) ---
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
