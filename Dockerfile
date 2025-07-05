# Use lightweight JDK base image
FROM eclipse-temurin:17-jdk-alpine

# Add Maven build artifact
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Expose app port
EXPOSE 8080

# Run the JAR
ENTRYPOINT ["java", "-jar", "/app.jar"]
