#!/bin/bash

# Move to the project root (two levels above)
cd "$(cd "$(dirname "$0")" && pwd)/../.." || { echo "❌ Failed to reach project root"; exit 1; }

echo "🔧 Compiling Java backend..."
cd backend || { echo "❌ 'backend' folder not found"; exit 1; }

mvn clean package || { echo "❌ Maven compilation failed"; exit 1; }

echo "📦 Copying the .war to the Tomcat container..."
docker cp target/chronogram.war docker-tomcat:/usr/local/tomcat/webapps/ || {
  echo "❌ Error while copying the .war file"
  exit 1
}

echo "🔁 Restarting Tomcat container..."
docker restart docker-tomcat || {
  echo "❌ Error while restarting Tomcat"
  exit 1
}

echo "✅ Backend successfully updated."
