#!/bin/bash -ex
CURDIR=`dirname $0`
helm tiller run helm upgrade ox2 --install --force ${CURDIR}/oxalis/ --set partyName=Red --set partyIndex=2
#--dry-run --debug
