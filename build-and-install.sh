#!/bin/bash

bashVersion=`bash --version | grep version | sed -E "s/.* version (.).*/\1/"`

source dev-tools/scripts/utils/coloring.sh
source dev-tools/scripts/utils/log-tools.sh
source dev-tools/scripts/utils/error-handling.sh

paramColor=${BBlue}
valueColor=${BGreen}
function printUsage {
    local errorMessage=${1}

    packageNameParam="${paramColor}--packageName=${NoColor}"
    if [ "${packageName}" == "" ]; then
        packageNameParam="${packageNameParam}${valueColor}your.package.name.here${NoColor}"
    else
        packageNameParam="${packageNameParam}${valueColor}${packageName}${NoColor}"
    fi

    projectParam="${paramColor}--project=${NoColor}"
    if [ "${projectName}" == "" ]; then
        projectParam="${projectParam}${valueColor}you-project-name${NoColor}"
    else
        projectParam="${projectParam}${valueColor}${projectName}${NoColor}"
    fi

    buildParam="${paramColor}--build=${NoColor}${buildParam}${valueColor}build-type${NoColor}"
    deviceIdParam="${paramColor}--deviceId=${NoColor}${valueColor}your-device-id-here${NoColor}"
    uninstallParam="${paramColor}optional flags:${NoColor} ${valueColor}--uninstall${NoColor} | ${valueColor}--offline${NoColor} | ${valueColor}--nobuild${NoColor} | ${valueColor}--clear-cachec${NoColor}"

    echo
    if [ "${errorMessage}" != "" ]; then
        logError "    ${errorMessage}"
        echo
    fi
    echo -e "   USAGE:"
    echo -e "     ${BBlack}bash${NoColor} ${BCyan}${0}${NoColor}"
    echo
    echo -e "               MUST:"
    echo -e "                         ${packageNameParam}"
    echo -e "                         ${projectParam}"
    echo -e "                         ${buildParam}"
    echo -e "                               |-- or use the --no-build flag"
    echo
    echo -e "           OPTIONAL:"
    echo -e "                         ${deviceIdParam}"
    echo -e "                         ${uninstallParam}"
    echo
    exit
}

if [ "${ANDROID_HOME}" == "" ]; then
    ANDROID_HOME="/Users/$USER/Library/Android/sdk"
fi

adbCommand=${ANDROID_HOME}/platform-tools/adb

offline="--offline"
build="Release"
build="Debug"

offline=""
nobuild=""

for (( lastParam=1; lastParam<=$#; lastParam+=1 )); do
    paramValue="${!lastParam}"
    if [[ "${paramValue}" =~ "--deviceId=" ]]; then
        deviceId=`echo "${paramValue}" | sed -E "s/--deviceId=(.*)/\1/"`
        deviceAdbCommand=" -s ${deviceId}"
        continue;
    fi

    if [[ "${paramValue}" =~ "--packageName=" ]]; then
        packageName=`echo "${paramValue}" | sed -E "s/--packageName=(.*)/\1/"`
        continue;
    fi

    if [[ "${paramValue}" =~ "--project=" ]]; then
        projectName=`echo "${paramValue}" | sed -E "s/--project=(.*)/\1/"`
        outputFolder="${projectName}/build/outputs/apk"
        continue;
    fi

    if [[ "${paramValue}" =~ "--build=" ]]; then
        _command=`echo "${paramValue}" | sed -E "s/--build=(.*)/\1/"`
        command="${command} assemble${_command}"
        continue;
    fi
done

for (( lastParam=1; lastParam<=$#; lastParam+=1 )); do
    paramValue="${!lastParam}"
    case ${paramValue} in
        "--clear-cache")
            "${adbCommand}""${deviceAdbCommand}" shell pm clear "${packageName}"
        ;;

        "--uninstall")
            "${adbCommand}""${deviceAdbCommand}" uninstall "${packageName}"
        ;;

        "--offline")
            offline=" --offline"
        ;;

        "--no-build")
            noBuild=" --no-build"
        ;;
    esac
done
echo

if [ "${packageName}" == "" ]; then
    printUsage
fi

if [ ! -d "${projectName}" ]; then
    printUsage "No project module named: '${projectName}'"
fi

if [ "${command}" == "" ] && [ "${noBuild}" == "" ]; then
    printUsage "MUST specify build type or set flag --no-build"
fi

if [ "${outputFolder}" == "" ]; then
    printUsage
fi

if [ "${noBuild}" == "" ]; then

    if [ -e "${outputFolder}" ]; then
        logInfo "deleting output folder:\n     ${outputFolder}"
        rm -rf "${outputFolder}"
    fi

    command="bash gradlew ${command}${offline}"

    logInfo "executing command:\n     ${command}"
    ${command}

    checkExecutionError "Build error..."
fi

pathToApk=`ls "${outputFolder}" | grep .*apk`

toExecute="${adbCommand}${deviceAdbCommand} install -r ${outputFolder}/${pathToApk}"
logDebug "installing apk:\n     ${toExecute}"
${toExecute}

toExecute="${adbCommand}${deviceAdbCommand} shell am start -n ${packageName}/com.nu.art.cyborg.ui.ApplicationLauncher -a android.intent.action.MAIN -c android.intent.category.LAUNCHER"
logDebug "Launching app:\n     ${toExecute}"
${toExecute}
