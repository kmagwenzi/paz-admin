# 🛠️ Manual Setup Guide (Without Docker)

This guide provides step-by-step instructions for setting up and running the PAZ Admin Portal without Docker, using local installations of all required software.

## 📋 Prerequisites

### Required Software
- **Java 21** or later ([Download JDK](https://adoptium.net/))
- **Node.js 18+** and npm ([Download Node.js](https://nodejs.org/))
- **PostgreSQL 15** ([Download PostgreSQL](https://www.postgresql.org/download/))
- **Git** for version control

### Verify Installations
```bash
# Check Java version
java -version

# Check Node.js version
node --version
npm --version

# Check PostgreSQL version
psql --version

# Check Git version
git --version
```

## 🗄️ PostgreSQL Manual Setup

### 1. Install PostgreSQL
Follow the official installation guide for your operating system:
- **Ubuntu/Debian**: `sudo apt-get install postgresql-15 postgresql-contrib-15`
- **macOS**: `brew install postgresql@15`
- **Windows**: Download from [postgresql.org](https://www.postgresql.org/download/windows/)

### 2. Start PostgreSQL Service
```bash
# Ubuntu/Debian
sudo systemctl start postgresql
sudo systemctl enable postgresql

# macOS
brew services start postgresql@15

# Windows
# PostgreSQL service should start automatically after installation
```

### 3. Create Database and User
```bash
# Switch to postgres user
sudo -u postgres psql

# Execute SQL commands
CREATE DATABASE paz_admin_db;
CREATE USER paz_admin WITH PASSWORD 'paz_admin_password';
GRANT ALL PRIVILEGES ON DATABASE paz_admin_db TO paz_admin;

# Exit psql
\q
```

### 4. Configure PostgreSQL for Remote Connections (Optional)
Edit `/etc/postgresql/15/main/postgresql.conf`:
```ini
listen_addresses = '*'
```

Edit `/etc/postgresql/15/main/pg_hba.conf`:
```ini
# Allow connections from any IP address
host    all             all             0.0.0.0/0               md5
```

Reload configuration:
```bash
sudo systemctl reload postgresql
```

## 🏗️ Backend Setup (Spring Boot)

### 1. Navigate to Backend Directory
```bash
cd paz-admin
```

### 2. Configure Database Connection
Edit `src/main/resources/application.properties`:
```properties
# Database configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/paz_admin_db
spring.datasource.username=paz_admin
spring.datasource.password=paz_admin_password

# JPA settings
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.show-sql=true

# Flyway migration
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration

# JWT settings (for development)
jwt.secret=dev-jwt-secret-key-change-in-production
jwt.expiration=86400000
jwt.refreshExpiration=604800000
```

### 3. Build the Backend
```bash
# Using Gradle Wrapper (recommended)
./gradlew build

# Or using installed Gradle
gradle build
```

### 4. Run the Backend
```bash
# Development mode with auto-reload
./gradlew bootRun

# Or run the built JAR
java -jar build/libs/*.jar
```

### 5. Verify Backend is Running
```bash
# Check health endpoint
curl http://localhost:8080/actuator/health

# Should return: {"status":"UP"}
```

## ⚛️ Frontend Setup (Next.js)

### 1. Navigate to Frontend Directory
```bash
cd frontend
```

### 2. Install Dependencies
```bash
npm install
```

### 3. Configure Environment Variables
Create `frontend/.env.local`:
```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api
API_URL=http://localhost:8080/api
NODE_ENV=development
```

### 4. Build and Run the Frontend
```bash
# Development mode with hot reload
npm run dev

# Or build for production and run
npm run build
npm start
```

### 5. Verify Frontend is Running
Open http://localhost:3000 in your browser. You should see the PAZ Admin Portal login page.

## 📊 Load Sample Data

### 1. Apply Database Migrations
The Spring Boot application will automatically apply Flyway migrations on first run. Verify tables are created:

```bash
psql -h localhost -U paz_admin -d paz_admin_db -c "\dt"
```

### 2. Load Sample Data
```bash
# Load the sample data SQL file
psql -h localhost -U paz_admin -d paz_admin_db < build-run-instructions/sample_data.sql
```

### 3. Verify Data Load
```bash
# Check if data was loaded successfully
psql -h localhost -U paz_admin -d paz_admin_db -c "SELECT COUNT(*) FROM teachers;"
psql -h localhost -U paz_admin -d paz_admin_db -c "SELECT COUNT(*) FROM task_reports;"
```

## 🔧 Development Workflow

### Running in Development Mode
```bash
# Terminal 1: Start PostgreSQL (if not running as service)
sudo systemctl start postgresql

# Terminal 2: Start Backend with auto-reload
cd paz-admin
./gradlew bootRun

# Terminal 3: Start Frontend with hot reload
cd frontend
npm run dev
```

### Useful Development Commands
```bash
# Run backend tests
./gradlew test

# Run frontend tests
cd frontend && npm test

# Check code style
./gradlew checkstyleMain

# Build production artifacts
./gradlew build -x test
cd frontend && npm run build
```

## ⚙️ Configuration Files

### Backend Configuration (`application.properties`)
```properties
# Server port
server.port=8080

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/paz_admin_db
spring.datasource.username=paz_admin
spring.datasource.password=paz_admin_password

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true

# Flyway
spring.flyway.enabled=true

# Logging
logging.level.zw.org.paz=DEBUG
```

### Frontend Configuration (`next.config.js`)
```javascript
/** @type {import('next').NextConfig} */
const nextConfig = {
  experimental: {
    appDir: true,
  },
  env: {
    API_URL: process.env.API_URL || 'http://localhost:8080/api',
  },
}

module.exports = nextConfig
```

## 🧪 Testing the Setup

### 1. Test Database Connection
```bash
psql -h localhost -U paz_admin -d paz_admin_db -c "SELECT version();"
```

### 2. Test Backend API
```bash
# Health check
curl http://localhost:8080/actuator/health

# Test authentication
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}'
```

### 3. Test Frontend
1. Open http://localhost:3000
2. Try logging in with:
   - Username: `admin`
   - Password: `password123`
3. Verify you can access different pages

### 4. Test Sample Data
```bash
# Check sample data was loaded
psql -h localhost -U paz_admin -d paz_admin_db -c "SELECT * FROM teachers LIMIT 5;"
psql -h localhost -U paz_admin -d paz_admin_db -c "SELECT * FROM task_reports LIMIT 5;"
```

## 🐛 Troubleshooting

### Common Issues and Solutions

**Database Connection Issues**
```bash
# Check if PostgreSQL is running
sudo systemctl status postgresql

# Check if port 5432 is listening
netstat -tulpn | grep 5432

# Test connection
telnet localhost 5432
```

**Port Conflicts**
```bash
# Check what's using port 8080
lsof -i :8080

# Check what's using port 3000
lsof -i :3000

# Change ports in application.properties if needed
server.port=8081
```

**Build Failures**
```bash
# Clean and rebuild
./gradlew clean build

# Frontend build issues
cd frontend && rm -rf node_modules package-lock.json && npm install
```

**Authentication Issues**
- Verify the sample data was loaded correctly
- Check the password hashes in the sample_data.sql file
- Ensure JWT secret is configured properly

### Logs and Debugging

**Backend Logs**
```bash
# View Spring Boot logs
tail -f build/logs/application.log

# Or enable debug logging
./gradlew bootRun --debug
```

**Frontend Logs**
- Check browser developer console for errors
- View Next.js logs in the terminal where `npm run dev` is running

**Database Logs**
```bash
# PostgreSQL logs location
# Ubuntu/Debian: /var/log/postgresql/postgresql-15-main.log
# macOS: /usr/local/var/log/postgresql@15.log
# Windows: C:\Program Files\PostgreSQL\15\data\log

tail -f /var/log/postgresql/postgresql-15-main.log
```

## 📝 Maintenance and Updates

### Database Backups
```bash
# Create backup
pg_dump -h localhost -U paz_admin -d paz_admin_db > backup_$(date +%Y%m%d).sql

# Restore backup
psql -h localhost -U paz_admin -d paz_admin_db < backup.sql
```

### Application Updates
```bash
# Pull latest code
git pull origin main

# Update backend dependencies
./gradlew build

# Update frontend dependencies
cd frontend && npm install

# Restart applications
# Stop and restart ./gradlew bootRun and npm run dev
```

### Cleanup
```bash
# Remove build artifacts
./gradlew clean
cd frontend && npm run clean

# Remove node_modules (if needed)
rm -rf frontend/node_modules
```

## 🚀 Production Considerations

### Environment Configuration
Create `application-prod.properties`:
```properties
# Production database
spring.datasource.url=jdbc:postgresql://production-db:5432/paz_admin_db
spring.datasource.username=prod_user
spring.datasource.password=prod_password

# JWT secret (generate a strong secret)
jwt.secret=your-strong-production-jwt-secret

# Disable debug logging
logging.level.zw.org.paz=INFO
```

### Build Production Artifacts
```bash
# Backend
./gradlew build -x test

# Frontend
cd frontend && npm run build
```

### Run in Production
```bash
# Backend
java -jar build/libs/*.jar --spring.profiles.active=prod

# Frontend
cd frontend && npm start
```

## 📞 Support

If you encounter issues:
1. Check the troubleshooting section above
2. Verify all prerequisites are installed correctly
3. Check application logs for error messages
4. Consult the main project documentation
5. Create an issue in the project repository

---

*For additional support, refer to the main README.md or contact the development team.*