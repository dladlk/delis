#!/bin/bash -e

PROJECT_ROOT=/wsh/delis
PROJECT_CODE=delis-web-api
PROJECT_SCAN_PATH_LIST="/delis-web-api;/delis-data"
DOCKER_REBUILD_COMMAND=${PROJECT_ROOT}/delis-docker/delis-web/build-${PROJECT_CODE}.sh
FORCE_UPDATE=
if [ -z $1 ]; then
   echo
else
    FORCE_UPDATE=1
fi

./zzz_check_changes.sh ${PROJECT_ROOT} ${PROJECT_CODE} ${PROJECT_SCAN_PATH_LIST} ${DOCKER_REBUILD_COMMAND} ${FORCE_UPDATE}