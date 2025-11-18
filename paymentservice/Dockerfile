FROM eclipse-temurin:21-jre-jammy

COPY build/libs/*.jar paymentservice.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "paymentservice.jar"]