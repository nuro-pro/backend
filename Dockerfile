FROM openjdk:17-jdk-slim
WORKDIR /app
# 빌드된 jar 파일을 컨테이너 내부로 복사
COPY build/libs/*-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]