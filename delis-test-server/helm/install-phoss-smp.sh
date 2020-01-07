#!/bin/bash -ex
CURDIR=`dirname $0`
helm tiller run helm upgrade st --install --force ${CURDIR}/phoss-smp/
#--dry-run --debug
