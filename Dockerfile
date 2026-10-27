FROM maven:3.9-eclipse-temurin-26 AS build
WORKDIR /app
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd -r -u 1001 appuser && mkdir -p /app/logs && chown -R appuser /app
COPY --from=build --chown=appuser /app/target/*.jar app.jar
USER appuser
EXPOSE 9095
ENTRYPOINT ["java","-jar","app.jar"]