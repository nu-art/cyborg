#!/bin/bash

source ./dev-tools/scripts/utils/tools.sh

result=$(cat test-file)
#echo "$result"

conflicts=`echo "${result}" | grep "CONFLICT" | sed -E "s/CONFLICT.*in (.*)$/Conflict in file: \1/"`
if [ "${conflicts}" != "" ]; then
    echo "Has conflicts"
    echo "${conflicts}"
fi
