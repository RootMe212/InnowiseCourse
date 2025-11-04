FROM openjdk:21-slim

COPY build/libs/*.jar apigateway.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "apigateway.jar"]