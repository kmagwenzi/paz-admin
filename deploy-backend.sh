#!/bin/bash

# PAZ Admin Backend Deployment Script
# Usage: ./deploy-backend.sh [environment]

set -e

ENVIRONMENT=${1:-prod}

echo "🚀 Deploying PAZ Admin Backend ($ENVIRONMENT environment)"

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker and try again."
    exit 1
fi

# Build the backend Docker image
echo "📦 Building backend Docker image..."
docker build -t paz-admin-backend:latest .

# Check if docker-compose is available
if command -v docker-compose &> /dev/null; then
    COMPOSE_CMD="docker-compose"
else
    COMPOSE_CMD="docker compose"
fi

# Start services based on environment
if [ "$ENVIRONMENT" = "dev" ]; then
    echo "🔧 Starting development environment..."
    $COMPOSE_CMD up -d postgres
    echo "⏳ Waiting for PostgreSQL to start..."
    sleep 10
    $COMPOSE_CMD up -d backend
else
    echo "🚀 Starting production environment..."
    $COMPOSE_CMD up -d
fi

echo "✅ Deployment completed!"
echo "📊 Backend API: http://localhost:8080"
echo "📊 API Health: http://localhost:8080/actuator/health"