#!/bin/bash -ex
CURDIR=`dirname $0`
helm install --name dtx ${CURDIR}/domibus/ --set partyName=Blue --set partyIndex=1 --values ${CURDIR}/install-domibus1-values-4.1.0.yaml \
    --values ${CURDIR}/install-domibus1-values-4.1.0.yaml \
    --values ${CURDIR}/local-only/install-domibus-local-only.yaml \
    --values ${CURDIR}/install-domibus1-values-jmx-ext.yaml \
    --dry-run --debug
