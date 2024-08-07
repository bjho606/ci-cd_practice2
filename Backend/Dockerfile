# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the Gradle wrapper and project files into the container
COPY . .

# Grant execution permissions to the Gradle wrapper
RUN chmod +x gradlew

# Build the project using the Gradle wrapper
RUN ./gradlew build
RUN ./gradlew bootJar

# Copy the built JAR file to the /app directory
COPY build/libs/Backend-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose port 5000
EXPOSE 5000

# Set the entry point to run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
