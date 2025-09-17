# 🏗️ PAZ Admin Portal - Architecture Implementation Plan

## Project Structure Setup Guide

### Required Directory Structure
```
paz-admin/
├── backend/
│   ├── src/main/java/zw/org/paz/
│   │   ├── PazAdminApplication.java
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── repository/
│   │   ├── service/
│   │   └── security/
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── application-dev.properties
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── pages/
│   │   ├── index.js
│   │   ├── login.js
│   │   ├── tasks/
│   │   ├── prints/
│   │   └── classes/
│   ├── components/
│   │   ├── ui/
│   │   │   ├── ZimDatePicker.js
│   │   │   ├── PrintBtn.js
│   │   │   ├── LoadShedBanner.js
│   │   │   └── EcoCashTotal.js
│   │   └── layout/
│   ├── styles/
│   ├── next.config.js
│   ├── package.json
│   └── Dockerfile
├── docker-compose.yml
├── .env.example
└── README.md
```

### Database Schema Implementation

```sql
-- Core Tables for Sprint 0
CREATE TABLE teachers (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    mobile VARCHAR(20),
    status VARCHAR(20) DEFAULT 'ACTIVE'
);

CREATE TABLE prisons (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    region VARCHAR(50),
    capacity INTEGER,
    address TEXT
);

CREATE TABLE task_reports (
    id SERIAL PRIMARY KEY,
    teacher_id INTEGER REFERENCES teachers(id),
    prison_id INTEGER REFERENCES prisons(id),
    date DATE NOT NULL,
    hours DECIMAL(4,2) NOT NULL,
    lesson_title VARCHAR(200) NOT NULL,
    saved_persons INTEGER DEFAULT 0,
    issues TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Additional tables to be implemented in later sprints
```

### Spring Boot Security Configuration

**Key Security Components:**
- JWT Authentication Filter
- Password Encoder (BCrypt)
- UserDetailsService implementation
- CORS configuration for localhost:3000 and vercel.app
- Role-based access control (Admin, Teacher, Prison Liaison, Super-Admin)

### Next.js Routing Structure

```javascript
// pages/index.js → Control Panel
// pages/login.js → JWT authentication
// pages/tasks/new.js → Teachers Task Form
// pages/tasks/[id].js → Task Read-only view
// pages/prints/new.js → Print Requisition Form
// pages/prints/[id].js → Print Preview
// pages/classes/index.js → List classes
// pages/classes/new.js → Create class
// pages/classes/[id].js → Class details and attendance
```

### Environment Variables Configuration

**.env.example:**
```
# Database
DB_URL=jdbc:postgresql://localhost:5432/paz_admin
DB_USERNAME=paz_admin
DB_PASSWORD=your_password_here

# JWT
JWT_SECRET=your_jwt_secret_here
JWT_EXPIRATION=900000
JWT_REFRESH_EXPIRATION=604800000

# Frontend
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

### Docker Compose Setup

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: paz_admin
      POSTGRES_USER: paz_admin
      POSTGRES_PASSWORD: paz_admin_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=dev
    depends_on:
      - postgres

volumes:
  postgres_data:
```

### Implementation Priority Order

1. **Database Setup**: PostgreSQL with initial tables
2. **Spring Boot Foundation**: Basic app structure + security
3. **JWT Authentication**: Login/refresh token flow
4. **Next.js Setup**: Basic routing and API integration
5. **Docker Configuration**: Local development environment
6. **Basic CRUD**: Teachers and Prisons management
7. **Task Reports**: Digital teaching task form
8. **Print Requisitions**: PDF generation workflow

### Testing Strategy

- **Unit Tests**: JUnit for backend services
- **Integration Tests**: Spring Boot Test with Testcontainers
- **Frontend Tests**: Jest + React Testing Library
- **API Testing**: Postman collection for endpoints
- **Performance**: Load testing for PDF generation (<5s for 30 inmates)

### Next Steps for Implementation Team

1. Switch to Code mode to create the actual project structure
2. Implement the database schema using Flyway or Liquibase
3. Set up Spring Security with JWT authentication
4. Create Next.js app with the specified routing
5. Configure Docker for local development
6. Implement basic CRUD operations for core entities