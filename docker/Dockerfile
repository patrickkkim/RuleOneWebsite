FROM maven:3.9.8-amazoncorretto-17 AS build

WORKDIR /usr/src/app

COPY ../pom.xml .
COPY ../src ./src
COPY ./docker .
RUN mvn package -Dmaven.test.skip=true

FROM amazoncorretto:17-alpine3.19

WORKDIR /usr/src/app

COPY --from=build /usr/src/app/target/*.jar app.jar
COPY ./docker .
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]