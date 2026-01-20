FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/sma-common-datamodel*.jar sma-common-datamodel.jar
EXPOSE 9093
ENTRYPOINT ["java","-jar","/app/sma-common-datamodel.jar"]
