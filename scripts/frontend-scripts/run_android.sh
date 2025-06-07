#!/bin/bash

# 📍 Go to the project root (two levels up from scripts/frontend-scripts/)
cd "$(cd "$(dirname "$0")" && pwd)/../.." || exit 1

# 📦 Enter the frontend directory
cd frontend || { echo "❌ Frontend directory not found"; exit 1; }

if [[ "$1" == "--live" ]]; then
  echo "🚀 Running app on Android with live reload..."
  ionic capacitor run android --livereload --external
else
  echo "🚀 Running app on Android..."
  ionic capacitor run android
fi
