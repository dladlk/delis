#!/bin/bash -ex
CURDIR=`dirname $0`
helm upgrade edelivery-test --install --force ${CURDIR}/edelivery-test/
