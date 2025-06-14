# Use Amazon Corretto JDK 21 (Alpine) as the base image
FROM amazoncorretto:21-alpine3.21-jdk

# Create a non-root user to run the application
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

# Set the working directory
WORKDIR /app

# Copy the JAR file into the container
COPY target/email-processor-0.0.1.jar email-processor.jar

# Start the application
CMD ["java", "-jar", "email-processor.jar"]