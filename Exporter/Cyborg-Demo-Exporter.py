__author__ = 'tacb0ss'
import sys
import re
import os
import shutil

automation = False


def query_yes_no(question, default="yes"):
    """Ask a yes/no question via raw_input() and return their answer.

    "question" is a string that is presented to the user.
    "default" is the presumed answer if the user just hits <Enter>.
        It must be "yes" (the default), "no" or None (meaning
        an answer is required of the user).

    The "answer" return value is True for "yes" or False for "no".
    """
    valid = {"yes": True, "y": True, "ye": True,
             "no": False, "n": False}
    if default is None:
        prompt = " [y/n] "
    elif default == "yes":
        prompt = " [Y/n] "
    elif default == "no":
        prompt = " [y/N] "
    else:
        raise ValueError("invalid default answer: '%s'" % default)

    while True:
        sys.stdout.write(question + prompt)
        choice = input().lower()
        if default is not None and choice == '':
            return valid[default]
        elif choice in valid:
            return valid[choice]
        else:
            sys.stdout.write("Please respond with 'yes' or 'no' "
                             "(or 'y' or 'n').\n")


def getAARVersion(moduleName):
    extractVersionPattern = 'versionName\\s"(.*?)"'
    with open("../" + moduleName + "/build.gradle", 'r') as f:
        for line in f:
            matches = re.search(extractVersionPattern, line)
            if not matches or not matches.groups():
                continue

            groups = matches.groups()
            return groups[0]

    return "missing-version"


def getJavaVersion(moduleName):
    extractVersionPattern = 'version\s(?:\"|\')(.*?)(?:\"|\')'
    with open("../" + moduleName + "/build.gradle", 'r') as f:
        for line in f:
            matches = re.search(extractVersionPattern, line)
            if not matches or not matches.groups():
                continue

            groups = matches.groups()
            return groups[0]

    return None


def clearTemplateProject(pathToProjectRoot):
    libsFolder = pathToProjectRoot + "/libs"
    if os.path.exists(libsFolder):
        shutil.rmtree(libsFolder)
    os.makedirs(libsFolder)

    gradleFile = pathToProjectRoot + "/build.gradle"
    if os.path.exists(gradleFile):
        os.remove(gradleFile)

    srcFolder = pathToProjectRoot + "/src"
    if os.path.exists(srcFolder):
        shutil.rmtree(srcFolder)

    return libsFolder, srcFolder


def copyJar(moduleName, targetFolder):
    version = getJavaVersion(moduleName)
    if version:
        fileToCopy = "../%s/build/libs/%s-%s.jar" % (moduleName, moduleName, version)
    else:
        fileToCopy = "../%s/build/libs/%s.jar" % (moduleName, moduleName)

    if not os.path.exists(fileToCopy):
        raise Exception("Could not locate jar file for module: " + moduleName + " -- At: " + fileToCopy)

    if version:
        targetFileName = "%s/%s-%s.jar" % (targetFolder, moduleName, version)
    else:
        targetFileName = "%s/%s.jar" % (targetFolder, moduleName)

    shutil.copyfile(fileToCopy, targetFileName)
    return moduleName, version


def copyAAR(moduleName, targetFolder):
    moduleVersion = getAARVersion(moduleName)
    fileToCopy = "../%s/build/outputs/aar/%s-v%s.aar" % (moduleName, moduleName, moduleVersion)
    if not os.path.exists(fileToCopy):
        raise Exception("Could not locate aar file for module: " + moduleName + " -- At: " + fileToCopy)

    fileName = moduleName + "-" + moduleVersion
    shutil.copyfile(fileToCopy, targetFolder + "/" + fileName + ".aar")
    return fileName, moduleVersion


def addPreZeros(sdkVersion):
    versionDetails = sdkVersion.split(".")
    if len(versionDetails[len(versionDetails) - 1]) < 3:
        versionDetails[len(versionDetails) - 1] = "0%s" % versionDetails[len(versionDetails) - 1]
    if len(versionDetails[len(versionDetails) - 1]) < 3:
        versionDetails[len(versionDetails) - 1] = "0%s" % versionDetails[len(versionDetails) - 1]
    sdkVersion = ''
    for b in versionDetails:
        sdkVersion = "%s.%s" % (sdkVersion, b)
    sdkVersion = sdkVersion[1:]
    return sdkVersion


def makeAndroidStudioArchive():
    projectName = "Cyborg Demo App"
    sdkExporterFolderName = "Cyborg for Android"
    sdkExporterFolder = "./%s" % sdkExporterFolderName
    templateProjectFolder = "%s/%s" % (sdkExporterFolder, projectName)
    demoAppName = "cyborg-demo-app"
    templateModuleFolder = "%s/%s" % (templateProjectFolder, demoAppName)
    libsFolder, srcFolder = clearTemplateProject(templateModuleFolder)
    cyborgCoreAAR, cyborgCoreVersion = copyAAR("cyborg-core", libsFolder)
    cyborgCoreVersion = addPreZeros(cyborgCoreVersion)

    nuArtCoreJar = copyJar("nu-art-core", libsFolder)
    nuArtReflectionJar = copyJar("reflection", libsFolder)
    nuArtModuleManagerJar = copyJar("module-manager", libsFolder)
    # nuArtArchiverJar = copyJar("archiver", libsFolder)
    # imageCurlEffectAAR, imageCurlEffectVersion = copyAAR("image-curl-effect", libsFolder)
    # nuArtPdfSdkAAR, cyborgCoreVersion = copyAAR("nu-art-pdf-sdk", libsFolder)
    demoAppVersion = getAARVersion(demoAppName)

    outputFolderName = "/home/tacb0ss/Cloud/Dropbox/Projects/Cyborg Demo/SDKs Releases/%s" % cyborgCoreVersion
    outputFileName = "%s/Cyborg-for-Android_v%s" % (outputFolderName, cyborgCoreVersion)
    if not automation:
        if os.path.exists(outputFolderName):
            if not query_yes_no("Version %s already exported... Override???" % cyborgCoreVersion, None):
                return
        else:
            os.makedirs(outputFolderName)

    dependencies = [cyborgCoreAAR]
    aarImport = ""
    for dependency in dependencies:
        aarImport += "\tcompile(name:'%s', ext:'aar')\n" % dependency

    shutil.copytree("../%s/src" % demoAppName, templateModuleFolder + "/src")
    with open("./build.gradle.orig", 'r') as f:
        data = f.read()

    data = data.replace("/*$(Dependencies)*/", aarImport)
    with open(templateModuleFolder + "/build.gradle", 'w') as f:
        f.write(data)

    demoAppApkFile = sdkExporterFolder + "/Cyborg-for-Android-Demo v%s.apk" % cyborgCoreVersion
    shutil.copyfile("../%s/build/outputs/apk/Cyborg-for-Android-Demo v%s.apk" % (demoAppName, demoAppVersion), demoAppApkFile)
    shutil.copyfile("../%s/build/outputs/apk/Cyborg-for-Android-Demo v%s.apk" % (demoAppName, demoAppVersion),
                    "%s/Cyborg-for-Android v%s.apk" % (outputFolderName, cyborgCoreVersion))

    shutil.make_archive(outputFileName, 'zip', "./", base_dir=sdkExporterFolderName)
    os.remove(demoAppApkFile)
    clearTemplateProject(templateModuleFolder)
    print("Output Zip: " + outputFileName + ".zip")

    if automation:
        return

    if not query_yes_no("Would you like to add release notes???", None):
        return

    releaseNotes = input("Enter Release Notes:")
    releaseNotes = releaseNotes.replace("\\n", "\n")
    with open("%s/release-notes.txt" % outputFolderName, 'a') as f:
        f.write(releaseNotes)


def main(argv):
    global automation
    print("Exporting Android Studio version")
    if len(argv) > 0:
        automation = argv[0] == "True"
    makeAndroidStudioArchive()


if __name__ == "__main__":
    main(sys.argv[1:])