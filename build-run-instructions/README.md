# 🚀 PAZ Admin Portal - Build and Run Instructions

This comprehensive guide will help you set up, build, and run the PAZ Admin Portal project locally for development and testing.

## 📋 Prerequisites

### System Requirements

- **Operating System**: Linux, macOS, or Windows 10/11
- **Memory**: Minimum 8GB RAM (16GB recommended)
- **Storage**: At least 2GB free space
- **Network**: Internet connection for dependencies

### Required Software

- **Java 21** or later ([Download JDK](https://adoptium.net/))
- **Node.js 18+** and npm ([Download Node.js](https://nodejs.org/))
- **PostgreSQL 15** ([Download PostgreSQL](https://www.postgresql.org/download/))
- **Docker** and **Docker Compose** (optional, for containerized setup)
- **Git** for version control

### Verify Installations

```bash
# Check Java version
java -version

# Check Node.js version
node --version
npm --version

# Check Docker version
docker --version
docker-compose --version

# Check PostgreSQL (if installed locally)
psql --version
```

## 🏗️ Project Structure Overview

```
paz-admin/
├── backend/                 # Spring Boot application
│   ├── src/main/java/      # Java source code
│   ├── build.gradle        # Gradle build configuration
│   └── Dockerfile          # Backend container definition
├── frontend/               # Next.js application
│   ├── app/                # Next.js app router
│   ├── components/         # React components
│   └── package.json        # Node.js dependencies
├── docker-compose.yml      # Multi-container setup
├── deploy-backend.sh       # Backend deployment script
└── build-run-instructions/ # This documentation
```

## 🐳 Quick Start with Docker Compose (Recommended)

### 1. Clone and Navigate

```bash
git clone git@github.com:kmagwenzi/paz-admin.git
cd paz-admin
```

### 2. Start All Services

```bash
docker-compose up -d
```

This command will start:

- **PostgreSQL** database on port 5433
- **Spring Boot Backend** on port 8080
- **Next.js Frontend** on port 3000

### 3. Verify Services

```bash
# Check running containers
docker-compose ps

# View logs
docker-compose logs backend
docker-compose logs frontend
docker-compose logs postgres
```

### 4. Access the Application

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api
- **API Health**: http://localhost:8080/actuator/health
- **Database**: localhost:5433 (username: paz_admin, password: paz_admin_password)

## 🗄️ Manual PostgreSQL Setup

### Option 1: Using Docker (Recommended for Development)

```bash
# Start PostgreSQL container
docker run --name paz-postgres \
  -e POSTGRES_DB=paz_admin_db \
  -e POSTGRES_USER=paz_admin \
  -e POSTGRES_PASSWORD=paz_admin_password \
  -p 5432:5432 \
  -d postgres:15

# Verify connection
docker exec -it paz-postgres psql -U paz_admin -d paz_admin_db
```

### Option 2: Local PostgreSQL Installation

1. Install PostgreSQL 15 on your system
2. Create database and user:

```sql
CREATE DATABASE paz_admin_db;
CREATE USER paz_admin WITH PASSWORD 'paz_admin_password';
GRANT ALL PRIVILEGES ON DATABASE paz_admin_db TO paz_admin;
```

### Option 3: Using Docker Compose (Database Only)

```bash
docker-compose up -d postgres
```

## 🛠️ Manual Build and Run (Without Docker)

### Backend Setup (Spring Boot)

#### 1. Configure Database Connection

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/paz_admin_db
spring.datasource.username=paz_admin
spring.datasource.password=paz_admin_password
spring.jpa.hibernate.ddl-auto=validate
```

#### 2. Build and Run Backend

```bash
# Using Gradle Wrapper
./gradlew build
./gradlew bootRun

# Or using installed Gradle
gradle build
gradle bootRun
```

#### 3. Verify Backend

```bash
curl http://localhost:8080/actuator/health
# Should return: {"status":"UP"}
```

### Frontend Setup (Next.js)

#### 1. Install Dependencies

```bash
cd frontend
npm install
```

#### 2. Configure Environment

Create `frontend/.env.local`:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api
API_URL=http://localhost:8080/api
NODE_ENV=development
```

#### 3. Run Development Server

```bash
npm run dev
# or
npm run build && npm start
```

#### 4. Verify Frontend

Open http://localhost:3000 in your browser

## 📊 Database Initialization and Sample Data

### Automatic Migration with Flyway

The Spring Boot application uses Flyway for database migrations. On first run, it will automatically:

- Create all required tables
- Apply indexes and constraints
- Seed initial data

### Manual Database Setup (If Needed)

```bash
# Connect to PostgreSQL
psql -h localhost -U paz_admin -d paz_admin_db

# Or using Docker
docker exec -it paz-postgres psql -U paz_admin -d paz_admin_db
```

## 🔧 Environment Configuration

### Backend Environment Variables

Create `application-dev.properties` for development:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/paz_admin_db
spring.datasource.username=paz_admin
spring.datasource.password=paz_admin_password

# JWT
jwt.secret=dev-jwt-secret-key-change-in-production
jwt.expiration=86400000
jwt.refreshExpiration=604800000

# Logging
logging.level.zw.org.paz=DEBUG
```

### Frontend Environment Variables

Create `frontend/.env.local`:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api
API_URL=http://localhost:8080/api
NODE_ENV=development
```

## 🧪 Testing the Setup

### 1. Verify Database Connection

```bash
# Using psql
psql -h localhost -U paz_admin -d paz_admin_db -c "SELECT version();"

# Using curl to test API
curl http://localhost:8080/actuator/health
```

### 2. Test Authentication

```bash
# Test login endpoint with admin user
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}'

# Test login endpoint with teacher user
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username":"john.moyo","password":"password123"}'

# Test login endpoint with test user
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

### 3. Test Frontend-Backend Connection

Open http://localhost:3000 and verify:

- Page loads without errors
- Login functionality works
- API calls succeed (check browser developer tools)

## 🐛 Troubleshooting Common Issues

### Port Conflicts

```bash
# Check what's using port 8080
lsof -i :8080

# Check what's using port 3000
lsof -i :3000

# Check what's using port 5432
lsof -i :5432
```

### Database Connection Issues

```bash
# Verify PostgreSQL is running
sudo systemctl status postgresql

# Check connection from application
telnet localhost 5432
```

### Build Issues

```bash
# Clean and rebuild
./gradlew clean build

# Frontend build issues
cd frontend && npm ci && npm run build
```

### Docker Issues

```bash
# Check Docker status
docker system info

# Restart Docker service
sudo systemctl restart docker

# Remove unused containers and images
docker system prune -a
```

## 🚀 Deployment Notes

### Production Environment Variables

For production deployment, set these environment variables:

**Backend:**

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://your-production-db:5432/paz_admin_db
export SPRING_DATASOURCE_USERNAME=production_user
export SPRING_DATASOURCE_PASSWORD=production_password
export PAZ_APP_JWTSECRET=strong-production-jwt-secret
```

**Frontend:**

```env
NEXT_PUBLIC_API_URL=https://your-api-domain.com/api
API_URL=https://your-api-domain.com/api
NODE_ENV=production
```

### Health Check Endpoints

- Backend Health: http://localhost:8080/actuator/health
- Frontend Health: http://localhost:3000 (should load without errors)

## 📝 Next Steps After Setup

1. **Login**: Use admin credentials (username: admin, password: password123) or teacher user (username: john.moyo, password: password123) or test user (username: testuser, password: password123)
2. **Explore Features**: Test task reports, print requisitions, and class management
3. **Review Documentation**: Check the main README.md for feature overview
4. **Development**: Start implementing new features or fixing issues

## 🆘 Getting Help

If you encounter issues:

1. Check the troubleshooting section above
2. Review application logs in the terminal
3. Check Docker container logs
4. Consult the main project documentation
5. Create an issue in the project repository

---

*Last Updated: 2025-09-22*
*For additional support, refer to the main project documentation or contact the development team.*
