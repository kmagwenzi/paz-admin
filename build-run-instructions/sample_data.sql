-- 📊 PAZ Admin Portal - Sample Data for Testing
-- This file contains realistic mock data for testing all system functions
-- Run this SQL after the database schema has been created

-- 🧑‍🏫 Sample Teachers Data
INSERT INTO teachers (full_name, email, mobile, status, created_at, updated_at) VALUES
('John Moyo', 'john.moyo@paz.org.zw', '+263772123456', 'ACTIVE', NOW(), NOW()),
('Sarah Ndlovu', 'sarah.ndlovu@paz.org.zw', '+263773234567', 'ACTIVE', NOW(), NOW()),
('Thomas Chikowore', 'thomas.chikowore@paz.org.zw', '+263774345678', 'ACTIVE', NOW(), NOW()),
('Grace Mandaza', 'grace.mandaza@paz.org.zw', '+263775456789', 'ACTIVE', NOW(), NOW()),
('Blessing Sibanda', 'blessing.sibanda@paz.org.zw', '+263776567890', 'INACTIVE', NOW(), NOW()),
('Memory Chigumba', 'memory.chigumba@paz.org.zw', '+263777678901', 'ACTIVE', NOW(), NOW()),
('David Mupfumi', 'david.mupfumi@paz.org.zw', '+263778789012', 'ACTIVE', NOW(), NOW()),
('Ruth Marimo', 'ruth.marimo@paz.org.zw', '+263779890123', 'SUSPENDED', NOW(), NOW()),
('Peter Zulu', 'peter.zulu@paz.org.zw', '+263771901234', 'ACTIVE', NOW(), NOW()),
('Loveness Ncube', 'loveness.ncube@paz.org.zw', '+263772012345', 'ACTIVE', NOW(), NOW());

-- 🏢 Sample Prisons Data
INSERT INTO prisons (name, region, capacity, address, created_at, updated_at) VALUES
('Harare Central Prison', 'Harare', 1200, 'Central Avenue, Harare', NOW(), NOW()),
('Chikurubi Maximum Security Prison', 'Harare', 2000, 'Chikurubi, Harare', NOW(), NOW()),
('Khami Prison', 'Bulawayo', 800, 'Khami, Bulawayo', NOW(), NOW()),
('Mutare Prison', 'Manicaland', 600, 'Mutare, Manicaland', NOW(), NOW()),
('Gweru Prison', 'Midlands', 700, 'Gweru, Midlands', NOW(), NOW()),
('Masvingo Prison', 'Masvingo', 550, 'Masvingo Town', NOW(), NOW()),
('Marondera Prison', 'Mashonaland East', 450, 'Marondera', NOW(), NOW()),
('Bindura Prison', 'Mashonaland Central', 400, 'Bindura', NOW(), NOW()),
('Gwanda Prison', 'Matabeleland South', 350, 'Gwanda', NOW(), NOW()),
('Hwange Prison', 'Matabeleland North', 500, 'Hwange', NOW(), NOW());

-- 👥 Sample Users Data (with hashed passwords - all passwords are "password123")
INSERT INTO users (teacher_id, username, password_hash, role, enabled, created_at, updated_at) VALUES
(1, 'john.moyo', '$2a$10$examplehashedpassword1234567890', 'TEACHER', true, NOW(), NOW()),
(2, 'sarah.ndlovu', '$2a$10$examplehashedpassword1234567890', 'TEACHER', true, NOW(), NOW()),
(3, 'thomas.chikowore', '$2a$10$examplehashedpassword1234567890', 'PRISON_LIAISON', true, NOW(), NOW()),
(4, 'grace.mandaza', '$2a$10$examplehashedpassword1234567890', 'TEACHER', true, NOW(), NOW()),
(5, 'blessing.sibanda', '$2a$10$examplehashedpassword1234567890', 'TEACHER', false, NOW(), NOW()),
(6, 'memory.chigumba', '$2a$10$examplehashedpassword1234567890', 'ADMIN', true, NOW(), NOW()),
(7, 'david.mupfumi', '$2a$10$examplehashedpassword1234567890', 'TEACHER', true, NOW(), NOW()),
(8, 'ruth.marimo', '$2a$10$examplehashedpassword1234567890', 'TEACHER', false, NOW(), NOW()),
(9, 'peter.zulu', '$2a$10$examplehashedpassword1234567890', 'PRISON_LIAISON', true, NOW(), NOW()),
(10, 'loveness.ncube', '$2a$10$examplehashedpassword1234567890', 'TEACHER', true, NOW(), NOW());

-- Add admin user (not linked to teacher)
INSERT INTO users (teacher_id, username, password_hash, role, enabled, created_at, updated_at) VALUES
(NULL, 'admin', '$2a$10$examplehashedpassword1234567890', 'SUPER_ADMIN', true, NOW(), NOW());

-- 📋 Sample Task Reports Data (Teaching Task Forms)
INSERT INTO task_reports (teacher_id, prison_id, date, hours, lesson_title, saved_persons, issues, created_at, updated_at) VALUES
(1, 1, '2025-09-15', 3.5, 'Introduction to Christianity', 2, 'Power outage during session', NOW(), NOW()),
(1, 2, '2025-09-10', 4.0, 'The Story of Jesus', 3, NULL, NOW(), NOW()),
(2, 3, '2025-09-12', 2.5, 'Basic Bible Study', 1, 'Limited materials available', NOW(), NOW()),
(2, 1, '2025-09-14', 3.0, 'Prayer and Meditation', 0, 'Low inmate participation', NOW(), NOW()),
(3, 4, '2025-09-08', 5.0, 'Life Transformation Program', 5, 'Excellent engagement', NOW(), NOW()),
(4, 5, '2025-09-11', 3.5, 'Forgiveness and Reconciliation', 2, NULL, NOW(), NOW()),
(4, 6, '2025-09-13', 4.0, 'Building Healthy Relationships', 3, 'Technical issues with projector', NOW(), NOW()),
(7, 7, '2025-09-09', 2.0, 'Basic Literacy Class', 0, 'First session - introductory', NOW(), NOW()),
(7, 8, '2025-09-16', 3.5, 'Advanced Bible Study', 4, NULL, NOW(), NOW()),
(10, 9, '2025-09-17', 4.5, 'Counseling and Support', 2, 'Need more counseling materials', NOW(), NOW()),
(10, 10, '2025-09-18', 3.0, 'Group Therapy Session', 1, 'Security restrictions limited session time', NOW(), NOW());

-- 🖨️ Sample Print Requisitions Data
INSERT INTO print_reqs (teacher_id, prison_id, module, copies, req_date, fulfil_date, status, pdf_url, created_at) VALUES
(1, 1, 'Who is God? Lesson 1', 25, '2025-09-15', '2025-09-16', 'DELIVERED', '/pdfs/print_001.pdf', NOW()),
(2, 3, 'Basic Bible Study Guide', 30, '2025-09-14', '2025-09-15', 'DELIVERED', '/pdfs/print_002.pdf', NOW()),
(3, 4, 'Life Transformation Workbook', 20, '2025-09-16', NULL, 'PENDING', NULL, NOW()),
(4, 5, 'Forgiveness Worksheets', 15, '2025-09-13', '2025-09-14', 'PRINTED', '/pdfs/print_004.pdf', NOW()),
(6, 2, 'Administration Guidelines', 10, '2025-09-12', '2025-09-12', 'DELIVERED', '/pdfs/print_005.pdf', NOW()),
(7, 7, 'Literacy Program Materials', 40, '2025-09-17', NULL, 'PENDING', NULL, NOW()),
(10, 9, 'Counseling Resources', 18, '2025-09-18', NULL, 'PENDING', NULL, NOW());

-- 🏫 Sample Classes Data
INSERT INTO classes (prison_id, title, start_date, end_date, time_slot, location_detail, created_at) VALUES
(1, 'Basic Christianity 101', '2025-09-01', '2025-12-15', 'Monday 10:00-12:00', 'Main Hall - Block A', NOW()),
(1, 'Advanced Bible Study', '2025-09-05', '2025-11-28', 'Friday 14:00-16:00', 'Library Room', NOW()),
(2, 'Life Skills Program', '2025-09-03', '2025-12-10', 'Wednesday 09:00-11:00', 'Education Center', NOW()),
(3, 'Literacy and Numeracy', '2025-09-02', NULL, 'Tuesday 13:00-15:00', 'Classroom 3', NOW()),
(4, 'Counseling and Support', '2025-09-04', '2025-11-20', 'Thursday 10:30-12:30', 'Therapy Room', NOW()),
(5, 'Vocational Training', '2025-09-08', '2025-12-12', 'Monday 14:00-16:00', 'Workshop Area', NOW());

-- 👨‍🎓 Sample Students Data
INSERT INTO students (class_id, prison_num, full_name, cell_block, dob, created_at) VALUES
(1, 'HC-001', 'Tendai Moyo', 'Block A', '1985-03-15', NOW()),
(1, 'HC-002', 'James Ncube', 'Block A', '1990-07-22', NOW()),
(1, 'HC-003', 'Robert Sibanda', 'Block B', '1988-11-30', NOW()),
(2, 'HC-015', 'Michael Zhou', 'Block C', '1982-05-18', NOW()),
(2, 'HC-016', 'Samuel Matanga', 'Block C', '1992-09-14', NOW()),
(3, 'CM-101', 'Andrew Dube', 'Maximum Security', '1978-12-08', NOW()),
(3, 'CM-102', 'Joseph Ndlovu', 'Maximum Security', '1985-06-25', NOW()),
(4, 'KM-201', 'Daniel Maphosa', 'Block 2', '1991-02-17', NOW()),
(4, 'KM-202', 'Thomas Moyo', 'Block 2', '1989-08-11', NOW()),
(4, 'KM-203', 'Blessing Nkomo', 'Block 3', '1993-04-03', NOW()),
(5, 'MT-301', 'Simon Chigumba', 'Main Block', '1987-10-19', NOW()),
(5, 'MT-302', 'Jonathan Sithole', 'Main Block', '1984-01-28', NOW()),
(6, 'GW-401', 'David Mbanje', 'Block D', '1990-07-07', NOW()),
(6, 'GW-402', 'Peter Makoni', 'Block D', '1986-09-23', NOW());

-- 📅 Sample Sessions Data
INSERT INTO sessions (class_id, date, topic, teacher_id, created_at) VALUES
(1, '2025-09-15', 'Introduction to Christianity', 1, NOW()),
(1, '2025-09-22', 'The Life of Jesus', 1, NOW()),
(1, '2025-09-29', 'The Teachings of Jesus', 4, NOW()),
(2, '2025-09-19', 'Old Testament Overview', 7, NOW()),
(2, '2025-09-26', 'New Testament Letters', 7, NOW()),
(3, '2025-09-17', 'Communication Skills', 3, NOW()),
(3, '2025-09-24', 'Conflict Resolution', 3, NOW()),
(4, '2025-09-16', 'Basic Reading', 2, NOW()),
(4, '2025-09-23', 'Basic Writing', 2, NOW()),
(5, '2025-09-18', 'Group Counseling', 10, NOW()),
(5, '2025-09-25', 'Individual Therapy', 10, NOW());

-- ✅ Sample Attendance Data
INSERT INTO attendances (session_id, student_id, present, notes, created_at) VALUES
(1, 1, true, 'Active participant', NOW()),
(1, 2, true, 'Asked good questions', NOW()),
(1, 3, false, 'Absent - medical reasons', NOW()),
(2, 1, true, 'Good engagement', NOW()),
(2, 2, true, 'Completed homework', NOW()),
(2, 3, true, 'Improved participation', NOW()),
(3, 4, true, 'Advanced student', NOW()),
(3, 5, true, 'Needs more practice', NOW()),
(4, 6, true, 'Enthusiastic learner', NOW()),
(4, 7, false, 'Security restriction', NOW()),
(5, 8, true, 'Making good progress', NOW()),
(5, 9, true, 'Needs extra help', NOW()),
(5, 10, true, 'Excellent improvement', NOW()),
(6, 11, true, 'Participated well', NOW()),
(6, 12, true, 'Good contribution', NOW()),
(7, 13, true, 'Vocational skills good', NOW()),
(7, 14, true, 'Shows aptitude', NOW());

-- 📝 Sample Audit Log Data
INSERT INTO audit_log (user_id, action, entity_type, entity_id, details, ip_address, timestamp) VALUES
(6, 'USER_LOGIN', 'User', 6, '{"username": "memory.chigumba", "status": "success"}', '192.168.1.100', NOW() - INTERVAL '2 hours'),
(6, 'TASK_REPORT_CREATE', 'TaskReport', 1, '{"teacher": "John Moyo", "prison": "Harare Central"}', '192.168.1.100', NOW() - INTERVAL '1 hour'),
(1, 'USER_LOGIN', 'User', 1, '{"username": "john.moyo", "status": "success"}', '192.168.1.101', NOW() - INTERVAL '3 hours'),
(3, 'PRINT_REQ_CREATE', 'PrintRequisition', 3, '{"module": "Life Transformation Workbook", "copies": 20}', '192.168.1.102', NOW() - INTERVAL '45 minutes'),
(4, 'ATTENDANCE_UPDATE', 'Attendance', 10, '{"session": "Group Counseling", "students": 5}', '192.168.1.103', NOW() - INTERVAL '30 minutes'),
(11, 'USER_LOGIN', 'User', 11, '{"username": "admin", "status": "success"}', '192.168.1.104', NOW() - INTERVAL '15 minutes');

-- 🔍 Useful Queries for Testing

-- Get all active teachers
SELECT * FROM teachers WHERE status = 'ACTIVE';

-- Get task reports for a specific teacher
SELECT tr.*, t.full_name as teacher_name, p.name as prison_name 
FROM task_reports tr
JOIN teachers t ON tr.teacher_id = t.id
JOIN prisons p ON tr.prison_id = p.id
WHERE t.id = 1;

-- Get pending print requisitions
SELECT pr.*, t.full_name as teacher_name, p.name as prison_name
FROM print_reqs pr
JOIN teachers t ON pr.teacher_id = t.id
JOIN prisons p ON pr.prison_id = p.id
WHERE pr.status = 'PENDING';

-- Get class attendance summary
SELECT c.title as class_name, s.date as session_date, s.topic, 
       COUNT(a.id) as total_students,
       SUM(CASE WHEN a.present THEN 1 ELSE 0 END) as present_count,
       ROUND((SUM(CASE WHEN a.present THEN 1 ELSE 0 END) * 100.0 / COUNT(a.id)), 2) as attendance_percentage
FROM sessions s
JOIN classes c ON s.class_id = c.id
LEFT JOIN attendances a ON s.id = a.session_id
GROUP BY c.title, s.date, s.topic
ORDER BY s.date DESC;

-- Get user login activity
SELECT u.username, al.action, al.timestamp, al.ip_address
FROM audit_log al
JOIN users u ON al.user_id = u.id
WHERE al.action = 'USER_LOGIN'
ORDER BY al.timestamp DESC
LIMIT 10;

-- 📊 Dashboard Statistics Queries

-- Total hours taught this month
SELECT SUM(hours) as total_hours_month
FROM task_reports
WHERE date >= DATE_TRUNC('month', CURRENT_DATE);

-- Total saved persons this month
SELECT SUM(saved_persons) as total_saved_month
FROM task_reports
WHERE date >= DATE_TRUNC('month', CURRENT_DATE);

-- Pending print jobs count
SELECT COUNT(*) as pending_prints
FROM print_reqs
WHERE status = 'PENDING';

-- Active classes count
SELECT COUNT(*) as active_classes
FROM classes
WHERE end_date IS NULL OR end_date >= CURRENT_DATE;

-- 🎯 Test User Credentials
-- Admin: username = 'admin', password = 'password123'
-- Teacher: username = 'john.moyo', password = 'password123'
-- Prison Liaison: username = 'thomas.chikowore', password = 'password123'

-- Note: In production, replace the example hashed passwords with actual BCrypt hashes
-- generated by Spring Security's password encoder.

-- To generate proper password hashes, use this Java code:
-- String encodedPassword = new BCryptPasswordEncoder().encode("password123");