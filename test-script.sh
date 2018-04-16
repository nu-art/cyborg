#!/bin/bash

source ./dev-tools/scripts/utils/tools.sh
#
#result=$(cat test-file)
##echo "$result"
#
#conflicts=`echo "${result}" | grep "CONFLICT" | sed -E "s/CONFLICT.*in (.*)$/Conflict in file: \1/"`
#if [ "${conflicts}" != "" ]; then
#    echo "Has conflicts"
#    echo "${conflicts}"
#fi


waitForDevice 4d00dd00753030ed "disconnect the device" false
waitForDevice 4d00dd00753030ed "waiting for device" true