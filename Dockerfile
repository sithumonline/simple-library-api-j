FROM gradle:jdk17 AS builder

WORKDIR /home/gradle/project

COPY --chown=gradle:gradle build.gradle settings.gradle ./
COPY --chown=gradle:gradle src ./src

RUN gradle clean bootJar --no-daemon build

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
