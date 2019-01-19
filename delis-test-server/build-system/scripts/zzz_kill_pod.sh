#!/bin/bash -e

PROJECT_CODE=$1

echo Searching for docker POD for project code ${PROJECT_CODE}
DOCKER_POD_ID=`docker ps | grep "POD_${PROJECT_CODE}" | cut -d ' ' -f 1`;
echo Found docker POD to kill: ${DOCKER_POD_ID}
echo Killing docker POD...
docker kill ${DOCKER_POD_ID}
