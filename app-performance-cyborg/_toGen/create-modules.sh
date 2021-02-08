#!/bin/bash

rm ./src/main/java/com/nu/art/cyborg/performance/modules/Module*
moduleJava=`cat module.java`
for (( index=1; index<=100; index+=1 )); do
    moduleFile=./src/main/java/com/nu/art/cyborg/performance/modules/Module${index}.java
    moduleFileJava=`echo -e ${moduleJava} | sed -E "s/ModuleXX/Module${index}/"`
#    moduleFileJava=${moduleJava}
    echo -e "${moduleFileJava}" > ${moduleFile}
done