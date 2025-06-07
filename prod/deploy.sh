#!/bin/bash

echo "📥 Pull dal repository..."
git pull || exit 1

echo "🔧 Compilazione Maven..."
cd ../backend || exit 1
mvn clean package || exit 1
cd ..

echo "📦 Copia del .war nella cartella docker/"
cp backend/target/chronogram.war docker/

echo "🚀 Deploy ambiente di produzione..."
cd prod || exit 1
docker compose -f docker-compose.prod.yml --env-file .env.prod up --build -d
