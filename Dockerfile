FROM openjdk:17-jdk-slim
COPY /target/ezbudget-server-0.0.1-SNAPSHOT.jar ./ezbudget-server.jar
WORKDIR .
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "ezbudget-server.jar"]
