#!/bin/bash

# Go to the project root (two levels up from scripts/frontend-scripts/)
cd "$(cd "$(dirname "$0")" && pwd)/../.." || exit 1

# Enter the frontend directory
cd frontend || { echo "❌ Frontend directory not found"; exit 1; }

echo "🛠️ Building Ionic app..."
ionic build || { echo "❌ Ionic build failed"; exit 1; }

echo "🔄 Syncing with Android project (assets + plugins)..."
ionic capacitor sync android || { echo "❌ Capacitor sync failed"; exit 1; }

echo "✅ Sync completed successfully."
