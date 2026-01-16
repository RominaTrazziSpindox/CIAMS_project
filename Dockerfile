# ==========================
# BUILD STAGE
# ==========================
# This stage is responsible ONLY for building the application.
# It uses Gradle with JDK 17 to compile the Spring Boot project
# and produce the final executable JAR.

FROM gradle:8.5-jdk17 AS builder

# Set the working directory inside the container
# All subsequent commands will be executed from /app
WORKDIR /app

# Copy the entire project into the container
# This includes source code, configuration and exclude files mentioned in .dockerignore
COPY . .

# Build the project using Gradle
# -x test skips tests to speed up the Docker build
# -q quiet mode
# (tests should be run separately in CI or locally)
RUN gradle build -q -x test


# ==========================
# RUNTIME STAGE
# ==========================
# This stage creates the final lightweight runtime image.
# It contains ONLY the JRE and the built JAR, nothing else.
FROM eclipse-temurin:17-jre

# Set the working directory for the runtime container
WORKDIR /app

# Copy only the final JAR produced in the build stage
# This keeps the image small and secure
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the port used by Spring Boot
EXPOSE 8080

# Start the Spring Boot application
# The active Spring profile is provided externally (in docker-compose or environment variables)
ENTRYPOINT ["java", "-jar", "app.jar"]



