# Rear Screen Bypass LSPosed

LSPosed module for Xiaomi devices with a rear display where rear-screen theme apply breaks after root.

This project hooks Xiaomi `ThemeManager` and `SubScreenCenter` to keep rear-display theme resources valid after rooting, with a focus on fixing apply failures instead of replacing Xiaomi components.

## What It Does

- Rewrites rear-screen widget paths before ThemeManager sends them to SubScreenCenter.
- Keeps SubScreenCenter from rejecting otherwise valid rear-screen widgets because of path resolution failures.
- Pre-stages missing rear-theme rights and resource files when Xiaomi's apply flow expects them in internal runtime storage.
- Preserves already-working themes while fixing built-in and downloaded themes that fail only after root.

## Scope

This module is intended for:

- rooted Xiaomi / HyperOS devices
- LSPosed users
- rear-display theme apply failures involving `com.android.thememanager` and `com.xiaomi.subscreencenter`

This repository does not include Xiaomi firmware, Xiaomi APKs, or decompiled Xiaomi source.

## Install

1. Install the APK.
2. Enable the module in LSPosed.
3. Scope it to:
   - `com.android.thememanager`
   - `com.xiaomi.subscreencenter`
4. Reboot.

Debug APK path after local build:

`app/build/outputs/apk/debug/app-debug.apk`

## Issues

If it does not work, send issues with log files.

` adb logcat -c`

` adb logcat -v time > log.txt`

## Build

Requirements:

- JDK 17
- Android SDK
- `local.properties` with a valid `sdk.dir`

Build a debug APK:

```cmd
gradlew.bat --no-daemon :app:assembleDebug
```

Build a release APK:

```cmd
gradlew.bat --no-daemon :app:assembleRelease
```

Optional helper scripts:

```cmd
build-debug.cmd
```

```cmd
build-release.cmd
```

```powershell
powershell -ExecutionPolicy Bypass -File .\build-debug.ps1
```

## Development Notes

- The project uses a compile-only Xposed API jar at `app/libs/api-82.jar`.
- The runtime API is still provided by LSPosed on-device.
- The main hook entrypoint is [`RearScreenBypass.java`](app/src/main/java/com/codex/rearscreenfix/RearScreenBypass.java).

## Open Source Notes

If you publish this project, only publish this module project directory. Do not publish:

- firmware zips
- pulled Xiaomi APKs
- decompiled Xiaomi files
- log captures with personal device data

## Releasing

See [RELEASING.md](RELEASING.md).
