#!/bin/bash -ex
CURDIR=`dirname $0`
helm upgrade monitoring stable/prometheus-operator --version=6.18.0 --namespace monitoring --install \
    --values ${CURDIR}/install-prometheus.yaml \
    --dry-run --debug > ./intall-prometheus-test.txt

