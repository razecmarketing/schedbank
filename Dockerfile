FROM openjdk:17-jdk-slim

# Metadata
LABEL maintainer="SchedBank Engineering <engineering@schedbank.com>"
LABEL version="1.0.0"
LABEL description="Enterprise Financial Transfer Scheduler"

# Create app directory and user
RUN groupadd -r schedbank && useradd -r -g schedbank schedbank
WORKDIR /app

# Copy Maven dependencies (for better caching)
COPY pom.xml ./
COPY .mvn .mvn
COPY mvnw ./

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application
RUN ./mvnw clean package -DskipTests -B

# Runtime stage
FROM openjdk:17-jre-slim

# Install security updates
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Create app user and directory
RUN groupadd -r schedbank && useradd -r -g schedbank schedbank
WORKDIR /app

# Copy jar file
COPY --from=0 /app/target/*.jar app.jar

# Change ownership
RUN chown -R schedbank:schedbank /app

# Switch to non-root user
USER schedbank

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Expose port
EXPOSE 8080

# JVM optimization for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]