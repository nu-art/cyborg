#!/bin/bash

bash ./dev-tools/scripts/dev/android/build-and-install.sh \
     --package-name=com.nu.art.cyborgX \
     --launcher-class=com.nu.art.cyborg.ui.ApplicationLauncher \
     --project=app-demo-cyborg-x \
     --app-name="cyborg-demo" \
     --build=debug \
     "$@"