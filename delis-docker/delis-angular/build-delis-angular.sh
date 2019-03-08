#!/bin/bash -e
CURDIR="$(dirname $(readlink -f $0))"

pushd ${CURDIR}
PROJECT_PATH=${CURDIR}/../../delis-angular

#SKIP_REBUILD=yes

if [ -z "${SKIP_REBUILD}" ]
then

echo "Building ${PROJECT_PATH}"

pushd ${PROJECT_PATH}

npm install

ng build --prod --configuration=production --base-href=/delis-gui-context-name/

popd

fi

rm -R -f ./docker/dist
cp -R ${PROJECT_PATH}/dist ./docker/dist

docker image \
       build \
       -t delis-angular:0.0.3 \
       -t delis-angular:latest \
       ./docker

rm -R -f ./docker/dist

popd