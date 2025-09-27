FROM openjdk:17-jdk-slim

WORKDIR /app
COPY target/user-service.jar user-service.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar","user-service.jar"]