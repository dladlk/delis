#!/bin/bash -ex
CURDIR=`dirname $0`
helm upgrade delis-phoss --install --force ${CURDIR}/phoss-smp/ 
#--dry-run --debug
