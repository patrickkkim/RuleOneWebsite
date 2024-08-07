FROM maven:3.9.8-amazoncorretto-17 AS build

WORKDIR /usr/src/app
ARG SKIP_TEST
ARG FS_API_KEY

COPY ./pom.xml .
COPY ./src ./src
RUN if ["$SKIPTEST" = "false"]; then \
    mvn package -Dmaven.test.skip=true -Dfs.api.key=${FS_API_KEY}; \
else \
    mvn package -Dmaven.test.skip=true; \
fi

FROM amazoncorretto:17-alpine3.19

WORKDIR /usr/src/app

COPY --from=build /usr/src/app/target/*.jar app.jar
EXPOSE 8080

RUN adduser --disabled-password --gecos "" appuser\
 && chown appuser .
USER appuser

ENTRYPOINT ["java", "-jar", "app.jar"]