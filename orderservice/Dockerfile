FROM openjdk:21-slim

COPY build/libs/*.jar orderservice.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "orderservice.jar"]