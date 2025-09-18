# 🐳 Docker Setup and Deployment Guide

This guide provides comprehensive instructions for setting up and running the PAZ Admin Portal using Docker and Docker Compose.

## 📋 Prerequisites

### Docker Installation
- **Docker Engine** 20.10+ ([Install Docker](https://docs.docker.com/engine/install/))
- **Docker Compose** 2.0+ ([Install Docker Compose](https://docs.docker.com/compose/install/))

### Verify Installation
```bash
# Check Docker version
docker --version

# Check Docker Compose version
docker-compose --version

# Verify Docker is running
docker info
```

## 🏗️ Docker Compose Overview

The project includes a `docker-compose.yml` file that defines three services:

1. **postgres**: PostgreSQL 15 database
2. **backend**: Spring Boot application
3. **frontend**: Next.js application

### docker-compose.yml Structure
```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: paz_admin_db
      POSTGRES_USER: paz_admin
      POSTGRES_PASSWORD: paz_admin_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  backend:
    build: .
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/paz_admin_db
      SPRING_DATASOURCE_USERNAME: paz_admin
      SPRING_DATASOURCE_PASSWORD: paz_admin_password
    ports:
      - "8080:8080"
    depends_on:
      - postgres

  frontend:
    build: ./frontend
    environment:
      NEXT_PUBLIC_API_URL: http://backend:8080/api
    ports:
      - "3000:3000"
    depends_on:
      - backend
```

## 🚀 Quick Start

### 1. Clone and Navigate
```bash
git clone <repository-url>
cd paz-admin
```

### 2. Start All Services
```bash
# Start in detached mode (recommended)
docker-compose up -d

# Or start with logs visible
docker-compose up
```

### 3. Monitor Services
```bash
# Check service status
docker-compose ps

# View logs for all services
docker-compose logs

# View specific service logs
docker-compose logs backend
docker-compose logs frontend
docker-compose logs postgres

# Follow logs in real-time
docker-compose logs -f backend
```

### 4. Access Applications
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api
- **Database**: localhost:5432

## 🔧 Service Management

### Start/Stop Services
```bash
# Start specific service
docker-compose up -d postgres

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v

# Restart services
docker-compose restart

# Restart specific service
docker-compose restart backend
```

### Build and Rebuild
```bash
# Build images without cache
docker-compose build --no-cache

# Rebuild specific service
docker-compose build backend

# Force rebuild and restart
docker-compose up -d --build
```

## 🐳 Individual Container Management

### Backend Container
```bash
# Build backend image
docker build -t paz-admin-backend:latest .

# Run backend only
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/paz_admin_db \
  -e SPRING_DATASOURCE_USERNAME=paz_admin \
  -e SPRING_DATASOURCE_PASSWORD=paz_admin_password \
  paz-admin-backend:latest
```

### Frontend Container
```bash
# Build frontend image
cd frontend
docker build -t paz-admin-frontend:latest .

# Run frontend only
docker run -p 3000:3000 \
  -e NEXT_PUBLIC_API_URL=http://localhost:8080/api \
  paz-admin-frontend:latest
```

### Database Container
```bash
# Run PostgreSQL only
docker run --name paz-postgres \
  -e POSTGRES_DB=paz_admin_db \
  -e POSTGRES_USER=paz_admin \
  -e POSTGRES_PASSWORD=paz_admin_password \
  -p 5432:5432 \
  -v paz_postgres_data:/var/lib/postgresql/data \
  -d postgres:15
```

## ⚙️ Environment Configuration

### Custom Environment Variables
Create a `.env` file in the project root:
```env
# Database
POSTGRES_PASSWORD=your_secure_password
POSTGRES_USER=paz_admin
POSTGRES_DB=paz_admin_db

# Backend
SPRING_PROFILES_ACTIVE=dev
JWT_SECRET=your_jwt_secret_key

# Frontend
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

### Docker Compose with Custom Env File
```bash
# Use custom environment file
docker-compose --env-file .env up -d
```

## 📊 Volume Management

### List Volumes
```bash
# List all volumes
docker volume ls

# Inspect specific volume
docker volume inspect paz-admin_postgres_data

# Remove unused volumes
docker volume prune
```

### Backup Database Volume
```bash
# Create backup
docker run --rm -v paz-admin_postgres_data:/source -v $(pwd)/backups:/backup \
  alpine tar czf /backup/postgres_backup_$(date +%Y%m%d).tar.gz -C /source .

# Restore backup
docker run --rm -v paz-admin_postgres_data:/target -v $(pwd)/backups:/backup \
  alpine tar xzf /backup/postgres_backup_20250101.tar.gz -C /target
```

## 🧪 Testing with Docker

### Run Tests in Containers
```bash
# Run backend tests
docker-compose run backend ./gradlew test

# Run frontend tests
docker-compose run frontend npm test

# Run specific test class
docker-compose run backend ./gradlew test --tests "*AuthControllerTest"
```

### Database Access
```bash
# Access PostgreSQL console
docker-compose exec postgres psql -U paz_admin -d paz_admin_db

# Execute SQL file
docker-compose exec -T postgres psql -U paz_admin -d paz_admin_db < sample_data.sql

# Backup database
docker-compose exec postgres pg_dump -U paz_admin -d paz_admin_db > backup.sql
```

## 🔍 Debugging and Troubleshooting

### Container Inspection
```bash
# Inspect running container
docker inspect paz-admin-backend-1

# View container resources
docker stats

# Execute command in running container
docker-compose exec backend sh
docker-compose exec frontend sh

# View environment variables
docker-compose exec backend env
```

### Common Issues

**Port Conflicts**
```bash
# Check port usage
lsof -i :8080
lsof -i :3000
lsof -i :5432

# Change ports in docker-compose.yml
ports:
  - "8081:8080"  # Map host port 8081 to container port 8080
```

**Build Failures**
```bash
# Clear Docker cache
docker system prune -a

# Check build logs
docker-compose build --no-cache

# Verify Dockerfile syntax
docker build --no-cache -t test-image .
```

**Database Connection Issues**
```bash
# Check if database is ready
docker-compose exec postgres pg_isready -U paz_admin -d paz_admin_db

# Check database logs
docker-compose logs postgres

# Test connection from backend
docker-compose exec backend curl http://postgres:5432
```

## 🚀 Production Deployment

### Docker Compose for Production
```yaml
# docker-compose.prod.yml
version: '3.8'
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    restart: unless-stopped

  backend:
    build: .
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/paz_admin_db
      SPRING_DATASOURCE_USERNAME: paz_admin
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
    ports:
      - "8080:8080"
    depends_on:
      - postgres
    restart: unless-stopped

  frontend:
    build: ./frontend
    environment:
      NEXT_PUBLIC_API_URL: https://api.yourdomain.com
    ports:
      - "3000:3000"
    restart: unless-stopped
```

### Deployment Commands
```bash
# Production deployment
docker-compose -f docker-compose.prod.yml up -d

# With environment file
docker-compose -f docker-compose.prod.yml --env-file .env.prod up -d
```

### Security Best Practices
```bash
# Use Docker secrets for sensitive data
echo "your_secret_password" | docker secret create db_password -

# Use read-only root filesystem
read_only: true

# Use non-root users
user: "1000:1000"
```

## 📝 Maintenance and Updates

### Update Containers
```bash
# Pull latest images
docker-compose pull

# Rebuild and restart
docker-compose up -d --build

# Update specific service
docker-compose pull postgres
docker-compose up -d postgres
```

### Cleanup
```bash
# Remove stopped containers
docker container prune

# Remove unused images
docker image prune

# Remove unused volumes
docker volume prune

# Full system cleanup
docker system prune -a
```

## 🆘 Getting Help

### Debug Commands
```bash
# Check container health
docker-compose ps --services --filter "status=running"

# View resource usage
docker-compose top

# Check network connectivity
docker-compose exec backend ping postgres

# View application logs
docker-compose logs --tail=100 backend
```

### Common Solutions
- **Port busy**: Change ports in docker-compose.yml or stop conflicting services
- **Build fails**: Check Dockerfile syntax and dependencies
- **Database not ready**: Add health checks or increase depends_on timeout
- **Memory issues**: Increase Docker memory allocation in settings

---

*For additional Docker documentation, visit [Docker Docs](https://docs.docker.com/)*