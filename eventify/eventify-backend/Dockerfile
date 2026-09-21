

FROM gradle:8.5-jdk21-alpine AS builder
WORKDIR /app

# Копируем вообще все файлы проекта сразу
COPY . .

# Собираем JAR, пропуская тесты и отключая поиск сторонних toolchain
RUN ./gradlew bootJar --no-daemon -x test -Porg.gradle.java.installations.auto-download=false

# --- Этап 2: Финальный легковесный образ ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S eventify && adduser -S eventify -G eventify
USER eventify:eventify

# Копируем собранный JAR-файл
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT java -XX:+UseG1GC -jar app.jar