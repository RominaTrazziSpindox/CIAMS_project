# -------- build stage --------
FROM gradle:8.5-jdk17 AS build
WORKDIR /home/gradle/project

# Copio tutto il progetto
COPY . .

# Creo il jar eseguibile Spring Boot
RUN gradle bootJar --no-daemon



# -------- runtime stage --------
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /home/gradle/project/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]