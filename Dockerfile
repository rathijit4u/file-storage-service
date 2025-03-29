FROM eclipse-temurin:21-jdk-jammy as deps

WORKDIR /file-storage-service
CMD ["./gradlew", "clean", "bootJar"]
COPY build/libs/*.jar app.jar


EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "app.jar" ]
