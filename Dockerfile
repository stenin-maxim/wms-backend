FROM eclipse-temurin:25-jdk-jammy

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /var/www/wms/backend/api

# Открываем порт бэкенда для сетевых запросов
EXPOSE 5000

# Запускаем Spring Boot напрямую через главный класс WmsApplication, 
# указывая в качестве Сlаsspаth папку target/classes и скачанные зависимости
ENTRYPOINT ["java", "-cp", "target/classes:target/dependency/*", "ru.wms.WmsApplication"]