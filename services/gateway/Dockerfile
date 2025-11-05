FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY . /workspace
RUN mvn -q -e -DskipTests package

FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
COPY --from=build /workspace/target/gateway-service-0.0.1-SNAPSHOT.jar /app/app.jar
ENV JAVA_OPTS=""
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

