---
name: nandani-donor-patient
description: >
  Code generation skill for Nandani (SRN 364) — Donor & Patient module with Observer pattern.
    Covers Donor entity, Patient entity, DonationEvent publisher/listener flow, Spring ApplicationEvent,
  donor registration, donation actions, patient requests, and request status tracking.
---

# Nandani — Donor & Patient Module (SRN 364)

## Responsibilities
| Area | Details |
|------|---------|
| **Entities** | `Donor`, `Patient` |
| **Pattern** | Observer (`DonationEvent` publisher/listener) |
| **GRASP** | Information Expert (Donor knows its own availability) |
| **Use Cases** | Donate blood/organ, view donation status, request blood/organ, track request status |

---

## Entity: Donor (extends User)

```
Donor extends User
  - bloodType: String
  - organType: String
  - availability: boolean
  + donateBlood()
  + donateOrgan()
  + viewDonationStatus()
  + notifyObservers()  ← Subject role
```

**File:** `com/donation/system/model/entity/Donor.java`
- `@Entity`, `@DiscriminatorValue("Donor")`
- `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor` (Lombok)
- Implements `DonationSubject` interface

---

## Entity: Patient (extends User)

```
Patient extends User
  - requiredBloodType: String
  - requiredOrgan: String
  - urgencyLevel: String (CRITICAL / HIGH / NORMAL)
  + requestBlood()
  + requestOrgan()
  + trackRequestStatus()
  + onDonationAvailable()  ← Observer role
```

**File:** `com/donation/system/model/entity/Patient.java`
- `@Entity`, `@DiscriminatorValue("Patient")`
- `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor` (Lombok)
- Implements `DonationObserver` interface

---

## Observer Pattern Implementation

### Subject Interface
```java
public interface DonationSubject {
    void registerObserver(DonationObserver observer);
    void removeObserver(DonationObserver observer);
    void notifyObservers(Donation donation);
}
```

### Observer Interface
```java
public interface DonationObserver {
    void onDonationAvailable(Donation donation);
}
```

### Donor as Subject
```java
@Entity
@DiscriminatorValue("Donor")
public class Donor extends User implements DonationSubject {
    @OneToMany(mappedBy = "donor", cascade = CascadeType.ALL)
    private List<DonationObserver> observers = new ArrayList<>();

    @Override
    public void notifyObservers(Donation donation) {
        for (DonationObserver obs : observers) {
            obs.onDonationAvailable(donation);
        }
    }
}
```

### Patient as Observer (Spring ApplicationEvent way)
```java
// Donor publishes DonationEvent via Spring ApplicationEventPublisher
public class DonationEvent extends ApplicationEvent {
    private final Donation donation;
    public DonationEvent(Object source, Donation donation) {
        super(source);
        this.donation = donation;
    }
    public Donation getDonation() { return donation; }
}

// Patient listens to DonationEvent
@EventListener
public void handleDonationEvent(DonationEvent event) {
    // Check if donation matches patient need
}
```

**Files:**
- `com/donation/system/service/observer/DonationSubject.java`
- `com/donation/system/service/observer/DonationObserver.java`
- `com/donation/system/service/observer/DonationEvent.java`

---

## MVC Structure

```
com.donation.system/
├── model/entity/
│   ├── Donor.java
│   └── Patient.java
├── repository/
│   ├── DonorRepository.java
│   └── PatientRepository.java
├── service/
│   ├── observer/
│   │   ├── DonationSubject.java
│   │   ├── DonationObserver.java
│   │   └── DonationEvent.java
│   ├── DonorService.java
│   └── PatientService.java
├── controller/
│   ├── DonorController.java
│   └── PatientController.java
└── resources/templates/
    ├── donor/
    │   ├── dashboard.html
    │   ├── donate.html
    │   └── status.html
    └── patient/
        ├── dashboard.html
        ├── request.html
        └── track.html
```

---

## Code Generation Rules (Nandani)
1. `Donor` and `Patient` use `@DiscriminatorValue` — single table inheritance from `User`
2. `Donor` implements `DonationSubject` — holds list of observers
3. `Patient` implements `DonationObserver` — receives notifications
4. Use Spring `@EventListener` for real-time event handling
5. Thymeleaf templates use Bootstrap 5 CDN
6. Service layer uses `Optional<T>` for null safety
7. Use `jakarta.*` imports (NOT `javax.*`)
8. Comment WHERE Observer pattern is applied in service classes
9. **GRASP (Information Expert) MUST be implemented and documented:**
   - `DonorService` and `PatientService` MUST have Javadoc: `/** GRASP: Information Expert */`
    - `DonorService` owns availability logic (`isAvailable`, eligibility checks, donation readiness)
   - Services hold ALL business logic — controllers only delegate
   - Each service class has the data + methods it needs for its own operations
   - Add `@author Nandani (SRN 364)` on every class

---

## Donor Controller Endpoints
| Method | Path | Description |
|--------|------|-------------|
| GET | `/donor/dashboard` | Donor dashboard |
| POST | `/donor/donate` | Record a donation (blood/organ) |
| GET | `/donor/status` | View donation history/status |
| POST | `/donor/register` | Register as donor |

## Patient Controller Endpoints
| Method | Path | Description |
|--------|------|-------------|
| GET | `/patient/dashboard` | Patient dashboard |
| POST | `/patient/request` | Request blood/organ |
| GET | `/patient/track` | Track request status |
| POST | `/patient/register` | Register as patient |

---

## MySQL (reference — auto-generated by JPA)
```sql
-- Donor & Patient inherit from users table (dtype = 'Donor' / 'Patient')
-- Fields in users table:
-- blood_type, organ_type, availability (Donor fields)
-- required_blood_type, required_organ, urgency_level (Patient fields)
```

---

## Navigation
- Project skill → see `.agent-skills/about-this-proejct/SKILL.md`
- Other members → `nandan-admin-donation.md`, `sharath-user-request.md`, `neha-inventory.md`
