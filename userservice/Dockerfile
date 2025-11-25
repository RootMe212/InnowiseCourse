FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY target/user-service.jar user-service.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "user-service.jar"]