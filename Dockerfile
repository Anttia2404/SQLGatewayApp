# Multi-stage build for SQLGatewayApp - Render Deployment
# Force rebuild: 2025-11-27 15:54 - Clear all caches
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

# Copy the WAR file from builder stage (ROOT.war deploys at root context)
COPY --from=builder /app/target/ROOT.war /usr/local/tomcat/webapps/ROOT.war

# Context.xml is already embedded in WAR file with correct Render database config

# Expose Tomcat port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
