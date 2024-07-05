FROM maven:3.8.5-openjdk-17 AS build
COPY target/blog-application-0.0.1-SNAPSHOT.jar blog-application.jar
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
COPY --from=build /target/blog-application-0.0.1-SNAPSHOT.jar blog-application.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","blog-application.jar"]
