#!/bin/bash -e

FOLDER=/wsh/delis/delis-resources/examples/xml
USER_PASS=user:password
SERVER_PATH=http://localhost:8080

for f in ${FOLDER}/*.xml; do 
	FILE=$f
	echo Sending ${FILE} ...
	curl --user "${USER_PASS}" -X POST "${SERVER_PATH}/rest/send" -H  "Content-Type: multipart/form-data" -F "file=@${FILE}"
	echo
done
