#!/bin/bash -e

CURDIR="$(dirname $(readlink -f $0))"
PROJECT_PATH=${CURDIR}/../merge-env-properties/
cd ${PROJECT_PATH}
mvn clean install -DskipTests=true

RELATIVE_PATH=${PROJECT_PATH}"$(ls  ./target/*.jar --sort=time | tail -n1)"
export MERGE_ENV_PROPERTIES_JAR_PATH="$(readlink -f ${RELATIVE_PATH})" 
echo MERGE_ENV_PROPERTIES_JAR_PATH=${MERGE_ENV_PROPERTIES_JAR_PATH}
cd ${CURDIR}
