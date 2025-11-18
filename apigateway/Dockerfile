FROM eclipse-temurin:21-jre-jammy

COPY build/libs/*.jar apigateway.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "apigateway.jar"]