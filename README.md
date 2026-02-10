# Hexlet Spring Blog

[![CI](https://github.com/your-username/hexlet-spring-blog/actions/workflows/ci.yml/badge.svg)](https://github.com/your-username/hexlet-spring-blog/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/your-username/hexlet-spring-blog/branch/main/graph/badge.svg)](https://codecov.io/gh/your-username/hexlet-spring-blog)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=your-username_hexlet-spring-blog&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=your-username_hexlet-spring-blog)

## Описание

Учебный проект — блог на Spring Boot с полным CRUD для пользователей и постов.

## Функциональность

- ✅ REST API для пользователей (`/api/users`)
- ✅ REST API для постов (`/api/posts`)
- ✅ Пагинация и сортировка постов
- ✅ Валидация данных
- ✅ Обработка ошибок (404, 422, 500)
- ✅ Автоматическая генерация тестовых данных
- ✅ Интеграционные тесты
- ✅ CI/CD через GitHub Actions
- ✅ Покрытие кода (JaCoCo)
- ✅ Статический анализ (SonarCloud)

## Технологии

- Java 21
- Spring Boot 3.2.2
- Spring Data JPA
- H2 Database
- Lombok
- JUnit 5
- MockMvc
- Instancio
- Faker
- JaCoCo
- SonarCloud

## Запуск приложения
```bash
./gradlew bootRun
```

Приложение запустится на `http://localhost:8080`

## Запуск тестов
```bash
./gradlew test
```

## Генерация отчета покрытия
```bash
./gradlew jacocoTestReport
```

Отчет будет доступен в `build/reports/jacoco/test/html/index.html`

## API Endpoints

### Users

- `GET /api/users` - список всех пользователей
- `GET /api/users/{id}` - получить пользователя по ID
- `POST /api/users` - создать пользователя
- `PUT /api/users/{id}` - обновить пользователя
- `DELETE /api/users/{id}` - удалить пользователя

### Posts

- `GET /api/posts` - список опубликованных постов (с пагинацией)
- `GET /api/posts/{id}` - получить пост по ID
- `POST /api/posts` - создать пост
- `PUT /api/posts/{id}` - обновить пост
- `DELETE /api/posts/{id}` - удалить пост

## Примеры запросов

### Создание пользователя
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "birthday": "1990-01-01"
  }'
```

### Получение постов с пагинацией
```bash
curl "http://localhost:8080/api/posts?page=0&size=5&sort=createdAt,desc"
```

## Разработка

### Требования

- Java 21
- Gradle 8.x

### Локальная разработка

1. Клонируйте репозиторий
2. Запустите приложение: `./gradlew bootRun`
3. Откройте H2 Console: `http://localhost:8080/h2-console`
    - JDBC URL: `jdbc:h2:mem:testdb`
    - Username: `sa`
    - Password: (пустой)
