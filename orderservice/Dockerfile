FROM eclipse-temurin:21-jre-jammy

COPY build/libs/*.jar orderservice.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "orderservice.jar"]