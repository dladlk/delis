#!/bin/bash -e
CURDIR="$(dirname $(readlink -f $0))"

${CURDIR}/recreate_db.sh

pushd ${CURDIR}/../delis-data
mvn liquibase:update
popd
