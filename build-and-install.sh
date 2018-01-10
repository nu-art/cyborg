#!/bin/bash


if [ "${ANDROID_HOME}" == "" ]; then
    ANDROID_HOME="/Users/$USER/Library/Android/sdk"
fi

adbCommand=${ANDROID_HOME}/platform-tools/adb
outputFolder="cyborg-demo-app/build/outputs/apk"
packageName="com.nu.art.cyborg.demo"

rm -rf "${outputFolder}"

bash gradlew assembleDebug --offline
pathToApk=`ls "${outputFolder}" | grep .*apk`

"${adbCommand}" install -r "${pathToApk}"
"${adbCommand}" shell am start -n "${packageName}/com.nu.art.cyborg.ui.ApplicationLauncher" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER
