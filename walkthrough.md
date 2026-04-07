# Walkthrough - Sharath (SRN 823)

## Module
User + Request

## Design Mapping
- Pattern: Factory Pattern
- Factory class: `RequestFactory`
- GRASP: Creator
- Creator class: `User`

## Implemented Files
- `src/main/java/com/donation/system/model/entity/User.java`
- `src/main/java/com/donation/system/model/entity/Request.java`
- `src/main/java/com/donation/system/service/factory/RequestFactory.java`
- `src/main/java/com/donation/system/repository/UserRepository.java`
- `src/main/java/com/donation/system/repository/RequestRepository.java`
- `src/main/java/com/donation/system/controller/RequestController.java`
- `src/main/resources/templates/index.html`
- `src/main/resources/templates/requests/new.html`
- `src/main/resources/templates/requests/index.html`
- `src/main/resources/static/css/app.css`

## Endpoints
- `GET /requests` : list all requests
- `GET /requests/new` : open create form
- `POST /requests` : submit a new request

## Flow
1. User opens `/requests/new`.
2. User enters name, email, request type, and details.
3. `RequestController` receives form data.
4. Existing user is fetched by email, or a new user is created.
5. `User` creates request through `RequestFactory`.
6. Request is saved using `RequestRepository`.
7. User is redirected to `/requests` to view entries.

## Run
1. Start app: `./mvnw spring-boot:run`
2. Open browser: `http://localhost:8080`
3. Open module: `http://localhost:8080/requests/new`
