# Stage 1: Build the application (Builder Stage)
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Give execution permission to the Maven wrapper
RUN chmod +x mvnw

# Download dependencies (this step is cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline

# Copy the source code and build the application
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: Create the minimal production image (Final Stage)
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Create a non-root user for better security (optional but recommended)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the built jar file from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
