FROM maven:3.9.16-eclipse-temurin-25 AS build

WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests


FROM eclipse-temurin:25

WORKDIR /app

COPY --from=build /build/target/app-1.0-SNAPSHOT.jar app.jar

RUN mkdir -p /app/logs

CMD ["java", "-jar", "app.jar"]