# Releasing

## 1. Bump the version

Edit `app/build.gradle`:

- Increase `versionCode`
- Set the next `versionName`

## 2. Build the APK

Debug build:

```cmd
gradlew.bat --no-daemon :app:assembleDebug
```

Release build:

```cmd
gradlew.bat --no-daemon :app:assembleRelease
```

Outputs:

- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release-unsigned.apk`

## 3. Sign the release APK

`assembleRelease` produces an unsigned APK. Sign it before publishing.

Create a keystore once:

```cmd
keytool -genkeypair -v -keystore rearscreenfix-release.jks -alias rearscreenfix -keyalg RSA -keysize 4096 -validity 10000
```

Sign the APK:

```cmd
"C:\portable apps\Android\toolchain\android-sdk\build-tools\34.0.0\apksigner.bat" sign --ks rearscreenfix-release.jks --out app-release.apk app\build\outputs\apk\release\app-release-unsigned.apk
```

Verify the signature:

```cmd
"C:\portable apps\Android\toolchain\android-sdk\build-tools\34.0.0\apksigner.bat" verify --print-certs app-release.apk
```

## 4. Test on device

```cmd
adb install -r "app-release.apk"
```

Enable the module in LSPosed for:

- `com.android.thememanager`
- `com.xiaomi.subscreencenter`

Then reboot and verify rear-screen theme apply still works.

## 5. Create a Git tag

Example for version `1.0.0`:

```cmd
git add .
git commit -m "Release 1.0.0"
git tag v1.0.0
git push origin main
git push origin v1.0.0
```

## 6. Create a GitHub Release

On GitHub:

1. Open the repository.
2. Open `Releases`.
3. Click `Draft a new release`.
4. Choose tag `v1.0.0`.
5. Title it `v1.0.0`.
6. Add short release notes.
7. Upload `app-release.apk`.
8. Publish the release.

## 7. What not to commit

Do not publish:

- Xiaomi firmware files
- Decompiled Xiaomi APK contents
- Personal logs
- `local.properties`
- Built APKs inside git history
