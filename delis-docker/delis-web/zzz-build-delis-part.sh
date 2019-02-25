#!/bin/bash -e
CURDIR="$(dirname $(readlink -f $0))"

DELIS_PART=$1

if [ -z "${DELIS_PART}" ]
then
	echo Set DELIS_PART to either "delis-web" or "delis-web-api"
	exit
fi

pushd ${CURDIR}

echo Building ${DELIS_PART}

CURDIR="$(dirname $(readlink -f $0))"

DELIS_DATA_PATH=../../delis-data
DELIS_PART_PATH=../../${DELIS_PART}

echo "Read version of ${DELIS_PART} from pom.xml..."

mvn org.apache.maven.plugins:maven-help-plugin:3.1.0:evaluate -Dexpression=project.version -f ${DELIS_PART_PATH}/pom.xml -q -Doutput=${CURDIR}/version.txt
DELIS_PART_VERSION=$(cat ${CURDIR}/version.txt)
rm -f ${CURDIR}/version.txt

#SKIP_REBUILD=yes

if [ -z "${SKIP_REBUILD}" ]
then

echo "Building ${DELIS_PART_VERSION}"

mvn install -DskipTests=true -f ${DELIS_DATA_PATH}/pom.xml
mvn install -DskipTests=true -f ${DELIS_PART_PATH}/pom.xml

fi

rm -r -f ./docker/app
unzip ${DELIS_PART_PATH}/target/${DELIS_PART}-${DELIS_PART_VERSION}.jar -d ./docker/app

DOCKER_IMAGE_NAME=${DELIS_PART}

if [ "${DOCKER_IMAGE_NAME}" = "delis-web" ]; then
DOCKER_IMAGE_NAME=delis-web-admin
fi

docker build \
	--build-arg VERSION=${DELIS_PART_VERSION} \
	-t ${DOCKER_IMAGE_NAME}:${DELIS_PART_VERSION} \
	-t ${DOCKER_IMAGE_NAME}:latest \
	./docker

rm -r -f ./docker/app

popd