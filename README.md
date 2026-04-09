## please refer sub branches for indivdual CODES.

| Branches |
|-------|
| **823/Creator + requests(Shrath)** |
| **Sharath/823** |
| **Feature/Controller** |
| **nandini-donor-patient** |
| **379/Inventory(Neha)** |


---

# Event-Driven Blood & Organ Donation Management System

A Spring Boot MVC web application for managing blood and organ donations, built with Java 17+, MySQL, and Thymeleaf.

---

## 🏗️ Architecture

| Layer | Technology |
|-------|------------|
| **Backend** | Spring Boot 3.2.5 · Java 17+ |
| **Database** | MySQL 8.0+ · Spring Data JPA · Hibernate |
| **Frontend** | Thymeleaf · Bootstrap 5 |
| **Build** | Maven (wrapper included) |

---

## 👥 Team

| Member | SRN | Module | Design Pattern | GRASP |
|--------|-----|--------|----------------|-------|
| **Nandan** | 363 | Admin, Donation | Singleton (EventManager) | Controller |
| **Nandani** | 364 | Donor, Patient | Observer (DonationEvent) | Information Expert |
| **Sharath** | 823 | User, Request | Factory (RequestFactory) | Creator |
| **Neha** | 379 | Inventory | Strategy (Matching) | Low Coupling |

---

## 🧩 Design Patterns

| Pattern | Implementation |
|---------|---------------|
| **Singleton** | `EventManager` — manages donation events globally |
| **Observer** | `Donor` notifies, `Patient` receives donation alerts |
| **Factory** | `RequestFactory` creates Blood/Organ requests |
| **Strategy** | `MatchingStrategy` — priority-based vs FIFO matching |

---

## 🚀 Quick Start

### Prerequisites
- JDK 17+
- MySQL 8.0+
- Git

### 1. Clone & Setup
```bash
git clone https://github.com/Nandan-D14/donation-management-system.git
cd donation-management-system
```

### 2. Database Setup
```sql
CREATE DATABASE donationdb;
```

### 3. Configure
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### 4. Run
```bash
./mvnw spring-boot:run
```

### 5. Access
Open `http://localhost:8080` in your browser.

### 6. If You Migrated Old Tables
If your MySQL schema had old columns (for example `userid` and `donationid`), run:

`sql/mysql_schema_repair.sql`

This script:
- Renames old tables to `_legacy`
- Creates clean tables matching current JPA entities
- Migrates compatible rows

After validating data, you can remove legacy tables by running:

`sql/mysql_drop_legacy_tables.sql`

To insert sample rows for quick UI checks, run:

`sql/mysql_seed_dummy_data.sql`

### 7. Important Runtime Note
Do not run with the dev profile when testing MySQL persistence.

- Use MySQL run: `./mvnw spring-boot:run`
- Avoid H2 dev run for DB persistence checks: `SPRING_PROFILES_ACTIVE=dev`

---

## 📁 Project Structure

```
src/main/java/com/donation/system/
├── model/
│   ├── entity/       → JPA entities (User, Donor, Patient, Admin, Donation, Request, Inventory)
│   ├── dto/          → Data Transfer Objects
│   └── enums/        → BloodType, RequestStatus, DonationType
├── repository/       → Spring Data JPA interfaces
├── service/
│   ├── factory/      → RequestFactory
│   ├── observer/     → DonationSubject, DonationObserver
│   ├── singleton/    → EventManager
│   └── strategy/     → MatchingStrategy, HighPriorityStrategy, NormalStrategy
├── controller/       → @Controller classes
└── resources/
    ├── templates/    → Thymeleaf HTML views
    └── application.properties
```

---

## 🌿 Git Workflow

| Branch | Purpose |
|--------|---------|
| `main` | Stable, production-ready code |
| `feature/singleton` | Nandan — Admin, Donation, EventManager |
| `feature/observer` | Nandani — Donor, Patient, Observer |
| `feature/factory` | Sharath — User, Request, Factory |
| `feature/strategy` | Neha — Inventory, Strategy |

### Creating a feature branch
```bash
git checkout -b feature/<member-name>
git push -u origin feature/<member-name>
```

### Merging
1. Push your feature branch
2. Create a Pull Request on GitHub
3. Code review → Merge to `main`

---

## 🛠️ Useful Commands

| Task | Command |
|------|---------|
| Build | `./mvnw clean package` |
| Run | `./mvnw spring-boot:run` |
| Test | `./mvnw test` |
| Skip tests | `./mvnw spring-boot:run -DskipTests` |

---

## ✅ Login Testing (Role-Based)

You can test login and dashboard routing with seeded accounts:

1. Admin login
    - Email: `admin.one@test.com`
    - Password: `admin123`
    - Expected: redirect to `/admin/dashboard`

2. Donor login
    - Email: `donor.blood@test.com`
    - Password: `donor123`
    - Expected: redirect to `/donor/dashboard`

3. Patient login
    - Email: `patient.one@test.com`
    - Password: `patient123`
    - Expected: redirect to `/patient/dashboard`

Quick DB verification queries:

- `SELECT COUNT(*) FROM users;`
- `SELECT COUNT(*) FROM donations;`
- `SELECT COUNT(*) FROM requests;`
- `SELECT COUNT(*) FROM inventory;`

---
This project is for academic purposes (OOAD Coursework).
