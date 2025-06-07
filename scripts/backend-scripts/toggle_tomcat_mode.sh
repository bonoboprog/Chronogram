#!/bin/bash

# Move to the project root (one level above scripts/)
cd "$(cd "$(dirname "$0")" && pwd)/../.." || exit 1

MODE=$1
BUILD=false

# Validate arguments
if [[ "$MODE" != "debug" && "$MODE" != "normal" ]]; then
  echo "❌ Usage: $0 [debug|normal] [--build]"
  exit 1
fi

# Check for the --build option
if [[ "$2" == "--build" || "$3" == "--build" ]]; then
  BUILD=true
fi

# Load environment variables from .env
if [ -f .env ]; then
  export $(grep -v '^#' .env | xargs)
else
  echo "⚠️  .env file not found in the current directory"
fi

# Set the mode
if [[ "$MODE" == "debug" ]]; then
  echo "♻️ Switching to DEBUG mode…"
  DEBUG_MODE=true
else
  echo "♻️ Switching to NORMAL mode..."
  DEBUG_MODE=false
fi

# Stop and remove only the Tomcat container
docker container stop docker-tomcat
docker container rm docker-tomcat

# Rebuild only if requested
COMPOSE_FILE="-f docker/docker-compose.yml"

if [[ "$BUILD" == true ]]; then
  echo "🔨 Rebuilding Tomcat container..."
  DEBUG_MODE=$DEBUG_MODE docker compose $COMPOSE_FILE up -d --no-deps --build tomcat
else
  DEBUG_MODE=$DEBUG_MODE docker compose $COMPOSE_FILE up -d --no-deps tomcat
fi

echo "✅ Tomcat started in mode: $MODE"
