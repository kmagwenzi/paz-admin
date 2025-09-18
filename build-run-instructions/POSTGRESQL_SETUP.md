# 🗄️ PostgreSQL Database Setup Guide

This guide provides comprehensive instructions for setting up PostgreSQL for the PAZ Admin Portal, including Docker and manual installation methods.

## 📋 Prerequisites

### System Requirements
- **PostgreSQL 15** or later
- **Minimum RAM**: 2GB (4GB recommended)
- **Storage**: At least 1GB free space
- **Network**: Port 5432 accessible

### Installation Methods
1. **Docker** (Recommended for development)
2. **Local Installation** (For production-like environments)
3. **Cloud Databases** (AWS RDS, Azure Database, etc.)

## 🐳 Docker Setup (Recommended)

### Quick Start with Docker Compose
The project includes a `docker-compose.yml` file that sets up PostgreSQL automatically:

```bash
# Start only PostgreSQL
docker-compose up -d postgres

# Or start all services
docker-compose up -d
```

### Manual Docker Setup
```bash
# Pull PostgreSQL image
docker pull postgres:15

# Run PostgreSQL container
docker run --name paz-postgres \
  -e POSTGRES_DB=paz_admin_db \
  -e POSTGRES_USER=paz_admin \
  -e POSTGRES_PASSWORD=paz_admin_password \
  -p 5432:5432 \
  -v paz_postgres_data:/var/lib/postgresql/data \
  -d postgres:15

# Verify container is running
docker ps

# Check logs
docker logs paz-postgres
```

### Environment Variables for Docker
| Variable | Description | Default Value |
|----------|-------------|---------------|
| `POSTGRES_DB` | Database name | `paz_admin_db` |
| `POSTGRES_USER` | Database user | `paz_admin` |
| `POSTGRES_PASSWORD` | User password | `paz_admin_password` |
| `POSTGRES_PORT` | Port number | `5432` |
| `PGDATA` | Data directory | `/var/lib/postgresql/data` |

## 💻 Local Installation

### Ubuntu/Debian
```bash
# Add PostgreSQL repository
sudo sh -c 'echo "deb http://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list'

# Import repository signing key
wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | sudo apt-key add -

# Update package list
sudo apt-get update

# Install PostgreSQL
sudo apt-get install postgresql-15 postgresql-contrib-15

# Start PostgreSQL service
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

### macOS with Homebrew
```bash
# Install PostgreSQL
brew install postgresql@15

# Start PostgreSQL service
brew services start postgresql@15

# Or run manually
pg_ctl -D /usr/local/var/postgres start
```

### Windows
1. Download installer from [PostgreSQL Official Website](https://www.postgresql.org/download/windows/)
2. Run the installer and follow prompts
3. Use pgAdmin or psql for database management

## 🔧 Database Configuration

### Create Database and User
```sql
-- Connect to PostgreSQL as superuser
psql -U postgres

-- Create database
CREATE DATABASE paz_admin_db;

-- Create user with password
CREATE USER paz_admin WITH PASSWORD 'paz_admin_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE paz_admin_db TO paz_admin;

-- Grant schema privileges
\c paz_admin_db
GRANT ALL ON SCHEMA public TO paz_admin;
```

### Configuration Files
**postgresql.conf** (Main configuration):
```ini
# Listen on all interfaces
listen_addresses = '*'

# Increase maximum connections
max_connections = 100

# Memory settings
shared_buffers = 128MB
work_mem = 4MB
maintenance_work_mem = 64MB
```

**pg_hba.conf** (Client authentication):
```ini
# Allow local connections
local   all             all                                     trust

# Allow IPv4 connections from any host
host    all             all             0.0.0.0/0               md5

# Allow IPv6 connections
host    all             all             ::/0                    md5
```

### Reload Configuration
```bash
# Reload PostgreSQL configuration
sudo systemctl reload postgresql

# Or using psql
SELECT pg_reload_conf();
```

## 🔍 Verification and Testing

### Test Connection
```bash
# Test connection with psql
psql -h localhost -U paz_admin -d paz_admin_db

# Test connection with telnet
telnet localhost 5432

# Test connection from application
./gradlew bootRun  # Should connect successfully
```

### Check Database Status
```sql
-- Check database size
SELECT pg_size_pretty(pg_database_size('paz_admin_db'));

-- List all databases
\l

-- List all users
\du

-- Show active connections
SELECT * FROM pg_stat_activity;
```

## ⚙️ Performance Tuning

### Basic Tuning for Development
```sql
-- Increase work_mem for better sorting performance
ALTER SYSTEM SET work_mem = '16MB';

-- Enable parallel queries
ALTER SYSTEM SET max_parallel_workers = 4;
ALTER SYSTEM SET max_parallel_workers_per_gather = 2;

-- Set effective cache size
ALTER SYSTEM SET effective_cache_size = '1GB';
```

### Maintenance Operations
```bash
# Vacuum database (recommended weekly)
psql -U paz_admin -d paz_admin_db -c "VACUUM ANALYZE;"

# Backup database
pg_dump -U paz_admin -d paz_admin_db > backup.sql

# Restore database
psql -U paz_admin -d paz_admin_db < backup.sql
```

## 🛡️ Security Considerations

### Change Default Password
```sql
-- Change postgres user password
ALTER USER postgres WITH PASSWORD 'new_strong_password';

-- Change application user password
ALTER USER paz_admin WITH PASSWORD 'new_strong_password';
```

### SSL Configuration (Production)
```ini
# In postgresql.conf
ssl = on
ssl_cert_file = '/etc/ssl/certs/ssl-cert-snakeoil.pem'
ssl_key_file = '/etc/ssl/private/ssl-cert-snakeoil.key'

# In pg_hba.conf
hostssl all all 0.0.0.0/0 md5
```

### Firewall Configuration
```bash
# Allow PostgreSQL port
sudo ufw allow 5432/tcp

# Or specific IP ranges
sudo ufw allow from 192.168.1.0/24 to any port 5432
```

## 🐛 Troubleshooting

### Common Issues and Solutions

**Connection Refused**
```bash
# Check if PostgreSQL is running
sudo systemctl status postgresql

# Check port listening
netstat -tulpn | grep 5432

# Check firewall rules
sudo ufw status
```

**Authentication Failed**
```bash
# Check pg_hba.conf configuration
sudo cat /etc/postgresql/15/main/pg_hba.conf

# Reset password
sudo -u postgres psql -c "ALTER USER paz_admin WITH PASSWORD 'new_password';"
```

**Disk Space Issues**
```bash
# Check disk usage
df -h

# Clean up old WAL files
pg_archivecleanup /var/lib/postgresql/15/main/pg_wal oldest_required_wal_file
```

### Logs and Monitoring
```bash
# View PostgreSQL logs
sudo tail -f /var/log/postgresql/postgresql-15-main.log

# Or Docker logs
docker logs paz-postgres

# Monitor performance
pg_top -U paz_admin -d paz_admin_db
```

## 📊 Migration from Other Databases

### Using pgLoader
```bash
# Install pgLoader
sudo apt-get install pgloader

# Migrate from MySQL
pgloader mysql://user:pass@localhost/source_db postgresql://paz_admin:paz_admin_password@localhost/paz_admin_db
```

### Manual Migration
1. Export data from source database
2. Convert data types if necessary
3. Import into PostgreSQL using psql or pgAdmin

## 🌐 Cloud Database Options

### AWS RDS Setup
1. Create RDS instance with PostgreSQL 15
2. Set master username and password
3. Configure security groups to allow connections
4. Update application properties with RDS endpoint

### Azure Database for PostgreSQL
1. Create Flexible Server instance
2. Configure networking and firewall rules
3. Create database and user
4. Update connection string

## 🔄 Database Maintenance

### Automated Backups
```bash
# Create backup script
#!/bin/bash
pg_dump -U paz_admin -d paz_admin_db -F c -b -v -f /backups/paz_admin_$(date +%Y%m%d).backup

# Add to crontab for daily backups
0 2 * * * /path/to/backup_script.sh
```

### Monitoring Queries
```sql
-- Monitor slow queries
SELECT * FROM pg_stat_statements ORDER BY total_time DESC LIMIT 10;

-- Check table statistics
SELECT * FROM pg_stat_user_tables;

-- Monitor locks
SELECT * FROM pg_locks;
```

## 📝 Next Steps

After PostgreSQL setup:
1. Run the Spring Boot application to apply database migrations
2. Verify tables are created successfully
3. Test database connections from both backend and frontend
4. Load sample data for testing (see sample_data.sql)

For production deployments:
1. Enable SSL encryption
2. Set up regular backups
3. Configure monitoring and alerts
4. Implement connection pooling

---

*For additional support, consult the PostgreSQL documentation or check the main project README.*