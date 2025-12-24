# OpenJDK image (Java 21)
FROM eclipse-temurin:21-jdk-alpine

# Set working directory inside the container
WORKDIR /app

# Αντιγραφή του maven wrapper και του pom.xml
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Αντιγραφή του source code
COPY src ./src

# Χτίσιμο της εφαρμογής (skip tests για ταχύτητα στο build του docker)
RUN ./mvnw package -DskipTests

# Η εντολή που θα τρέξει όταν ξεκινήσει το container
CMD ["java", "-jar", "target/mycitygov-0.0.1-SNAPSHOT.jar"]