#!/bin/bash -e
CURDIR="$(dirname $(readlink -f $0))"
docker run --rm \
	--name=delis-domibus-ws-sender \
	-e "SERVER_PORT=8015" \
	-e "SERVER_SERVLET_CONTEXT-PATH=/delis-domibus-ws-sender" \
	--network=host \
	delis-domibus-ws-sender:latest

#	-it --entrypoint=/bin/sh \
#    -p 8011:8011 \
