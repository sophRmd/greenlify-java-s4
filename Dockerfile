FROM eclipse-temurin:25-jre

ENV LANGUAGE='en_US:en'

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]