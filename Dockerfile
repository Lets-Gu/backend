FROM openjdk:21-jdk-slim

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
COPY module module

RUN chmod +x gradlew
RUN ./gradlew bootJar --no-daemon

EXPOSE 8080

RUN mv build/libs/*.jar app.jar

CMD ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -Dspring.config.location=$CONFIG_LOCATION -jar app.jar"]