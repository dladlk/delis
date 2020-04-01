#!/bin/bash -e

PROJECT_ROOT=/wsh/cef-erst
PROJECT_CODE=test-web-console
PROJECT_SCAN_PATH_LIST="/cef-test-web;../delis/delis-domibus-util"
DOCKER_REBUILD_COMMAND=${PROJECT_ROOT}/docker/build-test-web.sh

./zzz_check_changes.sh ${PROJECT_ROOT} ${PROJECT_CODE} ${PROJECT_SCAN_PATH_LIST} ${DOCKER_REBUILD_COMMAND}