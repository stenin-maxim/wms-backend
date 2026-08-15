# Stage 1: Сборка приложения (используем полный JDK 25)
FROM eclipse-temurin:25-jdk-jammy AS builder
WORKDIR /build
# Копируем исходный код Maven проекта
COPY . .
# Собираем fat-jar файл приложения без запуска тестов
RUN ./mvnw clean package -DskipTests

# Stage 2: Финальный легковесный образ для запуска
FROM eclipse-temurin:25-jre-jammy
WORKDIR /app
# Копируем скомпилированный jar файл из предыдущего шага
COPY --from=builder /build/target/*.jar app.jar
# Открываем порт для фронтенда
EXPOSE 5000
# Запуск WMS бэкенда с оптимизацией под виртуальные потоки (Virtual Threads)
ENTRYPOINT ["java", "-jar", "app.jar"]