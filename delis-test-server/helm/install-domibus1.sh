#!/bin/bash -ex
CURDIR=`dirname $0`
helm upgrade dt1 --install --force ${CURDIR}/domibus/ --set partyName=Blue --set partyIndex=1 --values ${CURDIR}/install-domibus1-values-4.0.2.yaml
#--dry-run --debug
