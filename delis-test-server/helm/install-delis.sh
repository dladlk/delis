#!/bin/bash -ex
CURDIR=`dirname $0`
helm upgrade delis --install --force ${CURDIR}/delis/ --values ${CURDIR}/install-delis-values.yaml
#--dry-run --debug