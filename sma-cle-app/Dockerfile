# ---------- Build stage ----------
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy only the files/folders required for the ordered build.
# Keep directory structure so relativePath in child POMs (../sma-parent/pom.xml) resolves correctly.
COPY sma-parent/ ./sma-parent/
COPY sma-common-datamodel/ ./sma-common-datamodel/
COPY sma-core-service/ ./sma-core-service/
COPY sma-cle-app/ ./sma-cle-app/
COPY sma-api-platform-amqp-service/ ./sma-api-platform-amqp-service/
COPY sma-platform-grpc-protobuf/ ./sma-platform-grpc-protobuf/

# Optional: show what we copied (useful for debugging in CI)
RUN ls -la /workspace && \
    ls -la /workspace/sma-parent || true && \
    ls -la /workspace/sma-cle-app || true

# 1) Install parent POM to local repo (non-recursive)
RUN mvn -B -N -f sma-parent/pom.xml install

# 2) Build (or install) the common module so other modules can use it.
# Use 'install' if other modules need to resolve its artifact from local repo.
RUN mvn -B -f sma-common-datamodel/pom.xml -DskipTests install

# 3) Build the core module (package or install depending on downstream needs)
RUN mvn -B -f sma-core-service/pom.xml -DskipTests install

# 4) Build the platform-grpc-protobuf module
RUN mvn -B -f sma-platform-grpc-protobuf/pom.xml -DskipTests install

# 5) Build the api-platform-amqp-service module
RUN mvn -B -f sma-api-platform-amqp-service/pom.xml -DskipTests install

# 6) Build the cle module (final application)
RUN mvn -B -f sma-cle-app/pom.xml -DskipTests package

# Verify produced artifact
RUN echo "CLE target contents:" && ls -la sma-cle-app/target || true


# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre-alpine
ARG JAR_FILE=target/*.jar

# Copy the built jar from the cle module
COPY --from=build /workspace/sma-cle-app/${JAR_FILE} /app/app.jar

EXPOSE 9099
ENTRYPOINT ["java","-jar","/app/app.jar"]
