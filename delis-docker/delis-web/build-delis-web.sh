#!/bin/bash -e

CURDIR="$(dirname $(readlink -f $0))"

DELIS_DATA_PATH=../../delis-data
DELIS_WEB_PATH=../../delis-web

SKIP_REBUILD=yes

if [ -z "${SKIP_REBUILD}" ]
then

echo "Read version of Delis-web from pom.xml..."

mvn org.apache.maven.plugins:maven-help-plugin:3.1.0:evaluate -Dexpression=project.version -f ${DELIS_WEB_PATH}/pom.xml -q -Doutput=${CURDIR}/delis-web-version.txt
DELIS_WEB_VERSION=$(cat ${CURDIR}/delis-web-version.txt)
rm -f ${CURDIR}/delis-web-version.txt

echo "Building ${DELIS_WEB_VERSION}"

mvn install -DskipTests=true -f ${DELIS_DATA_PATH}/pom.xml
mvn install -DskipTests=true -f ${DELIS_WEB_PATH}/pom.xml

else

DELIS_WEB_VERSION=1.0.0

fi

rm -r -f ./docker/app
unzip ${DELIS_WEB_PATH}/target/delis-web-${DELIS_WEB_VERSION}.jar -d ./docker/app

docker build \
	--build-arg VERSION=${DELIS_WEB_VERSION} \
	-t delis-web:${DELIS_WEB_VERSION} \
	./docker

rm -r -f ./docker/app
