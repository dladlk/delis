
echo "Starting application..."
echo "API_URL = ${API_URL}"
echo "CONTEXT_NAME = ${CONTEXT_NAME}"
echo "SERVER_PORT = ${SERVER_PORT}"
MARKER_CONTEXT_NAME=delis-gui-context-name

envsubst < "/usr/share/nginx/html/assets/config/runtime.json" > "/usr/share/nginx/html/assets/config/runtime.json"
sed -i "s/${MARKER_CONTEXT_NAME}/${CONTEXT_NAME}/g" /etc/nginx/nginx.conf
sed -i "s/${MARKER_CONTEXT_NAME}/${CONTEXT_NAME}/g" /usr/share/nginx/html/index.html

if [ -z "${SERVER_PORT}" ]
then
	echo Keep default port 80
else
	sed -i "s/listen 80/listen ${SERVER_PORT}/g" /etc/nginx/nginx.conf
fi

nginx -g 'daemon off;'
