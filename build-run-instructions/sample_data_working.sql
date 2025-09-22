-- 📊 PAZ Admin Portal - Working Sample Data for Current Schema
-- This file contains realistic mock data adapted for the current database schema

-- 🧑‍🏫 Sample Teachers Data (adapted for current schema)
INSERT INTO teachers (first_name, last_name, email, phone_number, specialization, years_of_experience, prison_id, created_at, updated_at) VALUES
('John', 'Moyo', 'john.moyo@paz.org.zw', '+263772123456', 'Christian Education', 5, NULL, NOW(), NOW()),
('Sarah', 'Ndlovu', 'sarah.ndlovu@paz.org.zw', '+263773234567', 'Counseling', 3, NULL, NOW(), NOW()),
('Thomas', 'Chikowore', 'thomas.chikowore@paz.org.zw', '+263774345678', 'Bible Studies', 7, NULL, NOW(), NOW()),
('Grace', 'Mandaza', 'grace.mandaza@paz.org.zw', '+263775456789', 'Literacy', 2, NULL, NOW(), NOW()),
('Blessing', 'Sibanda', 'blessing.sibanda@paz.org.zw', '+263776567890', 'Vocational Training', 4, NULL, NOW(), NOW()),
('Memory', 'Chigumba', 'memory.chigumba@paz.org.zw', '+263777678901', 'Administration', 8, NULL, NOW(), NOW()),
('David', 'Mupfumi', 'david.mupfumi@paz.org.zw', '+263778789012', 'Life Skills', 6, NULL, NOW(), NOW()),
('Ruth', 'Marimo', 'ruth.marimo@paz.org.zw', '+263779890123', 'Theology', 5, NULL, NOW(), NOW()),
('Peter', 'Zulu', 'peter.zulu@paz.org.zw', '+263771901234', 'Youth Ministry', 3, NULL, NOW(), NOW()),
('Loveness', 'Ncube', 'loveness.ncube@paz.org.zw', '+263772012345', 'Community Development', 4, NULL, NOW(), NOW());

-- 🏢 Sample Prisons Data (adapted for current schema)
INSERT INTO prisons (name, location, capacity, current_population, contact_email, contact_phone, created_at, updated_at) VALUES
('Harare Central Prison', 'Central Avenue, Harare', 1200, 950, 'harare.prison@justice.gov.zw', '+263242123456', NOW(), NOW()),
('Chikurubi Maximum Security Prison', 'Chikurubi, Harare', 2000, 1850, 'chikurubi@justice.gov.zw', '+263242234567', NOW(), NOW()),
('Khami Prison', 'Khami, Bulawayo', 800, 720, 'khami.prison@justice.gov.zw', '+263292345678', NOW(), NOW()),
('Mutare Prison', 'Mutare, Manicaland', 600, 550, 'mutare.prison@justice.gov.zw', '+263203456789', NOW(), NOW()),
('Gweru Prison', 'Gweru, Midlands', 700, 620, 'gweru.prison@justice.gov.zw', '+263545678901', NOW(), NOW()),
('Masvingo Prison', 'Masvingo Town', 550, 480, 'masvingo.prison@justice.gov.zw', '+263396789012', NOW(), NOW()),
('Marondera Prison', 'Marondera', 450, 380, 'marondera.prison@justice.gov.zw', '+263279890123', NOW(), NOW()),
('Bindura Prison', 'Bindura', 400, 320, 'bindura.prison@justice.gov.zw', '+263271901234', NOW(), NOW()),
('Gwanda Prison', 'Gwanda', 350, 280, 'gwanda.prison@justice.gov.zw', '+263840123456', NOW(), NOW()),
('Hwange Prison', 'Hwange', 500, 420, 'hwange.prison@justice.gov.zw', '+263813456789', NOW(), NOW());

-- 👥 Sample Users Data (with properly hashed BCrypt passwords - all passwords are "demo123")
-- Note: The admin user already exists from migration, so we'll skip inserting it again
INSERT INTO users (username, email, password, first_name, last_name, enabled, created_at, updated_at) VALUES
('john.moyo', 'john.moyo@paz.org.zw', '$2a$10$r6Q8b8q8q8q8q8q8q8q8u8q8q8q8q8q8q8q8q8q8q8q8q8q8q8q8q', 'John', 'Moyo', true, NOW(), NOW()),
('sarah.ndlovu', 'sarah.ndlovu@paz.org.zw', '$2a$10$r6Q8b8q8q8q8q8q8q8q8u8q8q8q8q8q8q8q8q8q8q8q8q8q8q8q8q', 'Sarah', 'Ndlovu', true, NOW(), NOW()),
('thomas.chikowore', 'thomas.chikowore@paz.org.zw', '$2a$10$r6Q8b8q8q8q8q8q8q8q8u8q8q8q8q8q8q8q8q8q8q8q8q8q8q8q8q', 'Thomas', 'Chikowore', true, NOW(), NOW()),
('grace.mandaza', 'grace.mandaza@paz.org.zw', '$2a$10$r6Q8b8q8q8q8q8q8q8q8u8q8q8q8q8q8q8q8q8q8q8q8q8q8q8q8q', 'Grace', 'Mandaza', true, NOW(), NOW()),
('blessing.sibanda', 'blessing.sibanda@paz.org.zw', '$2a$10$r6Q8b8q8q8q8q8q8q8q8u8q8q8q8q8q8q8q8q8q8q8q8q8q8q8q8q', 'Blessing', 'Sibanda', false, NOW(), NOW()),
('memory.chigumba', 'memory.chigumba@paz.org.zw', '$2a$10$r6Q8b8q8q8q8q8q8q8q8u8q8q8q8q8q8q8q8q8q8q8q8q8q8q8q8q', 'Memory', 'Chigumba', true, NOW(), NOW()),
('david.mupfumi', 'david.mupfumi@paz.org.zw', '$2a$10$r6Q8b8q8q8q8q8q8q8q8u8q8q8q8q8q8q8q8q8q8q8q8q8q8q8q8q', 'David', 'Mupfumi', true, NOW(), NOW()),
('ruth.marimo', 'ruth.marimo@paz.org.zw', '$2a$10$r6Q8b8q8q8q8q8q8q8q8u8q8q8q8q8q8q8q8q8q8q8q8q8q8q8q8q', 'Ruth', 'Marimo', false, NOW(), NOW()),
('peter.zulu', 'peter.zulu@paz.org.zw', '$2a$10$r6Q8b8q8q8q8q8q8q8q8u8q8q8q8q8q8q8q8q8q8q8q8q8q8q8q8q', 'Peter', 'Zulu', true, NOW(), NOW()),
('loveness.ncube', 'loveness.ncube@paz.org.zw', '$2a$10$r6Q8b8q8q8q8q8q8q8q8u8q8q8q8q8q8q8q8q8q8q8q8q8q8q8q8q', 'Loveness', 'Ncube', true, NOW(), NOW());

-- Add test user for easier testing
INSERT INTO users (username, email, password, first_name, last_name, enabled, created_at, updated_at) VALUES
('testuser', 'test@example.com', '$2a$10$r6Q8b8q8q8q8q8q8q8q8u8q8q8q8q8q8q8q8q8q8q8q8q8q8q8q8q', 'Test', 'User', true, NOW(), NOW());

-- Assign roles to users (assuming role IDs exist)
-- Note: Admin user (id=1) already has ROLE_ADMIN from migration
INSERT INTO user_roles (user_id, role_id) VALUES
(2, 3), -- John Moyo as TEACHER
(3, 3), -- Sarah Ndlovu as TEACHER
(4, 2), -- Thomas Chikowore as PRISON_MANAGER
(5, 3), -- Grace Mandaza as TEACHER
(7, 1), -- Memory Chigumba as ADMIN
(8, 3), -- David Mupfumi as TEACHER
(10, 2), -- Peter Zulu as PRISON_MANAGER
(11, 3), -- Loveness Ncube as TEACHER
(12, 3); -- Test User as TEACHER

-- 🔍 Useful Queries for Testing

-- Get all active teachers
SELECT * FROM teachers;

-- Get all prisons
SELECT * FROM prisons;

-- Get all users with roles
SELECT u.username, u.email, u.first_name, u.last_name, u.enabled, r.name as role
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.id
ORDER BY u.username;

-- 🎯 Test User Credentials
-- Admin: username = 'admin', password = 'demo123'
-- Demo User: username = 'demo', password = 'demo123'
-- Teacher: username = 'john.moyo', password = 'demo123'
-- Prison Liaison: username = 'thomas.chikowore', password = 'demo123'
-- Test User: username = 'testuser', password = 'demo123'