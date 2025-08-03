FROM eclipse-temurin:21-jre-alpine

# 시간대 설정 및 필요한 패키지 설치
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone

WORKDIR /app

# 로그 디렉토리 생성
RUN mkdir -p /app/logs

# JAR 파일 복사
ARG APP_JAR=build/libs/app.jar
COPY ${APP_JAR} app.jar

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행
# USE_PROFILE 환경변수를 통해 프로파일 설정 (기본값: local)
ENV USE_PROFILE=local
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=${USE_PROFILE}"]