#!/usr/bin/env bash
set -euo pipefail

# Load mock/accounts.json into the embedded MongoDB started by the application.

DB_NAME="test"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
JSON_IN="${REPO_ROOT}/mock/accounts.json"
JSON_TMP="/tmp/accounts_transformed.json"

# Minimal tool checks
need() { command -v "$1" >/dev/null 2>&1 || { echo "[ERROR] Missing required tool: $1" >&2; exit 1; }; }
need mongoimport
need mongosh

if [ ! -f "$JSON_IN" ]; then
  echo "[ERROR] Cannot find $JSON_IN. Run from repo root or fix path." >&2
  exit 1
fi

PORT=27027
echo "[INFO] Using Mongo at localhost:${PORT} (db=${DB_NAME})"

# Transform JSON: set _id = accountNumber (upsertable)
if command -v jq >/dev/null 2>&1; then
  echo "[INFO] Transforming JSON with jq (preserve accountNumber)..."
  jq 'map(. + { _id: .accountNumber })' "$JSON_IN" > "$JSON_TMP"
else
  echo "[INFO] jq not found; using sed (replaces accountNumber with _id)"
  sed 's/"accountNumber"/"_id"/g' "$JSON_IN" > "$JSON_TMP"
fi

echo "[INFO] Importing (upsert) accounts into '${DB_NAME}.accounts' with mongoimport..."
mongoimport \
  --uri "mongodb://localhost:${PORT}/${DB_NAME}" \
  --collection accounts \
  --file "$JSON_TMP" \
  --jsonArray \
  --mode upsert \
  --upsertFields _id

echo "[INFO] Counting documents in '${DB_NAME}.accounts'..."
mongosh --quiet --host localhost --port "$PORT" --eval "db.getSiblingDB('${DB_NAME}').accounts.countDocuments({})"

echo "[DONE] Accounts loaded."
