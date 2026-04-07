# Event-Driven Blood & Organ Donation Management System

A Spring Boot MVC application for managing blood and organ donations using design patterns and GRASP principles.

---

## 👥 Team Modules

### 🔹 Nandan (SRN 363) — Admin & Donation

* Entities: Admin, Donation
* Pattern: **Singleton (EventManager)** — manages system-wide events
* GRASP: **Controller**
* Routes:

  * `/login`, `/register`
  * `/admin/dashboard`
  * `/admin/users`
  * `/admin/donations`
  * `/admin/reports`

---

### 🔹 Sharath (SRN 823) — User & Request

* Entities: User, Request
* Pattern: **Factory (RequestFactory)** — creates Blood/Organ requests
* GRASP: **Creator**
* Routes:

  * `/requests`
  * `/requests/new`
  * `POST /requests`

---

### 🔹 Neha (SRN 379) — Inventory

* Entity: Inventory
* Pattern: **Strategy Pattern**

  * `MatchingStrategy` (interface)
  * `HighPriorityMatchingStrategy` (urgent-based)
  * `NormalMatchingStrategy` (FIFO)
* GRASP: **Low Coupling**
* Routes:

  * `/inventory/dashboard`
  * `/inventory/stock`
  * `/inventory/matching`

---

## 🧩 Design Patterns Used

| Pattern   | Module    | Purpose                 |
| --------- | --------- | ----------------------- |
| Singleton | Admin     | Global event management |
| Factory   | Request   | Create request types    |
| Strategy  | Inventory | Flexible matching logic |

---

## ⚙️ Matching Strategy (Inventory Module)

Matching Strategy determines **how available inventory is allocated to requests**.

* **HighPriorityStrategy** → handles urgent cases first
* **NormalStrategy (FIFO)** → processes requests in order

This allows dynamic switching of allocation logic without modifying core code.

---

## 🏗️ Architecture

| Layer    | Technology                   |
| -------- | ---------------------------- |
| Backend  | Spring Boot 4.0.5 · Java 23  |
| Database | MySQL 8.0+ · JPA · Hibernate |
| Frontend | Thymeleaf                    |
| Build    | Maven                        |

---

## 🚀 Run

### Prerequisites

* JDK 21+
* MySQL 8+

### Database

```sql
CREATE DATABASE donationdb;
```

### Configure

Update:

```
src/main/resources/application.properties
```

### Start

```bash
./mvnw spring-boot:run
```

---

## 🌐 Access

* Admin → `/login`
* Requests → `/requests`
* Inventory → `/inventory/dashboard`

---

## 📊 Features

* Inventory management (add/view stock)
* Low stock detection
* Strategy-based allocation logic
* MySQL integration
* MVC architecture

---

## 📌 Notes

* Inventory module works independently with low coupling
* Data is persisted in MySQL
* Thymeleaf used for dynamic UI rendering

---

## 🛠️ Commands

| Task  | Command                  |
| ----- | ------------------------ |
| Run   | `./mvnw spring-boot:run` |
| Build | `./mvnw clean package`   |
| Test  | `./mvnw test`            |

---

## 📄 License

Academic use (OOAD coursework)
