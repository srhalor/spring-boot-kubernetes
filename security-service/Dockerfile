# Use Amazon Corretto JDK 21 (Alpine) as the base image
FROM amazoncorretto:21-alpine3.21-jdk

# Create a non-root user to run the application
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

# Set the working directory
WORKDIR /app

# Copy the JAR file into the container
COPY target/security-service-0.0.1.jar security-service.jar

# Expose the port the application runs on
EXPOSE 8080

# Start the application
CMD ["java", "-jar", "security-service.jar"]