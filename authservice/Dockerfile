FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY build/libs/authservice*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]