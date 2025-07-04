# UserProfileAPI REST API (Spring Boot)

REST API для управления пользовательскими профилями с возможностью аутентификации, хранения контактных данных 
и фотографии пользователя.


### 1. Аутентификация 
Реализована модель работы с аккаунтами 
- **создание аккаунта**
- **авторизация по логину и паролю**
- **получение токена**



[Спецификация API auth-service](./swagger/auth_service_api.json)

### 2. Пользовательский сервис

Реализована модель работы с пользователями
- **Создание, удаление профиля пользователя**
- **Редактирование личной, контактной информации**
- **Создание, удаление, получение фотографии пользователя**

#### Особенности работы
- Требование для user-service: Для всех защищённых API user-service необходимо передавать 
валидный JWT токен в заголовке запроса. Токен проверяется сервисом для идентификации пользователя
и получения accountId, что позволяет выполнять операции с профилем, привязанным к данному аккаунту

[Спецификация API user-service](./swagger/user_service_api.json)

### 3. Аутентификация и авторизация

Используемый тип: JWT (JSON Web Tokens)

- Безопасность: JWT обеспечивает безопасный способ передачи информации между клиентом и сервером без необходимости хранить сессию на сервере
- Отсутствие постоянной передачи credentials: После первичной аутентификации логин и пароль не передаются при каждом запросе, что снижает риск компрометации
- Стандарт и совместимость: JWT — широко признанный и поддерживаемый стандарт для современных REST API
- Масштабируемость: Благодаря stateless архитектуре, JWT отлично подходит для распределённых систем и микросервисов
- Гибкость: Позволяет хранить в токене полезные данные (например, идентификатор аккаунта), что упрощает аутентификацию и авторизацию на сервисах


## Технологический стек

- **Язык:** Java 21
- **Фреймворк:** Spring Boot 3.4.5
- **База данных:** PostgreSQL
- **ORM:** Hibernate
- **API Gateway:** Spring Cloud Gateway
- **Обнаружение сервисов:** Eureka Service Discovery
- **HTTP-клиент для межсервисного взаимодействия:** Spring Cloud OpenFeign
- **Конфигурация:** Spring Cloud Config
- **Контейнеризация:** Docker + Docker Compose
- **Аутентификация:** Spring Security + JWT
- **Тестирование:** JUnit 5, Mockito, Postman
- **Сериализация JSON:** Jackson Databind
- **Маппинг DTO:** MapStruct
- **Сборка:** Maven

## Системные компоненты

### Бизнес-микросервисы:
- **Auth Service** - управление пользователями и аутентификацией
- **User Service** - управление пользователями и личными данными
- **Gateway Service** - единая точка входа для API

### Инфраструктурные сервисы:
- **Config Server** - централизованное хранение конфигураций
- **Eureka Server** - сервис обнаружения и регистрации микросервисов


Сервисы зарегистрированы в **Eureka Server** и используют:
- Централизованную конфигурацию
- Service discovery через Eureka
- Feign клиенты для межсервисного взаимодействия


   
## Docker развертывание

Собрать и запустить все сервисы:
```bash
docker compose build --no-cache && docker compose up
```

## Порядок запуска локально
  - Развернуть базы в docker (compose.yaml)
  - DiscoveryServer
  - ConfigServer
  - GatewayService

далее (после паузы)
   - AuthService
   - UserService

#### Особенности запуска

Для локального запуска из IDE после развертывания баз в docker в модуле config-server перед запуском следует 
раскомментировать строки в application.yaml: url: jdbc:postgresql://localhost:5435/auth-db для auth-service и
url: jdbc:postgresql://localhost:5436/user-db для user-service

### Для тестирования

В первую очередь производится регистрация в auth-service (логин, пароль), затем добавление данных в user_service

[Коллекция тестов для postman](./postman/UserProfilApi.postman_collection.json) представлена набором автотестов и 
тестов для самостоятельного тестирования (подстановка своих значений)

Все эндпоинты доступны по http://localhost:8080
