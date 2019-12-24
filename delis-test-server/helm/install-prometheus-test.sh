#!/bin/bash -ex
CURDIR=`dirname $0`
CURTIME="$(date '+%Y.%m.%d_%H.%M.%S')"

helm upgrade monitoring stable/prometheus-operator --version=6.18.0 --namespace monitoring --install \
    --values ${CURDIR}/install-prometheus.yaml \
    --dry-run --debug > ./intall-prometheus-test-${CURTIME}.txt

