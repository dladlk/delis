#!/bin/bash -ex
CURDIR=`dirname $0`
helm upgrade st --install --force ${CURDIR}/phoss-smp/
#--dry-run --debug
