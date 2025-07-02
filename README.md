# UserProfileAPI REST API (Spring Boot)

REST API для управления пользовательскими профилями с возможностью аутентификации и хранения контактных данных.


### 1. Аутентификация 
Реализована создание аккаунта, авторизация по логину и паролю
**Выбранный тип:** JWT (JSON Web Tokens)  
**Обоснование выбора:**
- Более безопасный вариант по сравнению с Basic Auth
- Не требует постоянного обмена credentials
- Стандартный подход для современных REST API
- Легко масштабируется


[Спецификация API auth-service](./swagger/auth_service_api.json)

### 2. Пользовательский сервис

Реализована модель работы с пользователями
- **Создание, удаление профиля пользователя**
- **Редактирование личной, контактной информации**
- **Создание, удаление, получение фотографии пользователя**

[Спецификация API user-service](./swagger/user_service_api.json)

## Технологический стек

- **Язык:** Java 17
- **Фреймворк:** Spring Boot 3.x
- **База данных:** PostgreSQL
- **ORM:** Hibernate
- **Spring Cloud Gateway**
- **Eureka Service Discovery**
- **Feign Client**
- **Docker + Docker Compose**
- **Аутентификация:** Spring Security + JWT
- **Тестирование:** JUnit 5, Mockito
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

1. Собрать и запустить все сервисы:
```bash
docker compose build --no-cache && docker compose up
```

### Порядок запуска локально
  - Развернуть базы в docker (compose.yaml)
  - DiscoveryServer
  - ConfigServer
  - GatewayService

далее (после паузы)
   - AuthService
   - UserService

#### Особенности запуска

Для локального запуска из IDE после развертывания баз в docker в модуле config-server перед запуском следует 
раскомментировать строки в application.yaml: url: jdbc:postgresql://account-db:5432/account-db для account-service и
url: jdbc:postgresql://auth-db:5432/auth-db для auth-service

### Для тестирования

В первую очередь производится регистрация в auth-service (логин, пароль), затем добавление данных в account service

[Коллекция тестов для postman](./postman/UserProfilApi.postman_collection.json) представлена набором автотестов и 
тестов для самостоятельного тестирования (подстановка своих значений) 
