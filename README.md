# Event-Driven Blood and Organ Donation Management System

Spring Boot MVC application for donation management with team-based modules.

---

## Active Modules

### Nandan (SRN 363) - Admin and Donation
- Entities: Admin, Donation
- Pattern: Singleton via EventManager (double-checked locking)
- GRASP: Controller (AdminController)
- Main routes:
  - /login
  - /register
  - /admin/dashboard
  - /admin/users
  - /admin/users/add
  - /admin/donations
  - /admin/donations/add
  - /admin/donations/update-status/{id}
  - /admin/reports

### Sharath (SRN 823) - User and Request
- Entities: User, Request
- Pattern: Factory via RequestFactory
- GRASP: Creator (User creates request through factory)
- Main routes:
  - /requests
  - /requests/new
  - POST /requests

---

## Current Scope Notes

- Donor, Patient, and Inventory modules are not integrated yet in this branch.
- Root route redirects to login via RootController.
- Request module is available independently at /requests.

---

## Architecture

| Layer | Technology |
|-------|------------|
| Backend | Spring Boot 4.0.5 · Java 23 |
| Database | MySQL 8.0+ · Spring Data JPA · Hibernate |
| Frontend | Thymeleaf |
| Build | Maven wrapper |

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

Set MySQL credentials in src/main/resources/application.properties.

### Start

```bash
./mvnw spring-boot:run
```

Then open:
- /login for admin flow
- /requests for user/request flow

---

## Useful Commands

| Task | Command |
|------|---------|
| Build | ./mvnw clean package |
| Test | ./mvnw test |
| Run | ./mvnw spring-boot:run |

---

For academic use (OOAD coursework).
