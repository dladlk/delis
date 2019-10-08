#!/bin/bash -ex
CURDIR=`dirname $0`
helm install --name dtx ${CURDIR}/domibus/ --set partyName=Blue --set partyIndex=1 --values ${CURDIR}/install-domibus1-values-4.1.0.yaml --dry-run --debug
