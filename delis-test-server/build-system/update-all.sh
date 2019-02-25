#!/bin/bash -e
CURDIR="$(dirname $(readlink -f $0))"

${CURDIR}/docker_access.sh

pushd ${CURDIR}/scripts

./pull-all.sh
./delis-web-admin.sh
./delis-web-api.sh
./delis-angular.sh
./cef-erst-test-web-console.sh

popd