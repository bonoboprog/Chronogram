#!/bin/bash

# Move to the project root (one level above scripts/)
cd "$(cd "$(dirname "$0")" && pwd)/../.." || exit 1

MODE=$1

# Validate arguments
if [[ "$MODE" != "debug" && "$MODE" != "normal" ]]; then
  echo "❌ Usage: $0 [debug|normal]"
  exit 1
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

# Restart Tomcat without rebuilding the image
COMPOSE_FILE="-f docker/docker-compose.yml"
DEBUG_MODE=$DEBUG_MODE docker compose $COMPOSE_FILE up -d --no-deps tomcat || {
  echo "❌ Failed to start Tomcat"
  exit 1
}

# Run the refresh script (compile + deploy WAR)
echo "🔄 Running refresh_tomcat.sh..."
scripts/backend-scripts/refresh_tomcat.sh || {
  echo "❌ Failed to refresh Tomcat with the updated WAR"
  exit 1
}

echo "✅ Tomcat started in mode: $MODE"