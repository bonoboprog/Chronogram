#!/bin/bash

# Navigate to the project root
cd "$(cd "$(dirname "$0")" && pwd)/../.." || exit 1

# Enter the frontend directory
cd frontend || { echo "❌ Frontend directory not found"; exit 1; }

echo "📱 Adding Android platform..."
ionic capacitor add android
