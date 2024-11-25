# Use an official Maven image to build the application
FROM maven:3.9.8-eclipse-temurin-21

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml file and download the dependencies
COPY pom.xml .
# Copy the source code into the container
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Set the working directory inside the container
WORKDIR /app

# Copy the built jar file from the build stage
COPY /app/target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]