#!/bin/bash -ex

source ./build-merge-env-properties.sh

cd ./domibus-jms

cp ${MERGE_ENV_PROPERTIES_JAR_PATH} ./${MERGE_ENV_PROPERTIES_JAR_PATH##*/}

CEF_DISTR_ROOT=https://ec.europa.eu/cefdigital/artifact/content/repositories/public/eu/domibus/domibus-distribution
DOMIBUS_VERSION_IMAGE=4.2
DOMIBUS_SUBVERSION=.0.0

EXT_MERGE_ENV_PROPERTIES=${MERGE_ENV_PROPERTIES_JAR_PATH##*/}

docker build \
	--build-arg EXT_MERGE_ENV_PROPERTIES=${EXT_MERGE_ENV_PROPERTIES} \
  -t domibus-activemq:$DOMIBUS_VERSION_IMAGE$DOMIBUS_SUBVERSION \
  -t cef-erst/domibus-activemq:$DOMIBUS_VERSION_IMAGE$DOMIBUS_SUBVERSION \
  -t domibus-activemq:latest \
  -t cef-erst/domibus-activemq:latest \
  .

rm ./${MERGE_ENV_PROPERTIES_JAR_PATH##*/}
