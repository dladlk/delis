#!/bin/bash -ex
CURDIR=`dirname $0`
helm upgrade dt2 --install --force ${CURDIR}/domibus/ --set partyName=Red --set partyIndex=2
#--dry-run --debug
