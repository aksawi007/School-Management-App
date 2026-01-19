# ---------- Build stage ----------
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy only the files/folders required for the ordered build.
# Keep directory structure so relativePath in child POMs (../rentin-parent/pom.xml) resolves correctly.
COPY rentin-parent/ ./rentin-parent/
COPY rentin-common-datamodel/ ./rentin-common-datamodel/
COPY rentin-core-service/ ./rentin-core-service/
COPY rentin-cust-mngt-app/ ./rentin-cust-mngt-app/
COPY rentin-api-platform-amqp-service/ ./rentin-api-platform-amqp-service/
COPY rentin-jpa-postgresql/ ./rentin-jpa-postgresql/
COPY rentin-platform-grpc-protobuf/ ./rentin-platform-grpc-protobuf/

# Optional: show what we copied (useful for debugging in CI)
RUN ls -la /workspace && \
    ls -la /workspace/rentin-parent || true && \
    ls -la /workspace/rentin-cust-mngt-app || true

# 1) Install parent POM to local repo (non-recursive)
RUN mvn -B -N -f rentin-parent/pom.xml install

# 2) Build (or install) the common module so other modules can use it.
# Use 'install' if other modules need to resolve its artifact from local repo.
RUN mvn -B -f rentin-common-datamodel/pom.xml -DskipTests install

# 3) Build the core module (package or install depending on downstream needs)
RUN mvn -B -f rentin-core-service/pom.xml -DskipTests install

# 4) Build the platform-grpc-protobuf module
RUN mvn -B -f rentin-platform-grpc-protobuf/pom.xml -DskipTests install

# 5) Build the api-platform-amqp-service module
RUN mvn -B -f rentin-api-platform-amqp-service/pom.xml -DskipTests install

# 6) Build the jpa-postgresql module
RUN mvn -B -f rentin-jpa-postgresql/pom.xml -DskipTests install

# 7) Build the customer module (final application)
RUN mvn -B -f rentin-cust-mngt-app/pom.xml -DskipTests package

# Verify produced artifact
RUN echo "Customer target contents:" && ls -la rentin-cust-mngt-app/target || true


# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre-alpine
ARG JAR_FILE=target/*.jar

# Copy the built jar from the customer module
COPY --from=build /workspace/rentin-cust-mngt-app/${JAR_FILE} /app/app.jar

EXPOSE 9091
ENTRYPOINT ["java","-jar","/app/app.jar"]
