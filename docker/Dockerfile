# OpenJDK 17 경량화 버전 사용
FROM openjdk:17-jdk-slim

# Gradle을 사용해 빌드를 실행하는 명령어
CMD ["./gradlew", "clean", "build"]

# 작업 디렉토리 설정
WORKDIR /apps

# 애플리케이션 jar 파일을 컨테이너로 복사
COPY build/libs/ezpz-admin-0.0.1-SNAPSHOT.jar /apps/app.jar

# 애플리케이션이 사용할 포트 노출
EXPOSE 8081

# 애플리케이션을 실행하기 위한 엔트리포인트 정의
ENTRYPOINT ["java", "-jar", "/apps/app.jar"]
