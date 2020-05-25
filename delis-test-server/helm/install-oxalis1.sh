#!/bin/bash -ex
CURDIR=`dirname $0`
helm tiller run helm upgrade ox1 --install --force ${CURDIR}/oxalis/ --set partyName=Blue --set partyIndex=1
#--dry-run --debug
