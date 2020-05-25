#!/bin/bash -e
CURDIR="$(dirname $(readlink -f $0))"
docker run --rm \
	--name=delis-validator \
	-e "SERVER_PORT=8016" \
	-e "SERVER_SERVLET_CONTEXT-PATH=/validator" \
	-v $CURDIR/../../delis-resources:/delis-resources \
	--network=host \
	delis-validator:latest
