#!/bin/bash -ex
CURDIR=`dirname $0`
helm upgrade monitoring stable/prometheus-operator --version=8.3.3 --namespace monitoring --install \
    --values ${CURDIR}/install-prometheus.yaml \
    --values ${CURDIR}/local-only/install-prometheus-smtp.yaml
