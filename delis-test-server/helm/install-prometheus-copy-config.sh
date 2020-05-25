#!/bin/bash -ex
CURDIR=`dirname $0`

kubectl cp monitoring/$(kubectl get pod --namespace monitoring -l app=prometheus -o template --template "{{(index .items 0).metadata.name}}"):/etc/prometheus/config_out/prometheus.env.yaml \
        ${CURDIR}/prometheus.env.yaml
