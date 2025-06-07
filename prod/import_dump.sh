#!/bin/bash
echo "📦 Copio dump.sql nel container..."
docker cp dump.sql chronogram-mysql:/tmp/dump.sql

echo "📤 Importazione nel database..."
docker exec -i chronogram-mysql mysql -u chronouser -pchronopass chronogram < /tmp/dump.sql


echo "✅ Importazione completata."

