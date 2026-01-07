# OpenJDK image (Java 21)
FROM eclipse-temurin:21-jdk-alpine

# Set working directory inside the container
WORKDIR /app

# Copy maven wrapper and pom.xml to working directory
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Copy source code
COPY src ./src

# Build the app
RUN ./mvnw package -DskipTests

# Run the Java application as soon as container starts
CMD ["java", "-jar", "target/mycitygov-0.0.1-SNAPSHOT.jar"]