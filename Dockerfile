FROM eclipse-temurin:21-jre
WORKDIR /app
ARG JAR=target/fx-deals-importer-0.0.1-SNAPSHOT.jar
COPY ${JAR} app.jar
ENV SPRING_PROFILES_ACTIVE=docker
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]