# 🚀 PAZ Admin Portal - Deployment Guide

This guide covers the deployment process for the PAZ Admin Portal, including local development, production deployment, and environment configuration.

## 📋 Prerequisites

- **Docker** and **Docker Compose** installed on your system
- **Java 21** (for local backend development)
- **Node.js 18+** (for local frontend development)
- **PostgreSQL** (optional, for local development without Docker)

## 🏗️ Project Structure

```
paz-admin/
├── Dockerfile                 # Backend Docker configuration
├── docker-compose.yml         # Multi-container setup
├── deploy-backend.sh          # Backend deployment script
├── src/main/resources/
│   ├── application.properties # Production configuration
│   └── application-dev.properties # Development configuration
├── frontend/
│   ├── Dockerfile            # Frontend Docker configuration
│   ├── deploy-frontend.sh    # Frontend deployment script
│   ├── .env.example          # Environment variables template
│   └── .env.local            # Local development environment
└── DEPLOYMENT.md            # This file
```

## 🚀 Quick Start with Docker Compose

### 1. Clone and Setup
```bash
git clone <repository-url>
cd paz-admin
```

### 2. Start All Services
```bash
docker-compose up -d
```

This will start:
- **PostgreSQL** on port 5432
- **Spring Boot Backend** on port 8080
- **Next.js Frontend** on port 3000

### 3. Access the Application
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api
- API Health: http://localhost:8080/actuator/health

## 🔧 Individual Service Deployment

### Backend Only
```bash
# Using the deployment script
./deploy-backend.sh

# Or manually with Docker
docker build -t paz-admin-backend:latest .
docker run -p 8080:8080 paz-admin-backend:latest
```

### Frontend Only
```bash
# Using the deployment script
cd frontend
./deploy-frontend.sh

# Or manually with Docker
cd frontend
docker build -t paz-admin-frontend:latest .
docker run -p 3000:3000 paz-admin-frontend:latest
```

## 🌳 Environment Configuration

### Backend Environments

**Production (application.properties)**
- Database: PostgreSQL with production credentials
- JWT: Production secret key
- Flyway: Enabled for database migrations

**Development (application-dev.properties)**
- Database: Development PostgreSQL instance
- JWT: Development secret key
- Enhanced logging: DEBUG level for development

### Frontend Environments

**Production (.env.production)**
```env
NEXT_PUBLIC_API_URL=http://your-production-domain:8080/api
API_URL=http://your-production-domain:8080/api
NODE_ENV=production
```

**Development (.env.local)**
```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api
API_URL=http://localhost:8080/api
NODE_ENV=development
```

## 📊 Database Setup

### Using Docker Compose
The `docker-compose.yml` includes a PostgreSQL container with pre-configured:
- Database: `paz_admin_db`
- User: `paz_admin`
- Password: `paz_admin_password`

### Manual PostgreSQL Setup
```sql
CREATE DATABASE paz_admin_db;
CREATE USER paz_admin WITH PASSWORD 'paz_admin_password';
GRANT ALL PRIVILEGES ON DATABASE paz_admin_db TO paz_admin;
```

## 🔒 Security Considerations

### JWT Secrets
- **Development**: Use the provided dev secret in `application-dev.properties`
- **Production**: Generate a strong, unique secret and set via environment variable:
  ```bash
  export PAZ_APP_JWTSECRET=your-strong-secret-key-here
  ```

### Environment Variables
Never commit sensitive data to version control. Use:
- `.env.local` for local development
- Docker environment variables for production
- Secret management systems (AWS SM, HashiCorp Vault) for production

## 🧪 Testing Deployment

### Backend Tests
```bash
./gradlew test
```

### Frontend Tests
```bash
cd frontend
npm test
```

### Integration Tests
Run both backend and frontend, then test API endpoints:
```bash
# Test authentication
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}'
```

## 📈 Monitoring and Logs

### View Docker Logs
```bash
# Backend logs
docker-compose logs backend

# Frontend logs
docker-compose logs frontend

# Database logs
docker-compose logs postgres
```

### Health Checks
- Backend: http://localhost:8080/actuator/health
- Frontend: Check browser console for errors

## 🚨 Troubleshooting

### Common Issues

1. **Port Conflicts**
   ```bash
   # Check running containers
   docker ps
   
   # Stop conflicting containers
   docker stop <container-name>
   ```

2. **Database Connection Issues**
   - Verify PostgreSQL is running
   - Check credentials in application.properties

3. **Build Failures**
   ```bash
   # Clean and rebuild
   ./gradlew clean build
   
   # Frontend build
   cd frontend && npm run build
   ```

### Support
For deployment issues, check:
- Docker logs for error messages
- Application logs in `/app/logs` (backend)
- Browser developer tools (frontend)

## 🎯 Production Deployment Checklist

- [ ] Set strong JWT secrets via environment variables
- [ ] Configure production database credentials
- [ ] Enable SSL/HTTPS for frontend and backend
- [ ] Set up proper firewall rules
- [ ] Configure backup strategies for database
- [ ] Set up monitoring and alerting
- [ ] Test all critical user flows
- [ ] Verify security headers are properly set

## 📝 Version History

- **v1.0.0** (2025-09-18): Initial deployment setup with Docker Compose
- Includes backend Spring Boot API, frontend Next.js app, and PostgreSQL database

---

For additional support, refer to the main [README.md](README.md) or create an issue in the project repository.