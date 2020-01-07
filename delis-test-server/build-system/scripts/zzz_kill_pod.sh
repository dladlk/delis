#!/bin/bash -e

PROJECT_CODE=$1

if [ -z $PROJECT_CODE ]; then
   echo Define a name of POD_ to kill
   exit
fi

if [ "$PROJECT_CODE" == "delis-web-admin" ]; then
   PROJECT_CODE=delis-admin
fi
if [ "$PROJECT_CODE" == "delis-web-api" ]; then
   PROJECT_CODE=delis-api
fi

echo Searching for docker POD for project code ${PROJECT_CODE}
DOCKER_POD_ID=`docker ps | grep "POD_${PROJECT_CODE}" | cut -d ' ' -f 1`;

if [ -z $DOCKER_POD_ID ]; then
   echo No running pod is found
else
   echo Found docker POD to kill: ${DOCKER_POD_ID}
   echo Killing docker POD...
   docker kill ${DOCKER_POD_ID}
fi


