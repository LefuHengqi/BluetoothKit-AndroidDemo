# 快速链接 / Quick Links

| 中文链接 | English Links |
|---------|---------------|
| [Android SDK概述](https://xinzhiyun.feishu.cn/docx/SbKCdKZhJobiNYxQLEdc7XgUnYc) | [Android SDK Overview](https://xinzhiyun.feishu.cn/docx/M6g2d4WAMoPCErxsUZAckymRnbd) |
| [Android SDK 集成方式](https://xinzhiyun.feishu.cn/docx/OD4rdSlWrobDIZxn0CBcD68bn1c) | [Android SDK Integration](https://xinzhiyun.feishu.cn/docx/Lw0idhnjYoqZsqxtxvfcGuS8nac) |
| [Android SDK 设备扫描](https://xinzhiyun.feishu.cn/docx/SE2GdDxg1odTT5xgSbscB0F7nv7) | [Android SDK Device Scanning](https://xinzhiyun.feishu.cn/docx/MOkZdHf0fo4S4YxqWjycaXJPnXf) |
| [Android SDK 人体秤接入](https://xinzhiyun.feishu.cn/docx/HGrqdpGqioulIhxhsbwcmlLkn7g) | [Android SDK Integrated body scale](https://xinzhiyun.feishu.cn/docx/Im7CdLR14oQ74vxBUZpcEpRrndB) |
| [Android SDK厨房秤接入](https://xinzhiyun.feishu.cn/docx/ILwQdEPoCozXSDxAQufcvOffn4f) | [Android SDK Integrated kitchen scale](https://xinzhiyun.feishu.cn/docx/VB3pd6xO1oPXbexoof8cSsEGnAh) |
| [Android SDK 计算库使用](https://xinzhiyun.feishu.cn/docx/L0UxdNKFPorB77xBjnmcqijtnVb) | [Android SDK Calculation library usage](https://xinzhiyun.feishu.cn/docx/HxxTdPd24oDWjyx4QfHccZUVnbd) |
| [Android示例程序地址](https://github.com/LefuHengqi/BluetoothKit-AndroidDemo) | [Android sample program address](https://github.com/LefuHengqi/BluetoothKit-AndroidDemo) |
| [乐福开放平台](https://uniquehealth.lefuenergy.com/unique-open-web/#/home) | [Lefu Open Platform](https://uniquehealth.lefuenergy.com/unique-open-web/#/home) |
| [乐福开放平台配置设备指南](https://xinzhiyun.feishu.cn/docx/VxXUdI86sorr96xSdn1cakkZndd) | [Lefu Open Platform Configuration Device Guide](https://xinzhiyun.feishu.cn/docx/Gw38d5JskoShyFxKnwIcvIaznhb) |
| [iOS SDK概述](https://xinzhiyun.feishu.cn/docx/MaHEdRx7Bo02Q8x60pFcrzRgnkf) | [iOS SDK Overview](https://xinzhiyun.feishu.cn/docx/FZYwd286NouHxoxXVpccUCZ9n4c) |
| [常见问题](https://xinzhiyun.feishu.cn/docx/Y3HgdVqXHoGhZqxG9BscGxCPnhh) | [Android FAQs](https://xinzhiyun.feishu.cn/docx/VDDGdJsuxoMwRZxf56ZcjReRnrh) |



# 概述

## Android SDK

当前版本：**4.6.29**

| 模块             | 描述     | 必须     | 集成方式                                                |
| -------------- | ------ | ------ |-----------------------------------------------------|
| ppbasekit      | 基础模块   | 是      | api 'com.lefu.ppbasekit:ppbasekit:4.6.29'           |
| ppbluetoothkit | 蓝牙协议模块 | 根据业务选择 | api 'com.lefu.ppbluetoothkit:ppbluetoothkit:4.6.29' |
| ppcalculatekit | 体脂计算库  | 根据业务选择 | api 'com.lefu.ppcalculatekit:ppcalculatekit:4.6.29' |

PPBaseKit是基础模块，必须集成

PPBluetoothKit是针对人体秤和食物秤进行封装的SDK，包含蓝牙扫描、蓝牙连接逻辑、数据解析逻辑、OTA升级、WiFi配网等功能。依赖 `com.lefu:bluetoothkit:1.5.5`

PPCalculateKit是针对体脂计算封装的SDK，包含了4电极交流算法、4电极直流算法、8电极交流算法。依赖 `com.lefu.ppbasiccalculatekit:ppbasiccalculatekit:1.1.1`

### 仓库配置

在工程的 `settings.gradle`（或根目录 `build.gradle` 的 `allprojects.repositories`）中添加乐福 Maven 仓库：

```groovy
repositories {
    google()
    mavenCentral()
    maven { url "https://raw.githubusercontent.com/LefuHengqi/PPBaseKit-Android/main" }
    maven { url "https://jitpack.io" }
}
```

然后在 App 模块中添加依赖：

```groovy
dependencies {
    api 'com.lefu.ppbasekit:ppbasekit:4.6.29'
    api 'com.lefu.ppbluetoothkit:ppbluetoothkit:4.6.29'
    api 'com.lefu.ppcalculatekit:ppcalculatekit:4.6.29'
    api 'com.lefu:bluetoothkit:1.5.5'
    api 'com.lefu.ppbasiccalculatekit:ppbasiccalculatekit:1.1.1'
}
```

SDK 已支持 Android 15 的 16 KB 内存页大小，可使用仓库根目录的 `check_aar_16kb_support.sh` / `check_16kb_so.sh` 进行校验。

## 示例程序

为了让客户快速实现称重以及对应的功能而实现，提供了一个示例程序，示例程序中包含体脂计算模块和设备功能模块。

* 设备功能模块目前支持的设备包含：蓝牙秤/食物秤/蓝牙WiFi秤，以及 Apple/Banana/Coconut/Durian/Egg/Fish/Grapes/Hamburger/Ice/Jambul/Torre/Borre/Dorre/Forre/Korre/Lorre/Morre 等协议系列设备。扫描到设备后，示例程序会根据设备类型自动进入对应的功能页面。

* 设备功能模块包括：连接与测量、测量结果展示及自动计算体脂、历史数据同步、设备用户与成员管理、WiFi配网、设备服务器域名配置、OTA升级、日志导出、自动指令执行等（具体以设备支持情况为准）。

* 体脂计算模块支持4电极交流算法、4电极直流算法、8电极交流算法。

* [GitHub - LefuHengqi/BluetoothKit-AndroidDemo](https://github.com/LefuHengqi/BluetoothKit-AndroidDemo)

![示例程序](icon/ppbluetothkit.PNG)

### 编译运行

* 环境要求：JDK 17+、Android Gradle Plugin 8.6.1、Gradle 8.9，示例程序 `minSdkVersion 24`、`compileSdk 35`。
* 编译：`./gradlew :app:assembleDebug`（Windows 下请在 Git Bash 中执行）。
* 安装：`./gradlew :app:installDebug`。
* 示例程序中的 AppKey/AppSecret 及体脂计算密钥仅用于演示（与包名 `com.lefu.demo` 绑定），正式接入请在[乐福开放平台](https://uniquehealth.lefuenergy.com/unique-open-web/#/home)申请并替换。

## 商用版本程序

* 可在GooglePlay或其他应用商店搜索下载 "Unique Health"

* 也可扫描下面的二维码进行下载

![示例程序](icon/uniquehealth.PNG)

  
# 常见问题

[ Android 常见问题](https://xinzhiyun.feishu.cn/docx/Y3HgdVqXHoGhZqxG9BscGxCPnhh)
