#!/bin/bash

echo "🔧 Maven build..."

# Spostati nella directory 'backend' partendo da scripts/backend-scripts/
cd "$(cd "$(dirname "$0")" && pwd)/../../backend" || {
  echo "❌ 'backend' folder not found"
  exit 1
}

mvn clean package || {
  echo "❌ Maven build failed"
  exit 1
}

echo "📦 Copying .war to Docker folder..."

cp target/chronogram.war ../docker/ || {
  echo "❌ Failed to copy .war to Docker folder"
  exit 1
}

echo "✅ Build completed."
