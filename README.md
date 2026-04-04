# Event-Driven Blood & Organ Donation Management System

Nandan-only Spring Boot MVC application for admin and donation management.

---

## Scope

This repository now keeps only the Nandan module:

- `AdminController` as the GRASP Controller
- `AdminService` and `DonationService` as the business logic layer
- `Admin` and `Donation` entities
- `AdminRepository` and `DonationRepository`
- `EventManager` as the Singleton event log (double-checked locking)
- Thymeleaf views under `templates/admin/` and `templates/donations/`

Removed from the runtime surface:

- `HomeController`
- `index.html`
- Separate donation controller entry points

---

## Architecture

| Layer | Technology |
|-------|------------|
| Backend | Spring Boot 4.0.5 · Java 23 |
| Database | MySQL 8.0+ · Spring Data JPA · Hibernate |
| Frontend | Thymeleaf · Bootstrap 5 |
| Build | Maven wrapper |

---

## GRASP and Pattern

| Concern | Implementation |
|---------|----------------|
| GRASP Controller | `AdminController` handles HTTP requests and delegates to services |
| Singleton Pattern | `EventManager` uses double-checked locking and logs donation events |
| Repository | `AdminRepository`, `DonationRepository` |

---

## Main Routes

- `/admin/dashboard`
- `/admin/users`
- `/admin/users/add`
- `/admin/donations`
- `/admin/donations/add`
- `/admin/donations/update-status/{id}`
- `/admin/reports`

---

## Run

### Prerequisites

- JDK 21+
- MySQL 8.0+

### Database

```sql
CREATE DATABASE donationdb;
```

### Configure

Set your MySQL password in `src/main/resources/application.properties`.

### Start

```bash
./mvnw spring-boot:run
```

Open `/admin/dashboard` in the browser.

---

## Useful Commands

| Task | Command |
|------|---------|
| Build | `./mvnw clean package` |
| Test | `./mvnw test` |
| Run | `./mvnw spring-boot:run` |

---

## Notes

The project documentation and runtime scope now reflect only Nandan's part.
