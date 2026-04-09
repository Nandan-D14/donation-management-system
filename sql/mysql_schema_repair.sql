-- In-place schema repair for donationdb (no new database required)
-- Strategy:
-- 1) Rename current tables to *_legacy
-- 2) Create fresh tables that match current JPA entities
-- 3) Migrate existing data where possible
-- 4) Keep legacy tables as backup

USE donationdb;

SET FOREIGN_KEY_CHECKS = 0;

-- Backup existing tables (kept for rollback/manual reference)
RENAME TABLE users TO users_legacy;
RENAME TABLE donations TO donations_legacy;
RENAME TABLE requests TO requests_legacy;
RENAME TABLE inventory TO inventory_legacy;

-- =========================
-- 1) New users table
-- =========================
CREATE TABLE users (
  id INT NOT NULL AUTO_INCREMENT,
  dtype VARCHAR(31) NOT NULL,
  name VARCHAR(255) NOT NULL,
  mail VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  phone BIGINT NULL,
  role VARCHAR(255) NOT NULL,
  blood_type VARCHAR(255) NULL,
  organ_type VARCHAR(255) NULL,
  availability BIT(1) NULL,
  status VARCHAR(255) NULL,
  condition_note VARCHAR(255) NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_mail (mail)
);

-- =========================
-- 2) New donations table
-- =========================
CREATE TABLE donations (
  id INT NOT NULL AUTO_INCREMENT,
  donation_type VARCHAR(255) NOT NULL,
  blood_type VARCHAR(255) NULL,
  organ_type VARCHAR(255) NULL,
  date DATETIME(6) NOT NULL,
  quantity INT NOT NULL,
  status VARCHAR(255) NOT NULL,
  allocated_status VARCHAR(255) NULL,
  donor_id INT NULL,
  PRIMARY KEY (id),
  KEY idx_donations_donor_id (donor_id)
);

-- =========================
-- 3) New requests table
-- =========================
CREATE TABLE requests (
  id INT NOT NULL AUTO_INCREMENT,
  request_kind VARCHAR(31) NOT NULL,
  request_type VARCHAR(255) NOT NULL,
  quantity INT NOT NULL,
  status VARCHAR(255) NOT NULL,
  created_at DATETIME(6) NOT NULL,
  created_by INT NOT NULL,
  blood_group VARCHAR(255) NULL,
  organ_type VARCHAR(255) NULL,
  PRIMARY KEY (id),
  KEY idx_requests_created_by (created_by)
);

-- =========================
-- 4) New inventory table
-- =========================
CREATE TABLE inventory (
  id INT NOT NULL AUTO_INCREMENT,
  blood_type VARCHAR(10) NULL,
  organ_type VARCHAR(50) NULL,
  quantity INT NOT NULL,
  PRIMARY KEY (id)
);

-- =========================
-- Data migration: users
-- =========================
INSERT INTO users (
  id, dtype, name, mail, password, phone, role,
  blood_type, organ_type, availability, status, condition_note
)
SELECT
  userid,
  CASE
    WHEN UPPER(COALESCE(dtype, '')) IN ('ADMIN', 'DONOR', 'PATIENT') THEN UPPER(dtype)
    ELSE 'PATIENT'
  END AS dtype,
  COALESCE(NULLIF(name, ''), 'Unknown User') AS name,
  mail,
  COALESCE(NULLIF(password, ''), 'changeme') AS password,
  phone,
  COALESCE(NULLIF(UPPER(role), ''),
    CASE
      WHEN UPPER(COALESCE(dtype, '')) IN ('ADMIN', 'DONOR', 'PATIENT') THEN UPPER(dtype)
      ELSE 'PATIENT'
    END
  ) AS role,
  blood_type,
  organ_type,
  availability,
  status,
  condition_note
FROM users_legacy
WHERE mail IS NOT NULL;

-- =========================
-- Data migration: donations
-- =========================
INSERT INTO donations (
  id, donation_type, blood_type, organ_type, date, quantity, status, allocated_status, donor_id
)
SELECT
  donationid,
  COALESCE(NULLIF(donation_type, ''), 'BLOOD') AS donation_type,
  blood_type,
  organ_type,
  COALESCE(date, NOW(6)) AS date,
  COALESCE(quantity, 0) AS quantity,
  COALESCE(NULLIF(status, ''), 'SUBMITTED') AS status,
  COALESCE(NULLIF(allocated_status, ''), 'NOT_ALLOCATED') AS allocated_status,
  donor_id
FROM donations_legacy;

-- =========================
-- Data migration: requests
-- =========================
INSERT INTO requests (
  id, request_kind, request_type, quantity, status, created_at, created_by, blood_group, organ_type
)
SELECT
  id,
  CASE
    WHEN UPPER(COALESCE(request_kind, '')) IN ('BLOOD', 'ORGAN') THEN UPPER(request_kind)
    WHEN UPPER(COALESCE(request_type, '')) IN ('BLOOD', 'ORGAN') THEN UPPER(request_type)
    ELSE 'BLOOD'
  END AS request_kind,
  CASE
    WHEN UPPER(COALESCE(request_type, '')) IN ('BLOOD', 'ORGAN') THEN UPPER(request_type)
    WHEN UPPER(COALESCE(request_kind, '')) IN ('BLOOD', 'ORGAN') THEN UPPER(request_kind)
    ELSE 'BLOOD'
  END AS request_type,
  COALESCE(quantity, units_requested, units_required, 1) AS quantity,
  COALESCE(NULLIF(status, ''), 'PENDING') AS status,
  COALESCE(created_at, request_date, NOW(6)) AS created_at,
  COALESCE(created_by, CAST(created_by_user_id AS SIGNED), patient_id) AS created_by,
  blood_group,
  organ_type
FROM requests_legacy
WHERE COALESCE(created_by, CAST(created_by_user_id AS SIGNED), patient_id) IS NOT NULL;

-- =========================
-- Data migration: inventory
-- =========================
INSERT INTO inventory (id, blood_type, organ_type, quantity)
SELECT id, blood_type, organ_type, COALESCE(quantity, 0)
FROM inventory_legacy;

-- =========================
-- Recreate foreign keys
-- =========================
ALTER TABLE donations
  ADD CONSTRAINT fk_donations_donor
  FOREIGN KEY (donor_id) REFERENCES users(id)
  ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE requests
  ADD CONSTRAINT fk_requests_created_by
  FOREIGN KEY (created_by) REFERENCES users(id)
  ON DELETE RESTRICT ON UPDATE CASCADE;

SET FOREIGN_KEY_CHECKS = 1;

-- =========================
-- Validation queries
-- =========================
SELECT 'users' AS table_name, COUNT(*) AS rows_count FROM users
UNION ALL
SELECT 'donations', COUNT(*) FROM donations
UNION ALL
SELECT 'requests', COUNT(*) FROM requests
UNION ALL
SELECT 'inventory', COUNT(*) FROM inventory;
