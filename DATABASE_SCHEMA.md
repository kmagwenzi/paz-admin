# 🗃️ PAZ Admin Portal - Database Schema Design

## PostgreSQL Database Schema for Sprint 0

### Core Tables Implementation

```sql
-- Teachers table (User management)
CREATE TABLE teachers (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    mobile VARCHAR(20),
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Prisons table (Location management)
CREATE TABLE prisons (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    region VARCHAR(50) NOT NULL,
    capacity INTEGER,
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Task Reports table (Digital teaching task forms)
CREATE TABLE task_reports (
    id SERIAL PRIMARY KEY,
    teacher_id INTEGER NOT NULL REFERENCES teachers(id) ON DELETE CASCADE,
    prison_id INTEGER NOT NULL REFERENCES prisons(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    hours DECIMAL(4,2) NOT NULL CHECK (hours > 0),
    lesson_title VARCHAR(200) NOT NULL,
    saved_persons INTEGER DEFAULT 0 CHECK (saved_persons >= 0),
    issues TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Users table for authentication (extends teachers)
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    teacher_id INTEGER UNIQUE REFERENCES teachers(id) ON DELETE CASCADE,
    username VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'TEACHER', 'PRISON_LIAISON', 'SUPER_ADMIN')),
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Audit log table for security tracking
CREATE TABLE audit_log (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id INTEGER,
    details JSONB,
    ip_address VARCHAR(45),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Indexes for Performance Optimization

```sql
-- Indexes for frequently queried fields
CREATE INDEX idx_task_reports_teacher_id ON task_reports(teacher_id);
CREATE INDEX idx_task_reports_date ON task_reports(date);
CREATE INDEX idx_task_reports_prison_id ON task_reports(prison_id);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_teachers_email ON teachers(email);
CREATE INDEX idx_audit_log_timestamp ON audit_log(timestamp);
CREATE INDEX idx_audit_log_user_id ON audit_log(user_id);
```

### Foreign Key Constraints

```sql
-- Additional foreign key constraints for data integrity
ALTER TABLE task_reports
ADD CONSTRAINT fk_task_reports_teacher
FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE;

ALTER TABLE task_reports
ADD CONSTRAINT fk_task_reports_prison
FOREIGN KEY (prison_id) REFERENCES prisons(id) ON DELETE CASCADE;

ALTER TABLE users
ADD CONSTRAINT fk_users_teacher
FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE;
```

### Initial Data Seeding

```sql
-- Sample prisons data for Zimbabwe
INSERT INTO prisons (name, region, capacity, address) VALUES
('Harare Central Prison', 'Harare', 1200, 'Central Avenue, Harare'),
('Chikurubi Maximum Security Prison', 'Harare', 2000, 'Chikurubi, Harare'),
('Khami Prison', 'Bulawayo', 800, 'Khami, Bulawayo'),
('Mutare Prison', 'Manicaland', 600, 'Mutare, Manicaland');

-- Sample admin user (password will be hashed in application)
INSERT INTO teachers (full_name, email, mobile, status) VALUES
('System Administrator', 'admin@paz.org.zw', '+263772123456', 'ACTIVE');

INSERT INTO users (teacher_id, username, password_hash, role) VALUES
(1, 'admin', '$2a$10$examplehashedpassword', 'SUPER_ADMIN');
```

### Database Migration Strategy

**Using Flyway for Database Migrations:**

1. **V1__initial_schema.sql**: Contains all above CREATE TABLE statements
2. **V2__add_indexes.sql**: Adds performance indexes
3. **V3__seed_initial_data.sql**: Inserts sample data for development

**Migration File Structure:**
```
backend/src/main/resources/db/migration/
├── V1__initial_schema.sql
├── V2__add_indexes.sql
└── V3__seed_initial_data.sql
```

### Security Considerations

- All passwords must be hashed using BCrypt (Spring Security)
- Audit logging for all sensitive operations
- Row-level security considerations for multi-tenant data
- Regular database backups (to be implemented in production)

### Future Table Extensions (For Later Sprints)

```sql
-- Tables to be added in Sprint 1+
CREATE TABLE print_reqs (
    id SERIAL PRIMARY KEY,
    teacher_id INTEGER REFERENCES teachers(id),
    prison_id INTEGER REFERENCES prisons(id),
    module VARCHAR(200) NOT NULL,
    copies INTEGER NOT NULL CHECK (copies > 0),
    req_date DATE NOT NULL,
    fulfil_date DATE,
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'PRINTED', 'DELIVERED')),
    pdf_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE classes (
    id SERIAL PRIMARY KEY,
    prison_id INTEGER REFERENCES prisons(id),
    title VARCHAR(200) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    time_slot VARCHAR(50),
    location_detail TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Database Connection Configuration

**application.properties:**
```
spring.datasource.url=jdbc:postgresql://localhost:5432/paz_admin
spring.datasource.username=paz_admin
spring.datasource.password=your_secure_password
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

This schema provides the foundation for Sprint 0 implementation with focus on authentication, teacher management, and task reporting functionality.