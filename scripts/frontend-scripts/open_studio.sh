#!/bin/bash

# Move to the project root (two levels up from scripts/frontend-scripts/)
cd "$(cd "$(dirname "$0")" && pwd)/../.." || exit 1

# Enter the frontend directory
cd frontend || { echo "❌ Frontend directory not found"; exit 1; }

echo "🧪 Opening Android project in Android Studio..."
ionic capacitor open android || { echo "❌ Failed to open Android Studio project"; exit 1; }

echo "✅ Android Studio launched successfully."
