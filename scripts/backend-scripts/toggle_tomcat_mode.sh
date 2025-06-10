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

# Set the DEBUG_MODE
if [[ "$MODE" == "debug" ]]; then
  echo "♻️ Switching to DEBUG mode…"
  DEBUG_MODE=true
else
  echo "♻️ Switching to NORMAL mode..."
  DEBUG_MODE=false
fi

# Compose file
COMPOSE_FILE="-f docker/docker-compose.yml"

# Restart Tomcat with new DEBUG_MODE
echo "🔁 Restarting Tomcat with DEBUG_MODE=$DEBUG_MODE ..."
DEBUG_MODE=$DEBUG_MODE docker compose $COMPOSE_FILE restart tomcat

echo "✅ Tomcat restarted in mode: $MODE"
