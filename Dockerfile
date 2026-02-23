# Stage 1: Build the application
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# Install Maven
RUN apt-get update && apt-get install -y maven

# Copy pom.xml and download dependencies (for layer caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build the package
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy only the built JAR from the build stage
COPY --from=build /app/target/order-management-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Execute the application
ENTRYPOINT ["java", "-jar", "app.jar"]