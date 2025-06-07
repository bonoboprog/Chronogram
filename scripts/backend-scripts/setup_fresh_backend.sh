#!/bin/bash

# 📍 Move to the project root (two levels above)
cd "$(cd "$(dirname "$0")" && pwd)/../.." || exit 1

echo "📄 Loading environment variables..."
export $(grep -v '^#' .env | xargs)

echo "🧹 Cleaning up existing environment..."
docker compose -f docker/docker-compose.yml down -v

echo "🔧 Compiling Java backend and generating .war..."
scripts/backend-scripts/compile_and_stage_war.sh || { echo "❌ Compilation failed"; exit 1; }

echo "🚀 Starting Docker environment (MySQL + Tomcat)..."
cd docker || exit 1
docker compose up --build -d

echo "✅ Development environment started!"
