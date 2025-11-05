FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy the built JAR file
COPY target/lms-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080
EXPOSE 8080

# Set environment variables (if any)
#ENV DATABASE_URL=jdbc:mysql://localhost:3306/lms
# ENV SPRING_PROFILES_ACTIVE=prod

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]