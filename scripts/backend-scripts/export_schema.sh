#!/bin/bash

# 📍 Go to the project root (two levels up from scripts/backend-scripts/)
cd "$(cd "$(dirname "$0")" && pwd)/../.." || {
  echo "❌ Failed to locate project root"
  exit 1
}

echo "📤 Exporting schema from MySQL container..."

# Load environment variables from .env
if [ -f .env ]; then
  export $(grep -v '^#' .env | xargs)
else
  echo "⚠️  .env file not found"
fi

# Perform schema export
docker exec chronogram-mysql mysqldump -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE" --no-data > docker/init/schema.sql

echo "✅ Schema exported to docker/init/schema.sql"
