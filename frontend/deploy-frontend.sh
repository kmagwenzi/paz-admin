#!/bin/bash

# PAZ Admin Frontend Deployment Script
# Usage: ./deploy-frontend.sh [environment]

set -e

ENVIRONMENT=${1:-prod}

echo "🚀 Deploying PAZ Admin Frontend ($ENVIRONMENT environment)"

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker and try again."
    exit 1
fi

# Build the frontend Docker image
echo "📦 Building frontend Docker image..."
docker build -t paz-admin-frontend:latest .

# Check if docker-compose is available
if command -v docker-compose &> /dev/null; then
    COMPOSE_CMD="docker-compose"
else
    COMPOSE_CMD="docker compose"
fi

# Start services based on environment
if [ "$ENVIRONMENT" = "dev" ]; then
    echo "🔧 Starting development environment..."
    # For dev, we might want to run without docker-compose for frontend hot reload, but using Docker for consistency
    $COMPOSE_CMD up -d frontend
else
    echo "🚀 Starting production environment..."
    $COMPOSE_CMD up -d frontend
fi

echo "✅ Frontend deployment completed!"
echo "🌐 Frontend URL: http://localhost:3000"
echo "📊 Make sure the backend is running on http://localhost:8080 for API calls"