#!/bin/bash -ex
CURDIR=`dirname $0`
helm tiller run helm upgrade delis-test-index --install --force ${CURDIR}/delis-test-index/
