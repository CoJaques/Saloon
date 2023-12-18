FROM eclipse-temurin:17
WORKDIR /app

COPY target/client-1.0.0.jar /app/client-1.0.0.jar

ENTRYPOINT ["java", "-jar", "client-1.0.0.jar"]
CMD ["--help"]