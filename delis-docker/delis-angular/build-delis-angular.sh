#!/bin/bash -e
CURDIR="$(dirname $(readlink -f $0))"

pushd ${CURDIR}
PROJECT_PATH=${CURDIR}/../../delis-angular

#SKIP_REBUILD=yes

echo "Building ${PROJECT_PATH}"

pushd ${PROJECT_PATH}

npm install

ng build --prod --configuration=production --base-href=/delis-gui-context-name/ && npm run post-build

popd

rm -R -f ./docker/dist
cp -R ${PROJECT_PATH}/dist/delis-web-angular ./docker/dist

docker image \
       build \
       -t delis-angular:0.0.3 \
       -t delis-angular:latest \
       ./docker

rm -R -f ./docker/dist

popd
