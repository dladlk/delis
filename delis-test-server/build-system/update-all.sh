#!/bin/bash -e
CURDIR="$(dirname $(readlink -f $0))"
CURTIME="$(date '+%Y.%m.%d_%H.%M.%S')"

CURLOG=${CURDIR}/log/${CURTIME}.log
echo "${CURLOG}" > ${CURDIR}/log/last_log_path.txt

local_add_timestamp() {
    while IFS= read -r line; do
        echo "$(date) $line"
    done
}

#./thisscript.sh | adddate >>/var/log/logfile

${CURDIR}/update-all-nolog.sh 2>&1 | local_add_timestamp | tee ${CURLOG}
