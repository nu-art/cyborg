#!/bin/bash

source dev-tools/scripts/utils/file-tools.sh

directories=$(listAllGitFolders)
directories=(${directories//,/ })

lastCommitToBitbucket() {
    folderName=$1

    git add .
    git commit -am "Fixing imports and packages"
    git push -u origin master
}

moveToGithub() {
    folderName=$1
    origin=$2

    rm -rf .git
    echo "Processing ${folderName}:  ${origin}"
    echo "# ${folderName}" >> README.md
    git init
    git add .
    git commit -am "Migrating from Bitbucket to Github ${origin}\nScary, first time in forever I would expose my stuff, and I think it is 2 years too late...\nI hope you guys will find this framework as useful as I intended it to be, I hope it will change the way people develop Android.\nLet's welcome Cyborg"
    git remote add origin "git@github.com:nu-art/${folderName}.git"
    git push -u origin master
}

containsElement () {
  local e
  for e in "${@:2}"; do [[ "$e" == "$1" ]] && return 1; done
  return 0
}

modulesToMigrate=("cyborg-google-analytics" "generic-processor" "belog" "cyborg-core" "reflection" "module-manager" "nu-art-core")
for folderName in "${directories[@]}"
do
    folderName=`echo ${folderName} | sed -E 's/\///'`

    origin=`git remote -v | grep push | sed -E 's/origin\s//' | sed -E 's/\s\(push\)//'`
    containsElement "${folderName}" "${modulesToMigrate[@]}"
    if [ "$?" == "0" ]; then
        continue;
    fi

    echo
    echo "--------- ${folderName} -----------"
    pushd ${folderName} >> /dev/null
        lastCommitToBitbucket "${folderName}"
#        moveToGithub "${folderName}" "${origin}"
    popd >> /dev/null
done

