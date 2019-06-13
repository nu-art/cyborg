#!/bin/bash

bash ./dev-tools/scripts/dev/android/build-and-install.sh \
     --package-name=com.nu.art.cyborg.demo \
     --launcher-class=com.nu.art.cyborg.ui.ApplicationLauncher \
     --project=app-demo-cyborg \
     --app-name="cyborg-demo" \
     --build=debug \
     "$@"