#!/bin/bash -e
CURDIR="$(dirname $(readlink -f $0))"
${CURDIR}/zzz-build-delis-part.sh delis-domibus-ws-sender
