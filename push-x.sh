#!/bin/bash

source ./dev-tools/scripts/git/_core.sh

if [[ "${1}" == "" ]]; then
    throwError "Missing commit message" 2
fi

function updateCyborgX() {
    local errorCode
    cd cyborg-x
        git checkout support_androidx && git pull && git merge origin/master && gitNoConflictsAddCommitPush "cyborg-x" "support_androidx" "updated cyborg-x to latest master: ${1}"
        errorCode=$?
    cd ..

    return ${errorCode}
}

bash ./dev-tools/scripts/git/git-push.sh --this --ignore="cyborg-x" -m="${1}" -np

updateCyborgX
throwError "Error updating CyborgX" $?

git add cyborg-x
git commit -m "updated cyborg-x to latest master: ${1}"

bash ./dev-tools/scripts/git/git-push.sh --debug --this --ignore="cyborg-x" -m="${1}"










#        git checkout support_androidx
#        echo $?
#
#        git pull
#        echo $?
#
#        git merge origin/master
#        echo $?
#
#        gitNoConflictsAddCommitPush "cyborg-x" "support_androidx" "updated latest master"
#
#        git push
#        echo $?
