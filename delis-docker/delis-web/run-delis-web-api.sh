#!/bin/bash -e
CURDIR="$(dirname $(readlink -f $0))"
docker run --rm \
	--name=delis-web-api \
	-e "SERVER_PORT=8012" \
	-e "SERVER_SERVLET_CONTEXT-PATH=/delis-web-api" \
	-v $CURDIR/../../delis-resources:/delis-resources \
	-v /delis:/delis \
	--network=host \
	delis-web-api:0.0.1

#	-it --entrypoint=/bin/sh \
#    -p 8011:8011 \
