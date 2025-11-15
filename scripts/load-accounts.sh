#!/usr/bin/env bash
set -euo pipefail

CONTAINER_NAME="rabobank-mongo"
DB_NAME="test"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
JSON_PATH_HOST="${REPO_ROOT}/mock/accounts.json"
JSON_PATH_CONTAINER="/tmp/accounts.json"

if ! docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
  echo "[INFO] Mongo container '${CONTAINER_NAME}' not running. Attempting to start via docker compose..."
  docker compose up -d mongo
  # Wait for healthy
  echo "[INFO] Waiting for Mongo to become healthy..."
  for i in {1..30}; do
    STATUS=$(docker inspect -f '{{ .State.Health.Status }}' ${CONTAINER_NAME} 2>/dev/null || echo "")
    if [ "$STATUS" = "healthy" ]; then
      break
    fi
    sleep 2
  done
fi

if [ ! -f "$JSON_PATH_HOST" ]; then
  echo "[ERROR] Cannot find $JSON_PATH_HOST. Run from repo root or fix path." >&2
  exit 1
fi

echo "[INFO] Copying $JSON_PATH_HOST into container..."
docker cp "$JSON_PATH_HOST" "${CONTAINER_NAME}:${JSON_PATH_CONTAINER}"

echo "[INFO] Preparing transformed JSON (_id from accountNumber) inside container..."
docker exec -i "$CONTAINER_NAME" sh -c "sed 's/\"accountNumber\"/\"_id\"/g' '$JSON_PATH_CONTAINER' > '/tmp/accounts_transformed.json'"

echo "[INFO] Importing (upsert) accounts into '${DB_NAME}.accounts' with mongoimport..."
docker exec -i "$CONTAINER_NAME" mongoimport \
  --db "$DB_NAME" \
  --collection accounts \
  --file "/tmp/accounts_transformed.json" \
  --jsonArray \
  --mode upsert \
  --upsertFields _id

echo "[INFO] Counting documents in '${DB_NAME}.accounts'..."
docker exec -i "$CONTAINER_NAME" mongosh --quiet --eval "db.getSiblingDB('${DB_NAME}').accounts.countDocuments({})"

echo "[DONE] Accounts loaded."
