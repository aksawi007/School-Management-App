FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/sma-admin-mngt-app*.jar app.jar
EXPOSE 9093
ENTRYPOINT ["java","-jar","/app/app.jar"]
