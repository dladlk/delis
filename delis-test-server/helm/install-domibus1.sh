#!/bin/bash -ex
CURDIR=`dirname $0`
kubectl config use-context minikube
helm tiller run helm upgrade dt1 --install --force ${CURDIR}/domibus/ \
    --set partyName=Blue \
    --set partyIndex=1 \
    --values ${CURDIR}/install-domibus1-values.yaml \
    --values ${CURDIR}/local-only/install-domibus-local-only.yaml \
    --values ${CURDIR}/install-domibus1-values-jmx-ext.yaml

