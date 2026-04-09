USE donationdb;

-- Dummy users
INSERT INTO users (dtype, name, mail, password, phone, role, availability, status)
VALUES
('ADMIN', 'Admin One', 'admin.one@test.com', 'admin123', 9999999999, 'ADMIN', 1, 'ACTIVE'),
('DONOR', 'Donor Blood', 'donor.blood@test.com', 'donor123', 8888888888, 'DONOR', 1, 'DONATED'),
('PATIENT', 'Patient One', 'patient.one@test.com', 'patient123', 7777777777, 'PATIENT', 1, 'PENDING');

-- Dummy donations (linked to donor)
INSERT INTO donations (donation_type, blood_type, organ_type, date, quantity, status, allocated_status, donor_id)
SELECT 'BLOOD', 'O+', NULL, NOW(6), 3, 'SUBMITTED', 'NOT_ALLOCATED', id
FROM users
WHERE mail = 'donor.blood@test.com'
LIMIT 1;

INSERT INTO donations (donation_type, blood_type, organ_type, date, quantity, status, allocated_status, donor_id)
SELECT 'ORGAN', NULL, 'Kidney', NOW(6), 1, 'SUBMITTED', 'NOT_ALLOCATED', id
FROM users
WHERE mail = 'donor.blood@test.com'
LIMIT 1;

-- Dummy request (linked to patient user)
INSERT INTO requests (request_kind, request_type, quantity, status, created_at, created_by, blood_group, organ_type)
SELECT 'BLOOD', 'BLOOD', 2, 'PENDING', NOW(6), id, 'O+', NULL
FROM users
WHERE mail = 'patient.one@test.com'
LIMIT 1;

-- Inventory seed (optional baseline)
INSERT INTO inventory (blood_type, organ_type, quantity)
VALUES ('O+', NULL, 3), (NULL, 'Kidney', 1);

-- Verify
SELECT 'users' AS table_name, COUNT(*) AS rows_count FROM users
UNION ALL
SELECT 'donations', COUNT(*) FROM donations
UNION ALL
SELECT 'requests', COUNT(*) FROM requests
UNION ALL
SELECT 'inventory', COUNT(*) FROM inventory;
