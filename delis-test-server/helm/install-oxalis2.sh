#!/bin/bash -ex
CURDIR=`dirname $0`
helm upgrade ox2 --install --force ${CURDIR}/oxalis/ --set partyName=Red --set partyIndex=2
#--dry-run --debug
