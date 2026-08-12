# app-demo-cyborg-x — Cyborg bootstrap (AndroidX)

Minimal Cyborg app on **`cyborg-x`** (AndroidX). This is the working bootstrap for the monorepo under AGP 8 / Gradle 8.

Sky how-tos → `/infra/cyborg` (especially → `/infra/cyborg/bootstrap`).

## Prerequisites

- **JDK 17** (`JAVA_HOME`)
- Android SDK (`local.properties` → `sdk.dir`, or `ANDROID_HOME`)

## Runtime note

Do **not** exclude `com.google.guava:listenablefuture` on this app. AndroidX `ProfileInstaller` needs the real `listenablefuture:1.0` (+ `androidx.concurrent:concurrent-futures`). The empty `9999.0-empty-to-avoid-conflict-with-guava` stub will crash the process shortly after launch.

## Build

From the cyborg repo root:

```bash
export JAVA_HOME=/path/to/jdk-17
./gradlew :app-demo-cyborg-x:assembleDebug
```

APK: `app-demo-cyborg-x/build/outputs/apk/debug/v*-D--basic-cyborg-x-app.apk`.

## Install + launch

With a device or emulator connected:

```bash
./gradlew :app-demo-cyborg-x:installDebug
adb shell am start -n com.nu.art.cyborgX/com.nu.art.cyborg.ui.ApplicationLauncher
```

Or install the APK directly:

```bash
adb install -r app-demo-cyborg-x/build/outputs/apk/debug/*.apk
adb shell am start -n com.nu.art.cyborgX/com.nu.art.cyborg.ui.ApplicationLauncher
```

## What it shows

- `MyApplication` → `CyborgBuilder` + `ModulePack_HelloWorld`
- Launch layout with a nested `CyborgStackController` and Hello World controllers
- Click/long-click on the bottom buttons pushes stack layers

Standalone GitHub template (older AGP; not the AGP 8 reference): https://github.com/nu-art/basic-cyborg-app
