-- 📊 PAZ Admin Portal - Sample Data for Testing
-- This file contains realistic mock data for testing all system functions
-- Run this SQL after the database schema has been created
-- All passwords are "password123" with proper BCrypt hashes

-- 🏢 Sample Prisons Data (matches current schema)
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

-- 🧑‍🏫 Sample Teachers Data (matches current schema)
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

-- 👥 Sample Users Data (with properly hashed BCrypt passwords for "password123")
INSERT INTO users (username, email, password, first_name, last_name, enabled, created_at, updated_at) VALUES
('john.moyo', 'john.moyo@paz.org.zw', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'John', 'Moyo', true, NOW(), NOW()),
('sarah.ndlovu', 'sarah.ndlovu@paz.org.zw', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Sarah', 'Ndlovu', true, NOW(), NOW()),
('thomas.chikowore', 'thomas.chikowore@paz.org.zw', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Thomas', 'Chikowore', true, NOW(), NOW()),
('grace.mandaza', 'grace.mandaza@paz.org.zw', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Grace', 'Mandaza', true, NOW(), NOW()),
('blessing.sibanda', 'blessing.sibanda@paz.org.zw', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Blessing', 'Sibanda', false, NOW(), NOW()),
('memory.chigumba', 'memory.chigumba@paz.org.zw', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Memory', 'Chigumba', true, NOW(), NOW()),
('david.mupfumi', 'david.mupfumi@paz.org.zw', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'David', 'Mupfumi', true, NOW(), NOW()),
('ruth.marimo', 'ruth.marimo@paz.org.zw', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Ruth', 'Marimo', false, NOW(), NOW()),
('peter.zulu', 'peter.zulu@paz.org.zw', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Peter', 'Zulu', true, NOW(), NOW()),
('loveness.ncube', 'loveness.ncube@paz.org.zw', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Loveness', 'Ncube', true, NOW(), NOW());

-- Add admin user (not linked to teacher)
INSERT INTO users (username, email, password, first_name, last_name, enabled, created_at, updated_at) VALUES
('admin', 'admin@paz.org.zw', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'System', 'Administrator', true, NOW(), NOW());

-- Add test user for easier testing
INSERT INTO users (username, email, password, first_name, last_name, enabled, created_at, updated_at) VALUES
('testuser', 'test@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Test', 'User', true, NOW(), NOW());

-- Assign roles to users (assuming role IDs exist: 1=ADMIN, 2=PRISON_MANAGER, 3=TEACHER)
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 3), -- John Moyo as TEACHER
(2, 3), -- Sarah Ndlovu as TEACHER
(3, 2), -- Thomas Chikowore as PRISON_MANAGER
(4, 3), -- Grace Mandaza as TEACHER
(6, 1), -- Memory Chigumba as ADMIN
(7, 3), -- David Mupfumi as TEACHER
(9, 2), -- Peter Zulu as PRISON_MANAGER
(10, 3), -- Loveness Ncube as TEACHER
(11, 1), -- Admin as ADMIN
(12, 3); -- Test User as TEACHER

-- Note: Additional tables like task_reports, print_reqs, classes, students, sessions, attendances, audit_log
-- are not included in the current schema. These would need to be created separately if needed.

-- 🔍 Useful Queries for Testing

-- Get all active teachers
SELECT * FROM teachers;

-- Get users with their roles
SELECT u.username, u.email, u.first_name, u.last_name, r.name as role_name
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
ORDER BY r.name, u.username;

-- Get prisons with current population
SELECT name, location, capacity, current_population, contact_email, contact_phone
FROM prisons
ORDER BY name;

-- 🎯 Test User Credentials
-- All users use the same password: "password123"
-- 
-- Admin User:
--   Username: admin
--   Password: password123
--   Role: ROLE_ADMIN
--
-- Prison Manager:
--   Username: thomas.chikowore
--   Password: password123  
--   Role: ROLE_PRISON_MANAGER
--
-- Teacher Users:
--   Username: john.moyo
--   Password: password123
--   Role: ROLE_TEACHER
--
--   Username: testuser
--   Password: password123
--   Role: ROLE_TEACHER
--
--   Username: sarah.ndlovu
--   Password: password123
--   Role: ROLE_TEACHER
--
--   Username: grace.mandaza
--   Password: password123
--   Role: ROLE_TEACHER
--
--   Username: david.mupfumi
--   Password: password123
--   Role: ROLE_TEACHER
--
--   Username: peter.zulu
--   Password: password123
--   Role: ROLE_PRISON_MANAGER
--
--   Username: loveness.ncube
--   Password: password123
--   Role: ROLE_TEACHER
--
--   Username: memory.chigumba
--   Password: password123
--   Role: ROLE_ADMIN (also has TEACHER role)

-- 📊 Dashboard Statistics Queries

-- Total active users
SELECT COUNT(*) as total_users FROM users WHERE enabled = true;

-- Users by role
SELECT r.name as role_name, COUNT(*) as user_count
FROM user_roles ur
JOIN roles r ON ur.role_id = r.id
GROUP BY r.name
ORDER BY user_count DESC;

-- Active teachers count
SELECT COUNT(*) as active_teachers FROM teachers;

-- Prisons capacity utilization
SELECT name, capacity, current_population, 
       ROUND((current_population * 100.0 / capacity), 2) as utilization_percentage
FROM prisons
ORDER BY utilization_percentage DESC;

-- Note: The BCrypt password hash used is for "password123" and can be verified using:
-- BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
-- boolean matches = encoder.matches("password123", "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi");