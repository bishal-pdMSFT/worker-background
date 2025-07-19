# Use Eclipse Temurin for Java 17 LTS
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/worker-background-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"] 