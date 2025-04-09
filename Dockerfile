# 1. Java 21 기반 이미지 사용
FROM eclipse-temurin:21-jdk

# 2. 작업 디렉토리 생성
WORKDIR /app

# 3. 빌드된 JAR 복사 (Github Actions에서 빌드된 파일이 위치한 경로 기준)
COPY build/libs/*.jar app.jar

# 4. EC2에서 사용할 설정파일 복사 (선택 사항: Secrets로 넘겨줌 -> 생략 가능)
# COPY application-secret.yml ./application-secret.yml

# 5. JAR 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
