# Stage 1: Build the application using Maven
# We use a specific version of Maven with OpenJDK 17. Change '17' if you use a different Java version.
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml file to download dependencies
COPY pom.xml .

# Download all the dependencies from pom.xml
# This step is cached, so it only runs again if your dependencies change.
RUN mvn dependency:go-offline

# Copy the rest of your source code
COPY src/ ./src

# Package the application into a JAR file. This runs your tests.
RUN mvn package -DskipTests


# Stage 2: Create the final, smaller image to run the application
# We use a slim version of OpenJDK to keep the image size small.
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file that was created in the 'build' stage
# Make sure the JAR file name matches what's in your pom.xml (artifactId-version.jar)
COPY --from=build /app/target/job-portal-backend-1.0.0.jar app.jar

# Expose the port that your Spring Boot application runs on (usually 8080)
EXPOSE 8081

# The command to run your application when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]
