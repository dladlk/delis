#!/bin/bash -ex
CURDIR=`dirname $0`
helm upgrade dt1 --install --force ${CURDIR}/domibus/ --set partyName=Blue --set partyIndex=1
#--dry-run --debug
