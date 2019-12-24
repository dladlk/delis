#!/bin/bash -e

PROJECT_ROOT=/wsh/delis
PROJECT_CODE=delis-sender-service
PROJECT_SCAN_PATH_LIST="/delis-sbdh;/delis-data;/delis-tasks;/delis-xslt-util;/delis-oxalis-sender;/delis-sender-service"
DOCKER_REBUILD_COMMAND=${PROJECT_ROOT}/delis-docker/delis-web/build-${PROJECT_CODE}.sh
FORCE_UPDATE=
if [ -z $1 ]; then
   echo
else
    FORCE_UPDATE=1
fi

./zzz_check_changes.sh ${PROJECT_ROOT} ${PROJECT_CODE} ${PROJECT_SCAN_PATH_LIST} ${DOCKER_REBUILD_COMMAND} ${FORCE_UPDATE}