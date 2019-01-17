#!/bin/bash -e
CURDIR="$(dirname $(readlink -f $0))"
docker run --rm \
	--name=delis-web \
	-e "SERVER_PORT=8011" \
	-e "SERVLET_SERVLET_CONTEXT-PATH=/delis-admin" \
	-v $CURDIR/../../delis-resources:/delis-resources \
	--network=host \
    -p 8011:8011 \
	delis-web:1.0.0

#	-it --entrypoint=/bin/sh \
