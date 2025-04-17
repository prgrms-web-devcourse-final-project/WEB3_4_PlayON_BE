# 1. Java 21 기반 이미지 사용
FROM eclipse-temurin:21-jdk

# 2. 작업 디렉토리 생성
WORKDIR /app

# 3. 빌드된 JAR 복사
COPY build/libs/*.jar app.jar

# 4. JVM 힙 메모리 및 타임존 설정
ENV JAVA_OPTS="-Xms512m -Xmx1024m -Duser.timezone=Asia/Seoul"

# 5. JAR 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
