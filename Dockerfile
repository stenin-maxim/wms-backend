# Stage 1: Сборка приложения (используем полный JDK 25)
FROM eclipse-temurin:25-jdk-jammy AS builder
WORKDIR /build

# КЭШИРОВАНИЕ ЗАВИСИМОСТЕЙ: Копируем только файлы сборщика
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Запускаем команду скачивания зависимостей вхолостую. 
# Этот слой закешируется. Если код поменяется, этот шаг Docker просто пропустит!
RUN ./mvnw dependency:go-offline -B

# Теперь копируем сам код и собираем проект (этот шаг будет работать мгновенно без скачиваний)
COPY src/ src/
RUN ./mvnw package -DskipTests

# Stage 2: Финальный легковесный образ для запуска
FROM eclipse-temurin:25-jre-jammy
WORKDIR /app
# Копируем скомпилированный jar файл из предыдущего шага
COPY --from=builder /build/target/*.jar app.jar
COPY --from=builder /build/target/classes ./target/classes
# Открываем порт для фронтенда
EXPOSE 5000
# Стартовая точка для гибкого подхвата внешних классов из volumes
ENTRYPOINT ["java", "-cp", "app.jar", "-Dloader.path=target/classes", "org.springframework.boot.loader.launch.PropertiesLauncher"]