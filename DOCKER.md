# Docker Deployment Guide

## Quick Start

### Prerequisites
- Docker installed on your system
- Docker Compose installed

### Deploy with Docker Compose (Recommended)

1. **Build and start all services:**
```bash
docker-compose up -d
```

2. **Check logs:**
```bash
# View all logs
docker-compose logs -f

# View app logs only
docker-compose logs -f app

# View database logs only
docker-compose logs -f postgres
```

3. **Access the application:**
   - Email List: http://localhost:8080/SQLGatewayApp/
   - SQL Gateway: http://localhost:8080/SQLGatewayApp/sqlGateway

4. **Stop services:**
```bash
docker-compose down
```

5. **Stop and remove all data:**
```bash
docker-compose down -v
```

### Manual Docker Build (Alternative)

If you want to build and run containers manually:

1. **Create a Docker network:**
```bash
docker network create sqlgateway-network
```

2. **Start PostgreSQL:**
```bash
docker run -d \
  --name sqlgateway-postgres \
  --network sqlgateway-network \
  -e POSTGRES_DB=murach \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -v postgres_data:/var/lib/postgresql/data \
  -p 5432:5432 \
  postgres:15-alpine
```

3. **Initialize database:**
```bash
docker exec -i sqlgateway-postgres psql -U postgres -d murach < database_postgresql.sql
```

4. **Build the application:**
```bash
docker build -t sqlgateway-app .
```

5. **Run the application:**
```bash
docker run -d \
  --name sqlgateway-app \
  --network sqlgateway-network \
  -p 8080:8080 \
  sqlgateway-app
```

## Configuration

### Database Connection
The Docker setup uses these default credentials:
- **Database:** murach
- **Username:** postgres
- **Password:** postgres
- **Host:** postgres (container name)
- **Port:** 5432

To change these, edit `docker-compose.yml` and `context-docker.xml`.

### Port Mapping
- **Application:** Port 8080 (host) → 8080 (container)
- **PostgreSQL:** Port 5432 (host) → 5432 (container)

To change ports, edit the `ports` section in `docker-compose.yml`.

## Troubleshooting

### Application won't start
```bash
# Check if database is ready
docker-compose logs postgres

# Restart the application
docker-compose restart app
```

### Database connection errors
```bash
# Verify database is running
docker-compose ps

# Check database logs
docker-compose logs postgres

# Test database connection
docker exec -it sqlgateway-postgres psql -U postgres -d murach
```

### View application logs
```bash
docker-compose logs -f app
```

### Reset everything
```bash
# Stop and remove all containers and volumes
docker-compose down -v

# Rebuild and start fresh
docker-compose up -d --build
```

## Production Deployment

For production, consider:

1. **Use environment variables for sensitive data:**
   - Don't hardcode passwords in `docker-compose.yml`
   - Use `.env` file or Docker secrets

2. **Enable SSL/TLS:**
   - Configure Tomcat with SSL certificates
   - Use a reverse proxy (nginx, traefik)

3. **Database backups:**
   - Set up automated backups for the PostgreSQL volume
   - Use `pg_dump` regularly

4. **Resource limits:**
   - Add memory and CPU limits in `docker-compose.yml`

5. **Monitoring:**
   - Add health checks
   - Use logging aggregation tools

## File Structure

```
SQLGatewayApp/
├── Dockerfile              # Multi-stage build for the app
├── docker-compose.yml      # Orchestration file
├── .dockerignore          # Files to exclude from build
├── context-docker.xml     # Docker-specific DB config
└── database_postgresql.sql # Database initialization
```
