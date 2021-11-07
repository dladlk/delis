#!/bin/bash -ex
CURDIR=`dirname $0`
helm tiller run helm upgrade dt2 --install --force ${CURDIR}/domibus/ \
     --set partyName=Red \
     --set partyIndex=2 \
     --values ${CURDIR}/install-domibus-both-values.yaml \
     --values ${CURDIR}/install-domibus2-values.yaml
#--dry-run --debug
