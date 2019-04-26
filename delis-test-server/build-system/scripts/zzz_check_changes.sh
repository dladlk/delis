#!/bin/bash -e

PROJECT_ROOT=$1
PROJECT_CODE=$2
PROJECT_SCAN_PATH_LIST=$3
DOCKER_REBUILD_COMMAND=$4
FORCE_UPDATE=$5

echo ${DOCKER_REBUILD_COMMAND}

if [ -z $1 ]; then
PROJECT_ROOT=/wsh/delis
PROJECT_CODE=delis-web-admin
PROJECT_SCAN_PATH_LIST="/delis-web;/delis-data"
DOCKER_REBUILD_COMMAND=${PROJECT_ROOT}/delis-docker/delis-web/build-${PROJECT_CODE}.sh
fi

echo "**********"
echo Update of ${PROJECT_CODE}
echo "**********"

if [ -z $FORCE_UPDATE ]; then
   echo Check whether code is changed since last docker image rebuild
else
    echo Forced update is requested - skip checks
	${DOCKER_REBUILD_COMMAND}
	./zzz_kill_pod.sh ${PROJECT_CODE}
fi

GIT_LOG_FILE=./log/${PROJECT_CODE}_git_log.txt

DOCKER_IMAGE_NAME=${PROJECT_CODE}:latest
DOCKER_CREATE_TIME=`docker image inspect ${DOCKER_IMAGE_NAME} --format="{{.Created}}"`
echo Docker image ${DOCKER_IMAGE_NAME} was last rebuilt ${DOCKER_CREATE_TIME}

echo Check commits, done after this time, to one of: ${PROJECT_SCAN_PATH_LIST}

PROJECT_SCAN_PATH_ARRAY=(${PROJECT_SCAN_PATH_LIST//;/ })

echo '' > ${GIT_LOG_FILE}
for PROJECT_SCAN_PATH in "${PROJECT_SCAN_PATH_ARRAY[@]}"; do
	echo Checking git logs for ${PROJECT_ROOT}${PROJECT_SCAN_PATH}
        echo git -C ${PROJECT_ROOT} log --after="${DOCKER_CREATE_TIME}" --no-merges --max-count=10 ${PROJECT_ROOT}${PROJECT_SCAN_PATH}
    git -C ${PROJECT_ROOT} log --after="${DOCKER_CREATE_TIME}" --no-merges --max-count=10 ${PROJECT_ROOT}${PROJECT_SCAN_PATH} >> ${GIT_LOG_FILE}
done

echo 'Remove empty lines in generated log file'
sed -i '/^$/d' ${GIT_LOG_FILE}

if [ -s ${GIT_LOG_FILE} ]; then
	echo Docker image ${DOCKER_IMAGE_NAME} should be rebuilt - found changes:
        cat ${GIT_LOG_FILE}
	${DOCKER_REBUILD_COMMAND}
	./zzz_kill_pod.sh ${PROJECT_CODE}
else
	echo No git changes are found for ${PROJECT_CODE}
fi
