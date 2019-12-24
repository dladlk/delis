#!/bin/bash -ex
CURDIR=`dirname $0`
helm upgrade delis-dry-run --install --force ${CURDIR}/delis/ --values ${CURDIR}/install-delis-values.yaml \
    --values ${CURDIR}/local-only/install-delis-mail.yaml --dry-run --debug