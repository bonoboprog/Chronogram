#!/bin/bash

# Move to the project root (two levels above)
cd "$(cd "$(dirname "$0")" && pwd)/../.." || exit 1

# Enter the frontend directory
cd frontend || { echo "❌ 'frontend' directory not found"; exit 1; }

echo "🏗️ Building Android project without launching..."
ionic capacitor build android
