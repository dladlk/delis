#!/bin/bash -e
CURDIR="$(dirname $(readlink -f $0))"
docker run --rm \
	--name=delis-web-admin \
	-e "SERVER_PORT=8011" \
	-e "SERVER_SERVLET_CONTEXT-PATH=/delis-web-admin" \
	-v $CURDIR/../../delis-resources:/delis-resources \
	-v /delis:/delis \
	--network=host \
	delis-web-admin:latest

#	-it --entrypoint=/bin/sh \
#    -p 8011:8011 \
