#!/bin/bash -e
CURDIR="$(dirname $(readlink -f $0))"

SLACK_MESSAGE_FILE=$1
if [ -z $1 ]; then
	echo Single parameter is expected with path to json file with Slack message
	exit 1
fi

SLACK_HOOK_URL=$(tail -n 1 ${CURDIR}/log/!slack_hook_url.conf)

echo Sending slack message:
cat ${SLACK_MESSAGE_FILE}
curl -X POST "${SLACK_HOOK_URL}" -H 'Content-type: application/json' --data "@${SLACK_MESSAGE_FILE}"
