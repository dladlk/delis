#!/bin/bash -e
CURDIR="$(dirname $(readlink -f $0))"
docker run --rm \
	--name=delis-angular \
	-e "CONTEXT_NAME=delis" \
	-e "API_URL=http://localhost:8012/delis-web-api/rest" \
	-e "BASE_URL=http://localhost:8013/delis-angular" \
	-e "SERVER_PORT=8013" \
	--network=host \
	delis-angular

#    -p 8011:8011 \
#	-it --entrypoint=/bin/sh \
