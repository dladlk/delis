#!/bin/bash -ex

source ./build-merge-env-properties.sh

cd ./domibus-tomcat

cp ${MERGE_ENV_PROPERTIES_JAR_PATH} ./${MERGE_ENV_PROPERTIES_JAR_PATH##*/}

CEF_DISTR_ROOT=https://ec.europa.eu/cefdigital/artifact/content/repositories/public/eu/domibus/domibus-distribution

DOMIBUS_VERSION=4.2
DOMIBUS_VERSION_IMAGE=4.2.5
DOMIBUS_WAR_URL=${CEF_DISTR_ROOT}/${DOMIBUS_VERSION}/domibus-distribution-${DOMIBUS_VERSION}-tomcat-war.zip
DOMIBUS_SETUP_URL=${CEF_DISTR_ROOT}/${DOMIBUS_VERSION}/domibus-distribution-${DOMIBUS_VERSION}-tomcat-configuration.zip
DOMIBUS_FS_URL=${CEF_DISTR_ROOT}/${DOMIBUS_VERSION}/domibus-distribution-${DOMIBUS_VERSION}-default-fs-plugin.zip
DOMIBUS_WS_URL=${CEF_DISTR_ROOT}/${DOMIBUS_VERSION}/domibus-distribution-${DOMIBUS_VERSION}-default-ws-plugin.zip

EXT_MERGE_ENV_PROPERTIES=${MERGE_ENV_PROPERTIES_JAR_PATH##*/}

DOMIBUS_SUBVERSION=.1

docker build \
	--build-arg DOMIBUS_VERSION=$DOMIBUS_VERSION \
	--build-arg DOMIBUS_WAR_URL=$DOMIBUS_WAR_URL \
	--build-arg DOMIBUS_SETUP_URL=$DOMIBUS_SETUP_URL \
	--build-arg DOMIBUS_FS_URL=$DOMIBUS_FS_URL \
	--build-arg DOMIBUS_WS_URL=$DOMIBUS_WS_URL \
	--build-arg EXT_MERGE_ENV_PROPERTIES=${EXT_MERGE_ENV_PROPERTIES} \
	-t cef-erst/domibus-tomcat:$DOMIBUS_VERSION_IMAGE$DOMIBUS_SUBVERSION \
	-t cef-erst/domibus-tomcat:latest \
	-t tledelivery.azurecr.io/domibus-tomcat:$DOMIBUS_VERSION_IMAGE$DOMIBUS_SUBVERSION \
	-t tledelivery.azurecr.io/domibus-tomcat:latest \
	-t tlopenpeppol.azurecr.io/domibus-tomcat:$DOMIBUS_VERSION_IMAGE$DOMIBUS_SUBVERSION \
	-t tlopenpeppol.azurecr.io/domibus-tomcat:latest \
	.
	
rm ./${MERGE_ENV_PROPERTIES_JAR_PATH##*/}
