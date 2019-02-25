#!/bin/bash -ex
CURDIR=`dirname $0`
helm upgrade delis --install --force ${CURDIR}/delis/
