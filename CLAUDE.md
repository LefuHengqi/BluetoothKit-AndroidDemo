# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

Lefu (乐福) Bluetooth scale SDK for Android. The repo contains a demo application plus the full SDK source for three published artifacts:

| Module | Artifact | Purpose |
| --- | --- | --- |
| `:ppsdkkit:ppbasekit` | `com.lefu.ppbasekit:ppbasekit` | Base models (`PPDeviceModel`, `PPUserModel`, `PPScaleDefine`), logging |
| `:ppsdkkit:ppbluetoothkit` | `com.lefu.ppbluetoothkit:ppbluetoothkit` | BLE scan, connect, protocol parsing, OTA, WiFi config |
| `:ppsdkkit:ppcalculatekit` | `com.lefu.ppcalculatekit:ppcalculatekit` | Body-fat algorithms (4-electrode AC/DC, 8-electrode) |

`:app` is the demo (`applicationId com.lefu.demo`), `:data_db` is a GreenDAO device database. Version numbers come from `gradle.properties` (`LIB_VERSION`, `LIB_VERSION_CODE`), not from the module scripts.

## Build / Run / Test

The repo has only the POSIX `gradlew` script (no `gradlew.bat`). On Windows, run it from Git Bash. Wrapper is Gradle 8.9 / AGP 8.6.1 and needs JDK 17+ (JDK 21 is installed).

```bash
./gradlew :app:assembleDebug                  # debug APK
./gradlew :app:assembleRelease                # release APK; output is copied to ./apk/PPBluetoothKit_<version>_<timestamp>.apk
./gradlew :app:installDebug                   # install via adb

./gradlew test                                # all unit tests
./gradlew :app:testDebugUnitTest --tests "com.lefu.ppscale.wifi.ExampleUnitTest"   # single test

./gradlew :data_db:greendao                   # regenerate DAO classes after editing entities
```

SDK publishing (writes AARs + POMs into the repo-root `maven/` directory):

```bash
./gradlew :ppsdkkit:ppbasekit:publishReleasePublicationToMavenRepository
./gradlew :ppsdkkit:ppcalculatekit:publishReleasePublicationToMavenRepository
./gradlew :ppsdkkit:ppbluetoothkit:publishReleasePublicationToMavenRepository
# ppbluetoothkit also publishes the bundled jar:
./gradlew :ppsdkkit:ppbluetoothkit:publishBluetoothkitJarPublicationToMavenRepository
```

16 KB page-size (Android 15) checks:

```bash
./check_aar_16kb_support.sh path/to/library.aar
./check_16kb_so.sh path/to/dir/containing/so
```

Tests are only the generated `ExampleUnitTest`/`ExampleInstrumentedTest` stubs — there is no real test suite.

## Build layout quirks

- `ppsdkkit/` is a **git submodule** (`gitee.com/shenzhen-lfscale/ppsdkkit-android`). Its module `build.gradle` files are one-liners (`apply from: '../../ppbluetoothkit.gradle'`), so edit the root-level `ppbasekit.gradle`, `ppbluetoothkit.gradle`, `ppcalculatekit.gradle`.
- `ppbasiccalculatekit` and `bluetoothkit` are now Maven coordinates (`com.lefu.ppbasiccalculatekit:ppbasiccalculatekit:1.1.1`, `com.lefu:bluetoothkit:1.5.5`), not source modules. The empty root `ppbluetoothkit/` and `ppbasiccalculatekit/` directories are leftovers.
- Java targets differ deliberately: SDK library modules compile for Java 17, but `:app` targets Java 11 (a workaround for an AAR class-index issue noted in comments). Don't "normalize" them without testing.
- `compileSdk` also differs deliberately: app 35, ppbluetoothkit 35, ppbasekit/ppcalculatekit 31, data_db 34.
- `allprojects.repositories` puts `./maven` first. Stale locally published AARs there will shadow remote coordinates — delete them if you see unexpected SDK code.
- `app/build.gradle` has duplicate `defaultConfig`/`packagingOptions` blocks; Gradle merges them. Leave as-is unless intentionally refactoring.
- Building needs network access (Aliyun mirrors, JitPack, and a GitHub raw maven repo).

## SDK architecture

### 1. Initialization and device configuration

`PPApplication` calls:

1. `PPBluetoothKit.initSdk(context, appKey, appSecret, "lefu.config")` — creates the underlying `BluetoothClient` and loads a list of `DeviceConfigVo`.
2. `PPCalculateKit.initSdk(context)` — sets the context used to load `BodyParam.json` / `BodyParam_HT_8.json` from ppcalculatekit assets.

Device configs can come from an encrypted `.config` (AES key derived from AppKey+AppSecret, see `com.peng.ppscale.key.AppKey`), a plain `.json`, a raw JSON string (`setDeviceConfigJsonStr`), or the cloud (`setNetConfig`, used by the demo in `ext/MainActivityExt.kt`). The config list is what lets the SDK recognize and classify devices; entries with `sdkCode` newer than `PPBluetoothKit.getSdkCode()` (currently 2) are ignored. The demo package `com.lefu.demo` is special-cased to load the SDK's internal `lefudemo.config`.

### 2. Scan → classify → dispatch

```
PPSearchManager (singleton)
  → BleSearchDelegate → scan callbacks
  → DeviceFilterHelper.getDeviceModel()
        matches scanRecord against DeviceConfigVo by deviceName (full, then partial),
        advLength, macAddressStart, V3 signature, etc.
        → builds PPDeviceModel with protocol/connect/calcute/accuracy types
  → PPDeviceModel.getDevicePeripheralType()
        maps (connect type, protocol type, device type, calcute type)
        → PeripheralTorre / Borre / Dorre / Forre / Korre / Lorre / Morre
          / Apple / Banana / Coconut / Ice / ... (product codenames)
```

The demo's `ScanDeviceListActivity` performs the scan and routes to the matching `PeripheralXxxActivity` by `getDevicePeripheralType()`. New products generally require no new UI: they fall into an existing peripheral family based on `PPDeviceModel` classification.

### 3. Peripheral controllers

Every family has a controller in `ppsdkkit/ppbluetoothkit/.../device/PeripheralXxx/PPBlutoothPeripheralXxxController.kt`, extending `PPBlutoothPeripheralBaseController`:

- `startSearch` / `startConnect` / `disConnect`, connect retry and rate limiting.
- On successful connect it calls `ConnectHelper` to walk the GATT profile and pick notify/write/battery/OTA/device-info characteristics, then hands bytes to the family delegate (`TorreDelegate`, `BorreDelegate`, `ProtocalDelegate`, `ProtocalV4DataHelper`, ...).
- Status is reported to the app through `PPBleStateInterface.monitorBluetoothWorkState(PPBleWorkState, PPDeviceModel?)` (`PPBleWorkState` covers searching/connect/auth-failed/disconnected/writable, etc.).

The demo keeps one singleton instance per family (`device/instance/PPBlutoothPeripheralXxxInstance`) and passes the selected `PPDeviceModel` through the activity's `companion object`, so controllers are not recreated on navigation.

Downstream data/listener contracts live in `business/ble/listener/`: `PPDataChangeListener` (weight/impedance/heart rate), `PPHistoryDataInterface`, `PPDeviceInfoInterface`, `PPDeviceSetInfoInterface`, `PPUserInfoInterface`, `PPClearDataInterface`, `OnOTAStateListener`, etc. Family managers (`TorreDeviceManager`, `BorreDeviceManager`, ...) are obtained from the controller and expose product-specific operations (measure, light, sync log, WiFi config, DFU).

### 4. Calculation

`CalculateManager.calculateData(bodyBaseModel, bodyFatModel)` in ppcalculatekit dispatches by `PPDeviceModel.deviceCalcuteType` to `CalculateHelper4`, `CalculateHelper8`, `CalculateHelper4TwoArms`, `CalculateHelper4TwoChannel`, etc. The result is a `PPBodyFatModel`. `SecretManager` in the demo holds per-package auth secrets required for body-composition detection (the secret is tied to the app package name; replace it for real integrations).

### 5. Demo app flow

- `MainActivity` → `ScanDeviceListActivity` (scan + pick device) → `PeripheralXxxActivity` (connect, measure, device settings, OTA, WiFi) and `CalculateManagerActivity` (body-fat algorithms without a device).
- `PPApplication` initializes GreenDAO (`data_db`, DB file `pplibrary.db`), `SettingManager` prefs, file logging (`MyQueueLinkedUtils`), and routes SDK logs to `LogUtils` via `PPSDKKit.setDebugLogCallBack`.
- `LogActivity` and `FilterNameActivity` are debug helpers for exporting logs and filtering scan results.

## Conventions / gotchas

- Comments, log messages, and some identifiers are Chinese or mixed Chinese/English; match the surrounding style when editing.
- The demo's AppKey/AppSecret and `SecretManager` values are demo-only placeholders. `AppKey.NORMAL_APPKEY` throws if used for a non-demo package, so don't move demo init code into production paths.
- `PPDeviceModel` is the runtime model; `com.lefu.ppscale.db.dao.DeviceModel` is the persisted GreenDAO entity. Changing the entity requires `:data_db:greendao` and committing regenerated DAO sources.
- SDK release builds enable R8 (`minifyEnabled true`) for the library modules; keep ProGuard rules working when adding public API.
- When adding a new protocol/device family, also update: `PPScaleDefine.PPDeviceProtocolType`, `PPDeviceModel.getDevicePeripheralType()`, `DeviceFilterHelper`, the controller/delegate, and the enum in `PPBleWorkState` only if new states are genuinely required.
