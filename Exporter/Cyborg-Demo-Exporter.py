__author__ = 'tacb0ss'
import re
import os
import shutil


def getSDKVersion(moduleName):
    extractVersionPattern = 'versionName\\s"(.*?)"'
    with open("../" + moduleName + "/build.gradle", 'r') as f:
        for line in f:
            matches = re.search(extractVersionPattern, line)
            if not matches or not matches.groups():
                continue

            groups = matches.groups()
            return groups[0]

    return "missing-version"


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
    fileToCopy = "../" + moduleName + "/build/libs/" + moduleName + ".jar"
    if not os.path.exists(fileToCopy):
        raise Exception("Could not locate jar file for module: " + moduleName + " -- At: " + fileToCopy)
    shutil.copyfile(fileToCopy, targetFolder + "/" + moduleName + ".jar")
    return moduleName


def copyAAR(moduleName, targetFolder):
    moduleVersion = getSDKVersion(moduleName)
    fileToCopy = "../%s/build/outputs/aar/%s-v%s.aar" % (moduleName, moduleName, moduleVersion)
    if not os.path.exists(fileToCopy):
        raise Exception("Could not locate aar file for module: " + moduleName + " -- At: " + fileToCopy)

    fileName = moduleName + "-" + moduleVersion
    shutil.copyfile(fileToCopy, targetFolder + "/" + fileName + ".aar")
    return fileName, moduleVersion


def makeAndroidStudioArchive():
    projectName = "Cyborg Demo App"
    sdkExporterFolderName = "Cyborg for Android"
    sdkExporterFolder = "./%s" % sdkExporterFolderName
    templateProjectFolder = "%s/%s" % (sdkExporterFolder, projectName)
    demoAppName = "cyborg-demo-app"
    templateModuleFolder = "%s/%s" % (templateProjectFolder, demoAppName)
    libsFolder, srcFolder = clearTemplateProject(templateModuleFolder)

    nuArtCoreJar = copyJar("nu-art-core", libsFolder)
    nuArtReflectionJar = copyJar("reflection", libsFolder)
    nuArtModuleManagerJar = copyJar("module-manager", libsFolder)
    # nuArtArchiverJar = copyJar("archiver", libsFolder)
    cyborgCoreAAR, sdkVersion = copyAAR("cyborg-core", libsFolder)
    # imageCurlEffectAAR, imageCurlEffectVersion = copyAAR("image-curl-effect", libsFolder)
    # nuArtPdfSdkAAR, sdkVersion = copyAAR("nu-art-pdf-sdk", libsFolder)
    demoAppVersion = getSDKVersion(demoAppName)

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

    demoAppApkFile = sdkExporterFolder + "/Cyborg-for-Android-Demo v%s.apk" % sdkVersion
    shutil.copyfile("../%s/build/outputs/apk/Cyborg-for-Android-Demo v%s.apk" % (demoAppName, demoAppVersion), demoAppApkFile)
    shutil.copyfile("../%s/build/outputs/apk/Cyborg-for-Android-Demo v%s.apk" % (demoAppName, demoAppVersion),
                    "/home/tacb0ss/Cloud/Dropbox/Projects/Cyborg Demo/SDKs Releases/Cyborg-for-Android v%s.apk" % sdkVersion)
    outputFileName = "/home/tacb0ss/Cloud/Dropbox/Projects/Cyborg Demo/SDKs Releases/Cyborg-for-Android_v" + sdkVersion
    shutil.make_archive(outputFileName, 'zip', "./", base_dir=sdkExporterFolderName)
    os.remove(demoAppApkFile)
    clearTemplateProject(templateModuleFolder)
    print("Output Zip: " + outputFileName)


def main():
    print("Exporting Android Studio version")
    makeAndroidStudioArchive()


if __name__ == "__main__":
    main()