FROM eclipse-temurin:21-jre
WORKDIR /app
COPY main-modules/bootstrap/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]