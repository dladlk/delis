#!/bin/sh

echo "Starting application..."
echo "API_URL = ${API_URL}"
envsubst < "/usr/share/nginx/html/assets/config/runtime.json" > "/usr/share/nginx/html/assets/config/runtime.json"
nginx -g 'daemon off;'