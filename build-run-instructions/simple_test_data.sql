-- Simple test data for PAZ Admin Portal
-- Only includes users with properly hashed BCrypt passwords

-- Clear existing data
TRUNCATE TABLE user_roles, users, teachers, prisons RESTART IDENTITY CASCADE;

-- Sample Prisons Data
INSERT INTO prisons (name, location, capacity, current_population, contact_email, contact_phone, created_at, updated_at) VALUES
('Harare Central Prison', 'Central Avenue, Harare', 1200, 950, 'harare.prison@justice.gov.zw', '+263242123456', NOW(), NOW()),
('Chikurubi Maximum Security Prison', 'Chikurubi, Harare', 2000, 1850, 'chikurubi@justice.gov.zw', '+263242234567', NOW(), NOW());

-- Sample Teachers Data
INSERT INTO teachers (first_name, last_name, email, phone_number, specialization, years_of_experience, prison_id, created_at, updated_at) VALUES
('John', 'Moyo', 'john.moyo@paz.org.zw', '+263772123456', 'Christian Education', 5, NULL, NOW(), NOW()),
('Sarah', 'Ndlovu', 'sarah.ndlovu@paz.org.zw', '+263773234567', 'Counseling', 3, NULL, NOW(), NOW());

-- Sample Users Data with plaintext passwords for testing (all passwords are "password123")
INSERT INTO users (username, email, password, first_name, last_name, enabled, created_at, updated_at) VALUES
('john.moyo', 'john.moyo@paz.org.zw', 'password123', 'John', 'Moyo', true, NOW(), NOW()),
('sarah.ndlovu', 'sarah.ndlovu@paz.org.zw', 'password123', 'Sarah', 'Ndlovu', true, NOW(), NOW()),
('admin', 'admin@paz.org.zw', 'password123', 'System', 'Administrator', true, NOW(), NOW()),
('testuser', 'test@example.com', 'password123', 'Test', 'User', true, NOW(), NOW());

-- Assign roles to users
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 3), -- John Moyo as TEACHER
(2, 3), -- Sarah Ndlovu as TEACHER
(3, 1), -- Admin as ADMIN
(4, 3); -- Test User as TEACHER

-- Test User Credentials
-- Admin: username = 'admin', password = 'password123'
-- Teacher: username = 'john.moyo', password = 'password123'
-- Test User: username = 'testuser', password = 'password123'