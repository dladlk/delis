#!/bin/bash -ex
CURDIR=`dirname $0`
kubectl config use-context minikube
helm tiller run helm upgrade edelivery-test --install --force ${CURDIR}/edelivery-test/ \
	--values ${CURDIR}/local-only/install-edelivery-test.yaml

