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
RESULT_CODE=${PIPESTATUS[0]}

echo RESULT_CODE=$RESULT_CODE

if [ $RESULT_CODE -eq 0 ];then
	STATUS_MESSAGE="SUCCESS: edelivery-test updated OK"
else
	STATUS_MESSAGE="ERROR: edelivery-test update ERROR (code ${RESULT_CODE})"
fi

ENDTIME="$(date '+%Y.%m.%d_%H.%M.%S')"
LAST_LOG_LINE="$(tail -n 1 ${CURLOG})"

SLACK_MESSAGE_FILE=${CURDIR}/log/slack_message.json
TEXT_MESSAGE="${STATUS_MESSAGE} [${CURTIME} - ${ENDTIME}], last line: ${LAST_LOG_LINE}"
jq --compact-output --null-input --arg text "$TEXT_MESSAGE" '{text: $text}' > ${SLACK_MESSAGE_FILE}

${CURDIR}/scripts/zzz_send_slack.sh ${SLACK_MESSAGE_FILE}
