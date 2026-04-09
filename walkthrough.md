# Donation Management System - Implementation Walkthrough

## 1) What this walkthrough covers
This document captures the full implementation state and the major work completed in this project, including:
- Endpoints currently implemented
- Entities and relationships used
- Service and repository flows
- Design patterns in use
- Templates and page routing
- Database scripts and runtime setup used
- Known gaps and next fixes

## 2) Project stack and runtime
- Backend: Spring Boot 3.2.5, Java 17+
- Persistence: Spring Data JPA + Hibernate
- Database: MySQL (database: donationdb)
- UI: Thymeleaf templates
- Build: Maven wrapper (mvnw / mvnw.cmd)

Active DB config in src/main/resources/application.properties:
- spring.datasource.url=jdbc:mysql://localhost:3306/donationdb
- spring.datasource.username=root
- spring.datasource.password=root123
- spring.jpa.hibernate.ddl-auto=update
- spring.devtools.restart.enabled=false

## 3) High-level architecture
- Controller layer: handles web routes, session checks, view rendering, form handling.
- Service layer: business rules for auth-adjacent flows, donation, request, inventory, admin reporting.
- Repository layer: Spring Data JPA interfaces for data access.
- Model/entity layer: JPA entities and inheritance for User and Request hierarchies.
- View layer: Thymeleaf pages under src/main/resources/templates.

## 4) Entities used

### 4.1 User hierarchy (single table inheritance)
Base entity:
- User (table: users)
  - id, name, mail, password, phone (Long), role

Subtypes:
- Admin (dtype: ADMIN)
- Donor (dtype: DONOR)
  - bloodType, organType, availability, status
- Patient (dtype: PATIENT)
  - conditionNote, status

Key behavior in User:
- login(inputMail, inputPassword)
- register() default role fallback
- updateProfile(updatedName, updatedPhone)
- createBloodRequest(bloodGroup, quantity)
- createOrganRequest(organType, quantity)

### 4.2 Request hierarchy (single table inheritance)
Base entity:
- Request (table: requests)
  - id, requestType, quantity, status, createdAt
  - createdBy (ManyToOne to User)
  - abstract getRequestDetail()

Subtypes:
- BloodRequest (request_kind: BLOOD)
  - bloodGroup
- OrganRequest (request_kind: ORGAN)
  - organType

### 4.3 Donation
- Donation (table: donations)
  - id, donationType, bloodType, organType, date, quantity, status, allocatedStatus
  - donor (ManyToOne to Donor)

### 4.4 Inventory
- Inventory (table: inventory)
  - id, bloodType, organType, quantity
  - addStock(amount)
  - reduceStock(amount)
  - checkAvailability(amount)

## 5) Repository interfaces used
- UserRepository
  - findByMail(mail)
  - findByMailAndPassword(mail, password)
- AdminRepository
  - findByMail(mail)
- DonorRepository
  - findByMail(mail)
- PatientRepository
  - findByMail(mail)
- DonationRepository
  - findByStatus(status)
  - findByDonationType(donationType)
  - findByDonor(donor)
- InventoryRepository
  - findByBloodType(...), findByOrganType(...)
  - findByBloodTypeIgnoreCase(...), findByOrganTypeIgnoreCase(...)
- RequestRepository
  - findByStatusIgnoreCaseOrderByCreatedAtAsc(status)

## 6) Service layer used

### 6.1 Auth and role-driven access
Auth logic is controller-centric using UserRepository + HttpSession attributes:
- userMail
- userName
- role

### 6.2 AdminService
- getAllAdmins()
- getAdminByMail(mail)
- saveAdmin(admin) -> role forced to ADMIN
- authenticate(mail, password)

### 6.3 DonationService
- getAllDonations(), getDonationById(id)
- recordDonation(donation)
- updateStatus(id, status)
- getDonationCount(), getEventCount()

### 6.4 DonorService
- saveDonor(donor)
- recordDonation(donor, donationType, bloodType, organType, quantity)
- getDonationsForDonor(donor)

### 6.5 RequestService
- getAllRequests()
- getPendingRequests()
- getRequestById(id)
- updateRequestStatus(id, status)
- createRequest(creatorRole, requestType, userName, userMail, detail, quantity)

### 6.6 InventoryService
- syncInventoryFromDonations() (reads SUBMITTED donations and updates inventory totals)
- addOrUpdateInventory(inventory)
- getAllInventory()
- addStock(bloodType, organType, amount)
- reduceStock(bloodType, organType, amount)
- switchMatchingStrategy(strategy)
- getMatchingStrategy()
- getRequestMatchingPreview() (NORMAL or HIGH_PRIORITY sorted by quantity desc)
- allocateDonation(donationId, requestId, quantity)
- canFulfill(request)

## 7) Design patterns currently implemented
- Singleton:
  - EventManager (service/singleton/EventManager.java)
  - Global in-memory event list for donation events.
- Factory:
  - RequestFactory (service/factory/RequestFactory.java)
  - Central creation of BloodRequest and OrganRequest.
- Strategy:
  - MatchingStrategy interface
  - BloodMatchingStrategy and OrganMatchingStrategy
  - Used by InventoryService.canFulfill and matching preview mode.
- Observer artifacts:
  - DonationEvent, DonationObserver, DonationSubject interfaces/classes exist.
  - Current main runtime path logs events via EventManager singleton.

## 8) Endpoint inventory (all current routes)

### 8.1 Home and aliases
- GET /
  - Controller: HomeController
  - View: index
- /admin-auth (legacy aliases in RootController)
  - GET /admin-auth
  - GET /admin-auth/
  - GET /admin-auth/login -> redirect /login
  - GET /admin-auth/register -> redirect /register

### 8.2 AuthController
- GET /login
  - View: auth/login
  - Optional params: error, registered
- GET /register
  - View: auth/register
- POST /register
  - Creates Admin/Donor/Patient by role input
- POST /login
  - Sets session attributes and role-based redirect:
    - DONOR -> /donor/dashboard
    - PATIENT -> /patient/dashboard
    - ADMIN -> /admin/dashboard
- GET /request
  - If logged in -> redirect /requests/create
- POST /logout
  - Invalidates session and redirects /

### 8.3 AdminController (ADMIN-only)
- GET /admin/dashboard
- GET /admin/users
- GET /admin/users/add
- POST /admin/users/add
- GET /admin/donations/add
- POST /admin/donations/add
- POST /admin/donations/update-status/{id}
- GET /admin/donations
- GET /admin/reports
- GET /admin/requests
- POST /admin/requests/{id}/approve
- POST /admin/requests/{id}/deny

### 8.4 DonorController (DONOR-only)
- GET /donor/dashboard
- GET /donor/donate
- POST /donor/donate
  - Params: donationType, bloodType?, organType?, quantity
- GET /donor/status

### 8.5 PatientController (PATIENT-only)
- GET /patient/dashboard
- GET /patient/request
- POST /patient/request
  - Params: requestType, detail, quantity
- GET /patient/track

### 8.6 RequestController (patient submit / admin approval)
- GET /requests -> redirect /requests/list
- GET /requests/list
  - Access: ADMIN-only (non-admins are redirected to /patient/track)
- GET /requests/create
- GET /requests/add
  - Access: PATIENT-only (redirects to /patient/request)
- POST /requests/create
- POST /requests/add
  - Access: PATIENT-only
  - Params: requestType, detail, quantity
- GET /requests/{id}
  - Access: ADMIN-only
  - View: requests/status
- POST /requests/{id}/status
  - Access: ADMIN-only
  - Params: status

### 8.7 InventoryController (ADMIN-only)
- GET /inventory
- GET /inventory/dashboard
- GET /inventory/stock
- POST /inventory/stock/add
  - Params: bloodType?, organType?, amount
- POST /inventory/stock/reduce
  - Params: bloodType?, organType?, amount
- GET /inventory/matching
- POST /inventory/matching/strategy
  - Params: strategy

## 9) Main page/template mapping used
- index -> templates/index.html
- auth/login -> templates/auth/login.html
- auth/register -> templates/auth/register.html
- admin/dashboard -> templates/admin/dashboard.html
- admin/list -> templates/admin/list.html
- admin/add -> templates/admin/add.html
- admin/reports -> templates/admin/reports.html
- donations/add -> templates/donations/add.html
- donations/list -> templates/donations/list.html
- donor/dashboard -> templates/donor/dashboard.html
- donor/donate -> templates/donor/donate.html
- donor/status -> templates/donor/status.html
- patient/dashboard -> templates/patient/dashboard.html
- patient/request -> templates/patient/request.html
- patient/track -> templates/patient/track.html
- requests/add -> templates/requests/add.html
- requests/list -> templates/requests/list.html
- requests/status -> templates/requests/status.html
- inventory/dashboard -> templates/inventory/dashboard.html
- inventory/stock -> templates/inventory/stock.html
- inventory/matching -> templates/inventory/matching.html

Note: There are also legacy top-level templates login.html and register.html in templates root, while current AuthController uses auth/login and auth/register.

## 10) Data flow walkthroughs

### 10.1 Registration and login
1. POST /register validates unique mail and creates a subtype entity based on role.
2. POST /login loads user by mail+password.
3. Session is populated with userMail, userName, role.
4. User is redirected to role dashboard.

### 10.2 Donor donation to inventory sync
1. Donor submits POST /donor/donate.
2. DonorService records Donation with status=SUBMITTED, allocatedStatus=NOT_ALLOCATED.
3. InventoryService.getAllInventory() triggers syncInventoryFromDonations().
4. SUBMITTED donations are aggregated into inventory by blood/organ type.

### 10.3 Patient/request creation
1. Patient submits POST /patient/request or POST /requests/create.
2. RequestService validates type and quantity, resolves logged-in creator.
3. Creates BloodRequest or OrganRequest via User helper methods.
4. Saves request with status=PENDING and createdAt timestamp.
5. Patient tracking page shows only requests created by that logged-in patient.

### 10.4 Request status page and update
1. Admin opens /admin/requests and sees all patient requests.
2. Admin clicks Approve or Deny on a request.
3. The request status changes to APPROVED or DENIED.
4. Patient tracking shows the updated decision.

### 10.5 Inventory strategy preview
1. GET /inventory/matching loads pending requests.
2. strategy=NORMAL -> insertion/order by repository fetch.
3. strategy=HIGH_PRIORITY -> sorted by quantity descending.

## 11) Database migration and seed scripts used
Scripts in sql/:
- mysql_schema_repair.sql
  - Intended for repairing old/legacy schema and aligning to current JPA model.
- mysql_seed_dummy_data.sql
  - Inserts sample users/donations/requests/inventory for testing.
- mysql_drop_legacy_tables.sql
  - Drops users_legacy, donations_legacy, requests_legacy, inventory_legacy after validation.

## 12) Runtime and test commands used in this project
Common commands executed:
- mvnw clean compile
- mvnw spring-boot:run
- mvnw spring-boot:run with explicit datasource args for MySQL
- Process cleanup for port conflicts:
  - Get-Process java | Stop-Process -Force
  - Kill process bound to 8080 before rerun

## 13) Important implementation notes from recent work
- Added inventory contract routes:
  - /inventory/stock, /inventory/stock/add, /inventory/stock/reduce
  - /inventory/matching, /inventory/matching/strategy
- Added request detail and status routes:
  - GET /requests/{id}
  - POST /requests/{id}/status
- Added RequestRepository method for pending request ordering:
  - findByStatusIgnoreCaseOrderByCreatedAtAsc
- Added request status page template and inventory stock/matching templates.
- Changed phone binding/type to Long for registration compatibility.

## 14) Known gaps and cleanup candidates
- Admin request review page now shows all requests, but you may still want a dedicated patient badge color for APPROVED and DENIED.

## 15) Suggested next implementation step
1. Add endpoint-level integration tests with full Spring context for request status updates.
2. Add audit/event logging for request status transitions (who changed status and when).
3. Add pagination/filtering on request list and status history for larger datasets.

## 16) Java Code by Person
This section shows the direct Java source code grouped by module owner. Tests are intentionally omitted.

### 16.1 Nandan - Admin and Donation
```java
package com.donation.system.controller;

import com.donation.system.model.entity.Admin;
import com.donation.system.model.entity.Donation;
import com.donation.system.service.AdminService;
import com.donation.system.service.DonationService;
import com.donation.system.service.RequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminController {

  private final AdminService adminService;
  private final DonationService donationService;
  private final RequestService requestService;

  public AdminController(AdminService adminService, DonationService donationService, RequestService requestService) {
    this.adminService = adminService;
    this.donationService = donationService;
    this.requestService = requestService;
  }

  @GetMapping("/dashboard")
  public String dashboard(HttpSession session, Model model) {
    if (!"ADMIN".equalsIgnoreCase((String) session.getAttribute("role"))) {
      return "redirect:/login";
    }
    model.addAttribute("message", "Welcome to Admin Dashboard");
    model.addAttribute("donationCount", donationService.getAllDonations().size());
    model.addAttribute("adminCount", adminService.getAllAdmins().size());
    model.addAttribute("eventCount", donationService.getEventCount());
    return "admin/dashboard";
  }

  @GetMapping("/users")
  public String listUsers(HttpSession session, Model model) {
    if (!"ADMIN".equalsIgnoreCase((String) session.getAttribute("role"))) {
      return "redirect:/login";
    }
    model.addAttribute("admins", adminService.getAllAdmins());
    return "admin/list";
  }

  @GetMapping("/users/add")
  public String addUserForm(HttpSession session, Model model) {
    if (!"ADMIN".equalsIgnoreCase((String) session.getAttribute("role"))) {
      return "redirect:/login";
    }
    model.addAttribute("admin", new Admin());
    return "admin/add";
  }

  @PostMapping("/users/add")
  public String addUser(HttpSession session, @ModelAttribute Admin admin) {
    if (!"ADMIN".equalsIgnoreCase((String) session.getAttribute("role"))) {
      return "redirect:/login";
    }
    adminService.saveAdmin(admin);
    return "redirect:/admin/users";
  }

  @GetMapping("/donations/add")
  public String recordDonationForm(HttpSession session, Model model) {
    if (!"ADMIN".equalsIgnoreCase((String) session.getAttribute("role"))) {
      return "redirect:/login";
    }
    model.addAttribute("donation", new Donation());
    return "donations/add";
  }

  @PostMapping("/donations/add")
  public String recordDonation(HttpSession session, @ModelAttribute Donation donation) {
    if (!"ADMIN".equalsIgnoreCase((String) session.getAttribute("role"))) {
      return "redirect:/login";
    }
    donationService.recordDonation(donation);
    return "redirect:/admin/donations";
  }

  @PostMapping("/donations/update-status/{id}")
  public String updateDonationStatus(HttpSession session,
                     @PathVariable int id,
                     @RequestParam String status) {
    if (!"ADMIN".equalsIgnoreCase((String) session.getAttribute("role"))) {
      return "redirect:/login";
    }
    donationService.updateStatus(id, status);
    return "redirect:/admin/donations";
  }

  @GetMapping("/donations")
  public String listDonations(HttpSession session, Model model) {
    if (!"ADMIN".equalsIgnoreCase((String) session.getAttribute("role"))) {
      return "redirect:/login";
    }
    model.addAttribute("donations", donationService.getAllDonations());
    return "donations/list";
  }

  @GetMapping("/reports")
  public String generateReports(HttpSession session, Model model) {
    if (!"ADMIN".equalsIgnoreCase((String) session.getAttribute("role"))) {
      return "redirect:/login";
    }
    model.addAttribute("donations", donationService.getAllDonations());
    model.addAttribute("donationCount", donationService.getDonationCount());
    model.addAttribute("eventCount", donationService.getEventCount());
    return "admin/reports";
  }

  @GetMapping("/requests")
  public String reviewRequests(HttpSession session, Model model) {
    if (!"ADMIN".equalsIgnoreCase((String) session.getAttribute("role"))) {
      return "redirect:/login";
    }
    model.addAttribute("requests", requestService.getAllRequests());
    return "admin/requests";
  }

  @PostMapping("/requests/{id}/approve")
  public String approveRequest(HttpSession session, @PathVariable int id) {
    if (!"ADMIN".equalsIgnoreCase((String) session.getAttribute("role"))) {
      return "redirect:/login";
    }
    requestService.updateRequestStatus(id, "APPROVED");
    return "redirect:/admin/requests";
  }

  @PostMapping("/requests/{id}/deny")
  public String denyRequest(HttpSession session, @PathVariable int id) {
    if (!"ADMIN".equalsIgnoreCase((String) session.getAttribute("role"))) {
      return "redirect:/login";
    }
    requestService.updateRequestStatus(id, "DENIED");
    return "redirect:/admin/requests";
  }
}

package com.donation.system.service;

import com.donation.system.model.entity.Donation;
import com.donation.system.repository.DonationRepository;
import com.donation.system.service.singleton.EventManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DonationService {

  private final DonationRepository donationRepository;
  private final EventManager eventManager;

  public DonationService(DonationRepository donationRepository) {
    this.donationRepository = donationRepository;
    this.eventManager = EventManager.getInstance();
  }

  public List<Donation> getAllDonations() {
    return donationRepository.findAll();
  }

  public Optional<Donation> getDonationById(int id) {
    return donationRepository.findById(id);
  }

  public Donation recordDonation(Donation donation) {
    Donation savedDonation = donationRepository.save(donation);
    eventManager.logEvent(savedDonation);
    return savedDonation;
  }

  public Donation updateStatus(int id, String status) {
    return donationRepository.findById(id)
        .map(donation -> {
          donation.updateStatus(status);
          Donation updated = donationRepository.save(donation);
          eventManager.logEvent(updated);
          return updated;
        })
        .orElse(null);
  }

  public long getDonationCount() {
    return donationRepository.count();
  }

  public int getEventCount() {
    return eventManager.getEventCount();
  }
}

package com.donation.system.service.singleton;

import com.donation.system.model.entity.Donation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventManager {
  private static volatile EventManager instance;
  private final List<Donation> events;

  private EventManager() {
    this.events = Collections.synchronizedList(new ArrayList<>());
  }

  public static EventManager getInstance() {
    if (instance == null) {
      synchronized (EventManager.class) {
        if (instance == null) {
          instance = new EventManager();
        }
      }
    }
    return instance;
  }

  public void logEvent(Donation donation) {
    events.add(donation);
  }

  public List<Donation> getEvents() {
    synchronized (events) {
      return List.copyOf(events);
    }
  }

  public int getEventCount() {
    return events.size();
  }
}
```

### 16.2 Nandani - Donor and Patient
```java
package com.donation.system.controller;

import com.donation.system.model.entity.Donor;
import com.donation.system.repository.DonorRepository;
import com.donation.system.service.DonorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/donor")
public class DonorController {

  private final DonorService donorService;
  private final DonorRepository donorRepository;

  public DonorController(DonorService donorService, DonorRepository donorRepository) {
    this.donorService = donorService;
    this.donorRepository = donorRepository;
  }

  @GetMapping("/dashboard")
  public String dashboard(HttpSession session, Model model) {
    if (!"DONOR".equalsIgnoreCase((String) session.getAttribute("role"))) {
      return "redirect:/login";
    }
    model.addAttribute("userName", session.getAttribute("userName"));
    return "donor/dashboard";
  }

  @GetMapping("/donate")
  public String donatePage(HttpSession session) {
    if (!"DONOR".equalsIgnoreCase((String) session.getAttribute("role"))) {
      return "redirect:/login";
    }
    return "donor/donate";
  }

  @PostMapping("/donate")
  public String donate(HttpSession session,
             @RequestParam String donationType,
             @RequestParam(required = false) String bloodType,
             @RequestParam(required = false) String organType,
             @RequestParam int quantity,
             Model model) {
    if (!"DONOR".equalsIgnoreCase((String) session.getAttribute("role"))) {
      return "redirect:/login";
    }

    String mail = (String) session.getAttribute("userMail");
    Optional<Donor> donorOptional = donorRepository.findByMail(mail);
    if (donorOptional.isEmpty()) {
      model.addAttribute("error", "Donor account not found.");
      return "donor/donate";
    }

    donorService.recordDonation(donorOptional.get(), donationType, bloodType, organType, quantity);
    return "redirect:/donor/status";
  }

  @GetMapping("/status")
  public String status(HttpSession session, Model model) {
    if (!"DONOR".equalsIgnoreCase((String) session.getAttribute("role"))) {
      return "redirect:/login";
    }

    String mail = (String) session.getAttribute("userMail");
    Optional<Donor> donorOptional = donorRepository.findByMail(mail);
    if (donorOptional.isEmpty()) {
      return "redirect:/login";
    }

    Donor donor = donorOptional.get();
    model.addAttribute("donations", donorService.getDonationsForDonor(donor));
    return "donor/status";
  }
}

package com.donation.system.controller;

import com.donation.system.repository.PatientRepository;
import com.donation.system.service.RequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/patient")
public class PatientController {

  private final PatientRepository patientRepository;
  private final RequestService requestService;

  public PatientController(PatientRepository patientRepository, RequestService requestService) {
    this.patientRepository = patientRepository;
    this.requestService = requestService;
  }

  @GetMapping("/dashboard")
  public String dashboard(HttpSession session, Model model) {
    if (!"PATIENT".equalsIgnoreCase((String) session.getAttribute("role"))) {
      return "redirect:/login";
    }
    model.addAttribute("userName", session.getAttribute("userName"));
    return "patient/dashboard";
  }

  @GetMapping("/request")
  public String requestPage(HttpSession session) {
    if (!"PATIENT".equalsIgnoreCase((String) session.getAttribute("role"))) {
      return "redirect:/login";
    }
    return "patient/request";
  }

  @PostMapping("/request")
  public String save(HttpSession session,
             @RequestParam String requestType,
             @RequestParam String detail,
             @RequestParam int quantity,
             Model model) {
    if (!"PATIENT".equalsIgnoreCase((String) session.getAttribute("role"))) {
      return "redirect:/login";
    }

    String mail = (String) session.getAttribute("userMail");
    patientRepository.findByMail(mail)
        .orElseThrow(() -> new IllegalArgumentException("Patient account not found"));

    try {
      requestService.createRequest("PATIENT", requestType,
          String.valueOf(session.getAttribute("userName")),
          mail,
          detail,
          quantity);
      return "redirect:/patient/track";
    } catch (IllegalArgumentException ex) {
      model.addAttribute("error", ex.getMessage());
      return "patient/request";
    }
  }

  @GetMapping("/track")
  public String track(HttpSession session, Model model) {
    if (!"PATIENT".equalsIgnoreCase((String) session.getAttribute("role"))) {
      return "redirect:/login";
    }
    String userMail = (String) session.getAttribute("userMail");
    model.addAttribute("requests", requestService.getRequestsForUserMail(userMail));
    return "patient/track";
  }
}

package com.donation.system.service;

import com.donation.system.model.entity.Donor;
import com.donation.system.repository.DonationRepository;
import com.donation.system.repository.DonorRepository;
import com.donation.system.service.singleton.EventManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DonorService {

  private final DonorRepository donorRepository;
  private final DonationRepository donationRepository;
  private final EventManager eventManager;

  public DonorService(DonorRepository donorRepository, DonationRepository donationRepository) {
    this.donorRepository = donorRepository;
    this.donationRepository = donationRepository;
    this.eventManager = EventManager.getInstance();
  }

  public Donor saveDonor(Donor donor) {
    donor.setRole("DONOR");
    if (donor.getAvailability() == null) {
      donor.setAvailability(true);
    }
    if (donor.getStatus() == null || donor.getStatus().isBlank()) {
      donor.setStatus("PENDING");
    }
    return donorRepository.save(donor);
  }

  public com.donation.system.model.entity.Donation recordDonation(Donor donor, String donationType, String bloodType, String organType, int quantity) {
    com.donation.system.model.entity.Donation donation = new com.donation.system.model.entity.Donation();
    donation.setDonor(donor);
    donation.setDonationType(donationType.toUpperCase());
    donation.setBloodType(bloodType != null ? bloodType.toUpperCase() : null);
    donation.setOrganType(organType != null ? organType.toUpperCase() : null);
    donation.setQuantity(quantity);
    donation.setDate(LocalDateTime.now());
    donation.setStatus("SUBMITTED");
    donation.setAllocatedStatus("NOT_ALLOCATED");

    donor.setStatus("DONATED");
    donorRepository.save(donor);

    com.donation.system.model.entity.Donation saved = donationRepository.save(donation);
    eventManager.logEvent(saved);
    return saved;
  }

  public List<com.donation.system.model.entity.Donation> getDonationsForDonor(Donor donor) {
    return donationRepository.findByDonor(donor);
  }
}

package com.donation.system.service.observer;

import com.donation.system.model.entity.Donation;

public interface DonationObserver {
  void onDonationAvailable(Donation donation);
}

package com.donation.system.service.observer;

import com.donation.system.model.entity.Donation;

public interface DonationSubject {
  void registerObserver(DonationObserver observer);
  void removeObserver(DonationObserver observer);
  void notifyObservers(Donation donation);
}

package com.donation.system.service.observer;

import com.donation.system.model.entity.Donation;
import org.springframework.context.ApplicationEvent;

public class DonationEvent extends ApplicationEvent {

  private final Donation donation;

  public DonationEvent(Object source, Donation donation) {
    super(source);
    this.donation = donation;
  }

  public Donation getDonation() {
    return donation;
  }
}
```

### 16.3 Sharath - User and Request
```java
package com.donation.system.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter
@Setter
@NoArgsConstructor
public abstract class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String mail;

  @Column(nullable = false)
  private String password;

  @Column
  private Long phone;

  @Column(nullable = false)
  private String role;

  public boolean login(String inputMail, String inputPassword) {
    return Objects.equals(mail, inputMail) && Objects.equals(password, inputPassword);
  }

  public void register() {
    if (role == null || role.isBlank()) {
      role = "USER";
    }
  }

  public void updateProfile(String updatedName, Long updatedPhone) {
    this.name = updatedName;
    this.phone = updatedPhone;
  }

  public Request createBloodRequest(String bloodGroup, int quantity) {
    return new BloodRequest(this, quantity, bloodGroup);
  }

  public Request createOrganRequest(String organType, int quantity) {
    return new OrganRequest(this, quantity, organType);
  }
}

package com.donation.system.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "request_kind")
@Getter
@Setter
@NoArgsConstructor
public abstract class Request {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @Column(nullable = false)
  private String requestType;

  @Column(nullable = false)
  private Integer quantity;

  @Column(nullable = false)
  private String status;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "created_by", nullable = false)
  private User createdBy;

  protected Request(User createdBy, int quantity, String requestType) {
    this.createdBy = createdBy;
    this.quantity = quantity;
    this.requestType = requestType;
    this.status = "PENDING";
    this.createdAt = LocalDateTime.now();
  }

  @Transient
  public abstract String getRequestDetail();
}

package com.donation.system.model.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("BLOOD")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class BloodRequest extends Request {

  private String bloodGroup;

  public BloodRequest(User createdBy, int quantity, String bloodGroup) {
    super(createdBy, quantity, "BLOOD");
    this.bloodGroup = bloodGroup;
  }

  @Override
  public String getRequestDetail() {
    return bloodGroup;
  }
}

package com.donation.system.model.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("ORGAN")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class OrganRequest extends Request {

  private String organType;

  public OrganRequest(User createdBy, int quantity, String organType) {
    super(createdBy, quantity, "ORGAN");
    this.organType = organType;
  }

  @Override
  public String getRequestDetail() {
    return organType;
  }
}

package com.donation.system.controller;

import com.donation.system.model.entity.User;
import com.donation.system.repository.UserRepository;
import com.donation.system.service.RequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/requests")
public class RequestController {

  private final RequestService requestService;
  private final UserRepository userRepository;

  public RequestController(RequestService requestService, UserRepository userRepository) {
    this.requestService = requestService;
    this.userRepository = userRepository;
  }

  @GetMapping
  public String listRedirect() {
    return "redirect:/requests/list";
  }

  @GetMapping("/list")
  public String listRequests(HttpSession session, Model model) {
    if (session.getAttribute("userMail") == null) {
      return "redirect:/login";
    }
    if (!"ADMIN".equalsIgnoreCase((String) session.getAttribute("role"))) {
      return "redirect:/patient/track";
    }
    model.addAttribute("requests", requestService.getAllRequests());
    return "requests/list";
  }

  @GetMapping({"/create", "/add"})
  public String addRequestForm(HttpSession session, Model model) {
    Object userMail = session.getAttribute("userMail");
    if (userMail == null) {
      return "redirect:/login";
    }
    if (!"PATIENT".equalsIgnoreCase((String) session.getAttribute("role"))) {
      return "redirect:/requests/list";
    }
    return "redirect:/patient/request";
  }

  @PostMapping({"/create", "/add"})
  public String createRequest(HttpSession session,
                @RequestParam String requestType,
                @RequestParam String detail,
                @RequestParam int quantity,
                Model model) {
    Object userMailObj = session.getAttribute("userMail");
    if (userMailObj == null) {
      return "redirect:/login";
    }
    if (!"PATIENT".equalsIgnoreCase((String) session.getAttribute("role"))) {
      return "redirect:/requests/list";
    }

    String userMail = String.valueOf(userMailObj).trim().toLowerCase();
    Optional<User> currentUserOptional = userRepository.findByMail(userMail);
    if (currentUserOptional.isEmpty()) {
      session.invalidate();
      return "redirect:/login";
    }

    User currentUser = currentUserOptional.get();
    String creatorRole = currentUser.getRole() == null ? "USER" : currentUser.getRole();
    String userName = currentUser.getName() == null ? "User" : currentUser.getName();

    try {
      requestService.createRequest(creatorRole, requestType, userName, userMail, detail, quantity);
      return "redirect:/patient/track";
    } catch (IllegalArgumentException ex) {
      model.addAttribute("error", ex.getMessage());
      model.addAttribute("currentUserMail", userMail);
      return "patient/request";
    }
  }

  @GetMapping("/{id}")
  public String viewRequestById(HttpSession session, @PathVariable int id, Model model) {
    if (session.getAttribute("userMail") == null) {
      return "redirect:/login";
    }
    if (!"ADMIN".equalsIgnoreCase((String) session.getAttribute("role"))) {
      return "redirect:/requests/list";
    }
    return requestService.getRequestById(id)
        .map(req -> {
          model.addAttribute("request", req);
          model.addAttribute("allowedStatuses", requestService.getAllowedNextStatuses(req.getStatus()));
          return "requests/status";
        })
        .orElse("redirect:/requests/list");
  }

  @PostMapping("/{id}/status")
  public String updateRequestStatus(HttpSession session,
                    @PathVariable int id,
                    @RequestParam String status,
                    RedirectAttributes redirectAttributes) {
    if (session.getAttribute("userMail") == null) {
      return "redirect:/login";
    }
    if (!"ADMIN".equalsIgnoreCase((String) session.getAttribute("role"))) {
      return "redirect:/requests/list";
    }
    try {
      requestService.updateRequestStatus(id, status);
      redirectAttributes.addFlashAttribute("message", "Request status updated successfully.");
    } catch (IllegalArgumentException ex) {
      redirectAttributes.addFlashAttribute("error", ex.getMessage());
    }
    return "redirect:/requests/" + id;
  }
}

package com.donation.system.service.factory;

import com.donation.system.model.entity.BloodRequest;
import com.donation.system.model.entity.OrganRequest;
import com.donation.system.model.entity.Request;
import com.donation.system.model.entity.User;

public final class RequestFactory {

  private RequestFactory() {
  }

  public static Request createBloodRequest(User createdBy, String bloodGroup, int quantity) {
    return new BloodRequest(createdBy, quantity, bloodGroup);
  }

  public static Request createOrganRequest(User createdBy, String organType, int quantity) {
    return new OrganRequest(createdBy, quantity, organType);
  }
}
```

### 16.4 Neha - Inventory and Strategy
```java
package com.donation.system.controller;

import com.donation.system.model.entity.Inventory;
import com.donation.system.service.InventoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/inventory")
public class InventoryController {

  private final InventoryService inventoryService;

  public InventoryController(InventoryService inventoryService) {
    this.inventoryService = inventoryService;
  }

  @GetMapping({"", "/dashboard"})
  public String inventoryDashboard(HttpSession session, Model model) {
    String role = (String) session.getAttribute("role");
    if (!"ADMIN".equalsIgnoreCase(role)) {
      return "redirect:/login";
    }
    model.addAttribute("inventoryItems", inventoryService.getAllInventory());
    return "inventory/dashboard";
  }

  @GetMapping("/stock")
  public String stock(HttpSession session,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) String error,
            Model model) {
    String role = (String) session.getAttribute("role");
    if (!"ADMIN".equalsIgnoreCase(role)) {
      return "redirect:/login";
    }

    List<Inventory> inventoryItems = inventoryService.getAllInventory();
    model.addAttribute("inventoryItems", inventoryItems);
    model.addAttribute("message", message);
    model.addAttribute("error", error);
    return "inventory/stock";
  }

  @PostMapping("/stock/add")
  public String addStock(HttpSession session,
               @RequestParam(required = false) String bloodType,
               @RequestParam(required = false) String organType,
               @RequestParam int amount) {
    String role = (String) session.getAttribute("role");
    if (!"ADMIN".equalsIgnoreCase(role)) {
      return "redirect:/login";
    }

    try {
      inventoryService.addStock(bloodType, organType, amount);
      return "redirect:/inventory/stock?message=Stock added successfully";
    } catch (IllegalArgumentException ex) {
      return "redirect:/inventory/stock?error=" + ex.getMessage();
    }
  }

  @PostMapping("/stock/reduce")
  public String reduceStock(HttpSession session,
                @RequestParam(required = false) String bloodType,
                @RequestParam(required = false) String organType,
                @RequestParam int amount) {
    String role = (String) session.getAttribute("role");
    if (!"ADMIN".equalsIgnoreCase(role)) {
      return "redirect:/login";
    }

    boolean reduced = inventoryService.reduceStock(bloodType, organType, amount);
    if (reduced) {
      return "redirect:/inventory/stock?message=Stock reduced successfully";
    }
    return "redirect:/inventory/stock?error=Unable to reduce stock";
  }

  @GetMapping("/matching")
  public String matching(HttpSession session, Model model) {
    String role = (String) session.getAttribute("role");
    if (!"ADMIN".equalsIgnoreCase(role)) {
      return "redirect:/login";
    }

    model.addAttribute("strategy", inventoryService.getMatchingStrategy());
    model.addAttribute("requests", inventoryService.getRequestMatchingPreview());
    model.addAttribute("inventoryItems", inventoryService.getAllInventory());
    return "inventory/matching";
  }

  @PostMapping("/matching/strategy")
  public String switchStrategy(HttpSession session, @RequestParam String strategy) {
    String role = (String) session.getAttribute("role");
    if (!"ADMIN".equalsIgnoreCase(role)) {
      return "redirect:/login";
    }

    inventoryService.switchMatchingStrategy(strategy);
    return "redirect:/inventory/matching";
  }
}

package com.donation.system.service;

import com.donation.system.model.entity.Inventory;
import com.donation.system.model.entity.Request;
import com.donation.system.model.entity.Donation;
import com.donation.system.repository.InventoryRepository;
import com.donation.system.repository.DonationRepository;
import com.donation.system.repository.RequestRepository;
import com.donation.system.service.strategy.BloodMatchingStrategy;
import com.donation.system.service.strategy.MatchingStrategy;
import com.donation.system.service.strategy.OrganMatchingStrategy;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InventoryService {

  private final InventoryRepository inventoryRepository;
  private final DonationRepository donationRepository;
  private final RequestRepository requestRepository;
  private String matchingStrategy = "NORMAL";

  public InventoryService(InventoryRepository inventoryRepository,
              DonationRepository donationRepository,
              RequestRepository requestRepository) {
    this.inventoryRepository = inventoryRepository;
    this.donationRepository = donationRepository;
    this.requestRepository = requestRepository;
  }

  public void syncInventoryFromDonations() {
    List<Donation> submissions = donationRepository.findByStatus("SUBMITTED");
    Map<String, Integer> bloodInventory = new HashMap<>();
    Map<String, Integer> organInventory = new HashMap<>();

    for (Donation d : submissions) {
      if ("BLOOD".equalsIgnoreCase(d.getDonationType()) && d.getBloodType() != null) {
        String type = d.getBloodType().toUpperCase();
        bloodInventory.put(type, bloodInventory.getOrDefault(type, 0) + d.getQuantity());
      } else if ("ORGAN".equalsIgnoreCase(d.getDonationType()) && d.getOrganType() != null) {
        String type = d.getOrganType().toUpperCase();
        organInventory.put(type, organInventory.getOrDefault(type, 0) + d.getQuantity());
      }
    }

    for (Map.Entry<String, Integer> entry : bloodInventory.entrySet()) {
      Optional<Inventory> existing = inventoryRepository.findByBloodTypeIgnoreCase(entry.getKey());
      if (existing.isPresent()) {
        existing.get().setQuantity(entry.getValue());
        inventoryRepository.save(existing.get());
      } else {
        Inventory inv = new Inventory();
        inv.setBloodType(entry.getKey());
        inv.setQuantity(entry.getValue());
        inventoryRepository.save(inv);
      }
    }

    for (Map.Entry<String, Integer> entry : organInventory.entrySet()) {
      Optional<Inventory> existing = inventoryRepository.findByOrganTypeIgnoreCase(entry.getKey());
      if (existing.isPresent()) {
        existing.get().setQuantity(entry.getValue());
        inventoryRepository.save(existing.get());
      } else {
        Inventory inv = new Inventory();
        inv.setOrganType(entry.getKey());
        inv.setQuantity(entry.getValue());
        inventoryRepository.save(inv);
      }
    }
  }

  public Inventory addOrUpdateInventory(Inventory inventory) {
    if (inventory.getQuantity() == null || inventory.getQuantity() < 0) {
      inventory.setQuantity(0);
    }
    if (inventory.getBloodType() != null) {
      inventory.setBloodType(inventory.getBloodType().trim().toUpperCase());
    }
    if (inventory.getOrganType() != null) {
      inventory.setOrganType(inventory.getOrganType().trim().toUpperCase());
    }
    return inventoryRepository.save(inventory);
  }

  public List<Inventory> getAllInventory() {
    syncInventoryFromDonations();
    return inventoryRepository.findAll();
  }

  public Inventory addStock(String bloodType, String organType, int amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("Amount must be greater than zero.");
    }

    String normalizedBlood = bloodType == null || bloodType.isBlank() ? null : bloodType.trim().toUpperCase();
    String normalizedOrgan = organType == null || organType.isBlank() ? null : organType.trim().toUpperCase();

    Optional<Inventory> existing = normalizedBlood != null
        ? inventoryRepository.findByBloodTypeIgnoreCase(normalizedBlood)
        : inventoryRepository.findByOrganTypeIgnoreCase(normalizedOrgan);

    Inventory inventory = existing.orElseGet(Inventory::new);
    if (inventory.getBloodType() == null) {
      inventory.setBloodType(normalizedBlood);
    }
    if (inventory.getOrganType() == null) {
      inventory.setOrganType(normalizedOrgan);
    }
    if (inventory.getQuantity() == null) {
      inventory.setQuantity(0);
    }
    inventory.addStock(amount);
    return inventoryRepository.save(inventory);
  }

  public boolean reduceStock(String bloodType, String organType, int amount) {
    if (amount <= 0) {
      return false;
    }

    String normalizedBlood = bloodType == null || bloodType.isBlank() ? null : bloodType.trim().toUpperCase();
    String normalizedOrgan = organType == null || organType.isBlank() ? null : organType.trim().toUpperCase();

    Optional<Inventory> existing = normalizedBlood != null
        ? inventoryRepository.findByBloodTypeIgnoreCase(normalizedBlood)
        : inventoryRepository.findByOrganTypeIgnoreCase(normalizedOrgan);

    if (existing.isEmpty()) {
      return false;
    }
    Inventory inventory = existing.get();
    if (!inventory.reduceStock(amount)) {
      return false;
    }
    inventoryRepository.save(inventory);
    return true;
  }

  public void switchMatchingStrategy(String strategy) {
    if (strategy == null || strategy.isBlank()) {
      this.matchingStrategy = "NORMAL";
      return;
    }
    this.matchingStrategy = strategy.trim().toUpperCase();
  }

  public String getMatchingStrategy() {
    return matchingStrategy;
  }

  public List<Request> getRequestMatchingPreview() {
    List<Request> pending = requestRepository.findByStatusIgnoreCaseOrderByCreatedAtAsc("PENDING");
    if ("HIGH_PRIORITY".equalsIgnoreCase(matchingStrategy)) {
      return pending.stream()
          .sorted(Comparator.comparing(Request::getQuantity).reversed())
          .collect(Collectors.toList());
    }
    return pending;
  }

  public boolean allocateDonation(int donationId, int requestId, int quantity) {
    Optional<Donation> donOptional = donationRepository.findById(donationId);
    if (donOptional.isEmpty()) {
      return false;
    }

    Donation don = donOptional.get();
    String invQuery = "BLOOD".equalsIgnoreCase(don.getDonationType()) ? don.getBloodType() : don.getOrganType();

    Optional<Inventory> invOptional = "BLOOD".equalsIgnoreCase(don.getDonationType())
        ? inventoryRepository.findByBloodTypeIgnoreCase(invQuery)
        : inventoryRepository.findByOrganTypeIgnoreCase(invQuery);

    if (invOptional.isEmpty() || !invOptional.get().reduceStock(quantity)) {
      return false;
    }

    don.setAllocatedStatus("ALLOCATED");
    don.setStatus("ALLOCATED");
    donationRepository.save(don);
    inventoryRepository.save(invOptional.get());
    return true;
  }

  public boolean canFulfill(Request request) {
    MatchingStrategy strategy = "BLOOD".equalsIgnoreCase(request.getRequestType())
        ? new BloodMatchingStrategy()
        : new OrganMatchingStrategy();

    for (Inventory inventory : inventoryRepository.findAll()) {
      if (strategy.matches(inventory, request) && inventory.checkAvailability(request.getQuantity())) {
        return true;
      }
    }
    return false;
  }
}

package com.donation.system.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "blood_type", length = 10)
  private String bloodType;

  @Column(name = "organ_type", length = 50)
  private String organType;

  @Column(nullable = false)
  private Integer quantity = 0;

  public void addStock(int amount) {
    if (amount > 0) {
      this.quantity += amount;
    }
  }

  public boolean reduceStock(int amount) {
    if (this.quantity >= amount) {
      this.quantity -= amount;
      return true;
    }
    return false;
  }

  public boolean checkAvailability(int amount) {
    return this.quantity >= amount;
  }
}

package com.donation.system.service.strategy;

import com.donation.system.model.entity.Inventory;
import com.donation.system.model.entity.Request;

public interface MatchingStrategy {
  boolean matches(Inventory inventory, Request request);
}

package com.donation.system.service.strategy;

import com.donation.system.model.entity.BloodRequest;
import com.donation.system.model.entity.Inventory;
import com.donation.system.model.entity.Request;

public class BloodMatchingStrategy implements MatchingStrategy {

  @Override
  public boolean matches(Inventory inventory, Request request) {
    if (!(request instanceof BloodRequest bloodRequest)) {
      return false;
    }
    if (inventory.getBloodType() == null || bloodRequest.getBloodGroup() == null) {
      return false;
    }
    return inventory.getBloodType().equalsIgnoreCase(bloodRequest.getBloodGroup());
  }
}

package com.donation.system.service.strategy;

import com.donation.system.model.entity.Inventory;
import com.donation.system.model.entity.OrganRequest;
import com.donation.system.model.entity.Request;

public class OrganMatchingStrategy implements MatchingStrategy {

  @Override
  public boolean matches(Inventory inventory, Request request) {
    if (!(request instanceof OrganRequest organRequest)) {
      return false;
    }
    if (inventory.getOrganType() == null || organRequest.getOrganType() == null) {
      return false;
    }
    return inventory.getOrganType().equalsIgnoreCase(organRequest.getOrganType());
  }
}
```

## 17) Database Table Reference
This section describes the current database tables and what each column means.

### 17.1 users
Purpose: stores all user types in one table using `dtype` for inheritance.

Columns:
- `id` - Primary key.
- `dtype` - Discriminator value for `ADMIN`, `DONOR`, or `PATIENT`.
- `name` - Display name.
- `mail` - Unique login email.
- `password` - Plain-text password in the current implementation.
- `phone` - Optional phone number stored as `BIGINT`.
- `role` - Application role used for session and route checks.
- `blood_type` - Donor blood type, if applicable.
- `organ_type` - Donor organ preference/type, if applicable.
- `availability` - Donor availability flag.
- `status` - User-specific status field.
- `condition_note` - Patient condition note.

### 17.2 requests
Purpose: stores all patient/admin request records in one table using `request_kind` for inheritance.

Columns:
- `id` - Primary key.
- `request_kind` - Discriminator value for `BLOOD` or `ORGAN`.
- `request_type` - Stored request type for processing.
- `quantity` - Number of units requested.
- `status` - Workflow status. Current app uses `PENDING`, `APPROVED`, `DENIED`.
- `created_at` - Request creation timestamp.
- `created_by` - Foreign key to `users.id`.
- `blood_group` - Blood group detail for blood requests.
- `organ_type` - Organ detail for organ requests.

### 17.3 donations
Purpose: stores donor submissions and admin-entered donation rows.

Columns:
- `id` - Primary key.
- `donation_type` - `BLOOD` or `ORGAN`.
- `blood_type` - Blood specification, if donation is blood.
- `organ_type` - Organ specification, if donation is organ.
- `date` - Donation timestamp.
- `quantity` - Quantity donated.
- `status` - Donation status such as `SUBMITTED` or `ALLOCATED`.
- `allocated_status` - Allocation flag such as `NOT_ALLOCATED` or `ALLOCATED`.
- `donor_id` - Foreign key to `users.id`.

### 17.4 inventory
Purpose: stores current blood and organ stock.

Columns:
- `id` - Primary key.
- `blood_type` - Blood stock type.
- `organ_type` - Organ stock type.
- `quantity` - Available stock count.

### 17.5 legacy backup tables
Created by `sql/mysql_schema_repair.sql` before migration.

Tables:
- `users_legacy`
- `donations_legacy`
- `requests_legacy`
- `inventory_legacy`

Purpose:
- Preserve old data during schema repair.
- Can be dropped later with `sql/mysql_drop_legacy_tables.sql` after validation.

## 18) Notes on current workflow
- Patients submit requests from the patient page.
- Admin reviews and approves or denies requests from `/admin/requests`.
- Patient tracking reflects the final decision.
- Inventory remains admin-managed and auto-synced from donations.

---
SHARATH08