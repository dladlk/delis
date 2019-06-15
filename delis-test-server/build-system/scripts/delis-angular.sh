#!/bin/bash -e

PROJECT_ROOT=/wsh/delis
PROJECT_CODE=delis-angular
PROJECT_SCAN_PATH_LIST="/delis-angular"
DOCKER_REBUILD_COMMAND=${PROJECT_ROOT}/delis-docker/delis-angular/build-${PROJECT_CODE}.sh

./zzz_check_changes.sh ${PROJECT_ROOT} ${PROJECT_CODE} ${PROJECT_SCAN_PATH_LIST} ${DOCKER_REBUILD_COMMAND}