# Multi-stage build for SQLGatewayApp
# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-11 AS builder

WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the WAR file
RUN mvn clean package -DskipTests

# Stage 2: Runtime with Tomcat
FROM tomcat:10.1-jdk11

# Remove default Tomcat applications
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy the WAR file from builder stage
COPY --from=builder /app/target/SQLGatewayApp.war /usr/local/tomcat/webapps/SQLGatewayApp.war

# Copy Docker-specific context.xml for database configuration
COPY context-docker.xml /usr/local/tomcat/conf/Catalina/localhost/SQLGatewayApp.xml

# Expose Tomcat port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
