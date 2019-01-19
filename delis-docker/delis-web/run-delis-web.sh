#!/bin/bash -e
CURDIR="$(dirname $(readlink -f $0))"
docker run --rm \
	--name=delis-web \
	-e "SERVER_PORT=8011" \
	-e "SERVER_SERVLET_CONTEXT-PATH=/delis-admin" \
	-v $CURDIR/../../delis-resources:/delis-resources \
	-v /delis:/delis \
	--network=host \
	delis-web:1.0.0

#	-it --entrypoint=/bin/sh \
#    -p 8011:8011 \
