[English Docs](README_EN.md)  |  [中文文档](README.md)

## 相关文档

[乐福开放平台](https://uniquehealth.lefuenergy.com/unique-open-web/#/document)  |
[PPBluetoothKit iOS SDK](https://uniquehealth.lefuenergy.com/unique-open-web/#/document?url=https://lefuhengqi.apifox.cn/doc-2625647)  |
[PPBluetoothKit 微信小程序插件](https://uniquehealth.lefuenergy.com/unique-open-web/#/document?url=https://lefuhengqi.apifox.cn/doc-2625745)

# PPBluetoothKit Android SDK

[ Android sample program address ](https://gitee.com/shenzhen-lfscale/bluetooth-kit-android-demo.git)

## LF Bluetooth Scale/Food Scale/WiFi Scale

### SDK Description

PPBluetoothKit is an integrated SDK including Bluetooth connection logic and data parsing logic.
In order to allow customers to quickly realize weighing and corresponding functions, it contains sample programs, which include body fat calculation modules and equipment function modules.

1. The devices currently supported by the device function module include: Bluetooth scales, food scales, and Torre series Bluetooth WiFi body fat scales.
2. The body fat calculation module supports 4-electrode AC algorithm, 4-electrode DC algorithm, and 8-electrode AC algorithm.

### Commercial version program:

- You can search and download "Unique Health" in each application market

## Ⅰ. Integration method

### 1.1 sdk import method

-Add it to build.gradle under the module that needs to import sdk (please check the libs under the module of ppscalelib for the latest version)

```
    dependencies {
        //aar import
        //api fileTree(include: ['*.jar', '*.aar'], dir: 'libs')
        api(name: 'ppblutoothkit-3.1.0-20230829.165034-1', ext: 'aar')
    }

```

- If you encounter an error related to "AndroidManifest.xml" after integration, please try to add the following code to the main module to solve it:

```
android {
    ```
    packagingOptions {
        exclude 'AndroidManifest.xml'
        ```
        }
    }
```

- If you encounter a ".so" type file error, please try to clear the cache and change the way of integrating SDK to API

## Ⅱ . Instructions for use

### 1.1 Bluetooth permission related

#### 1.1.1 Operating environment

Due to the need for Bluetooth connection, Demo needs to run on a real device, Android phone 6.0 and above or HarmonyOS2.0 and above

#### 1.1.2 Conventions related to Bluetooth permissions

In the process of using the demo, you need to turn on the bluetooth and turn on the location switch, and make sure to turn on and authorize the necessary permissions: precise location permissions and nearby device permissions
You can view the official Bluetooth permissions document, the document address: [ Google developer website about Bluetooth permissions instructions ](https://developer.android.com/guide/topics/connectivity/bluetooth/permissions) .

- Precise positioning permissions
- Permissions for nearby devices
- Position switch
- Bluetooth switch

```
<manifest>
    <!-- Request legacy Bluetooth permissions on older devices. -->
    <uses-permission android:name="android.permission.BLUETOOTH"
    android:maxSdkVersion="30" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN"
    android:maxSdkVersion="30" />

    <!-- Needed only if your app looks for Bluetooth devices.
    If your app doesn't use Bluetooth scan results to derive physical
    location information, you can strongly assert that your app
    doesn't derive physical location. -->
    <uses-permission android:name="android.permission.BLUETOOTH_SCAN" />

    <!-- Needed only if your app makes the device discoverable to Bluetooth
    devices. -->
    <uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE" />

    <!-- Needed only if your app communicates with already-paired Bluetooth
    devices. -->
    <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />

    <!-- Needed only if your app uses Bluetooth scan results to derive physical location. -->
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    ...
</manifest>

```

#### 1.1.3 Conventions related to measuring body data

##### 1.1.3.1 Weigh and measure fat:

1. The scale supports fat measurement
2. Weigh on bare feet and touch the corresponding electrode sheet
3. The weighing interface returns weight (kg) and impedance information
4. Assign the weight (kg) and impedance information, together with the user's personal information objects (height, age, gender), to PPBodyBaseModel,
5. Pass the PPBodyBaseModel object created in 4 to PPBodyFatModel to generate a PPBodyFatModel object
6. PPBodyFatModel contains various body fat information

##### 1.1.3.2 Calculation of body fat:

1. Need height, age, gender and corresponding impedance, call the corresponding calculation library to obtain
2. The body fat data items involved in the 8 electrodes need to be supported by the scale before they can be used

#### 1.1.4 SDK initialization

PPBlutoothKit.init(this)

### 1.2 Home page function description

#### 1.2.1 Caclulate - CalculateManagerActivity

According to the body weight and impedance analyzed by the Bluetooth protocol, plus the height, age, and gender of the user data, the body fat rate and other body fat parameter information are calculated.

##### 1.2.1.1 8-electrode AC body fat calculation - 8AC - Calculate8Activitiy

Content that can be entered:

The range of height: 30-220 cm;
The value range of age: 10-99 years old;
Gender 1 represents male, 0 represents female;
Weight: 0-220kg can be input
Impedance values of 10 body parts: Please enter according to the impedance returned by the scale

PPUserModel parameter description:

userHeight, age, sex must be real
The range of userHeight is 100-220cm
age age range is 10-99
sex Gender 0 is female and 1 is male

PPDeviceModel parameter configuration:

    //8 electrode calculation type
    deviceModel.deviceCalcuteType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8

##### 1.2.1.2 4 Electrode DC Body Fat Calculation - 4DC - Calculate4DCActivitiy

Content that can be entered:

The range of height: 30-220 cm;
The value range of age: 10-99 years old;
Gender 1 represents male, 0 represents female;
Weight: 0-220kg can be input
Impedance value of both feet: please input according to the impedance returned by the scale

PPUserModel parameter description:

userHeight, age, sex must be real
The range of userHeight is 100-220cm
age age range is 10-99
sex Gender 0 is female and 1 is male

PPDeviceModel parameter configuration:

    //4 electrode DC calculation type
    deviceModel.setDeviceCalcuteType(PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeDirect)

##### 1.2.2.3 4-electrode AC fluid grease calculation - 4AC - Calculate4ACActivitiy

Content that can be entered:

The range of height: 30-220 cm;
The value range of age: 10-99 years old;
Gender 1 represents male, 0 represents female;
Weight: 0-220kg can be input
Impedance value of both feet: please input according to the impedance returned by the scale

PPUserModel parameter description:

userHeight, age, sex must be real
The range of userHeight is 100-220cm
age age range is 10-99
sex Gender 0 is female and 1 is male

PPDeviceModel parameter configuration:

    //4-electrode AC calculation type
    deviceModel.setDeviceCalcuteType(PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate)

#### 1.2.3 Device scan surrounding supported devices - ScanDeviceListActivity

Scan the list of nearby devices - a list of nearby body fat scales, you can choose a device to connect and other function examples
Scanned device categories:

| Category Name       | Use Example Class           | Connection Method              | Device Type   |
| ------------------- | --------------------------- | ------------------------------ | ------------- |
| PeripheralApple     | PeripheralAppleActivity     | Connection                     | Body Scale    |
| PeripheralBanana    | PeripheralBananaActivity    | Broadcasting                   | Body Scale    |
| PeripheralCoconut   | PeripheralCoconutActivity   | Connection                     | Body Scale    |
| PeripheralDurian    | PeripheralDutianActivity    | Connection to Device Computing | Body Scale    |
| PeripheralEgg       | PeripheralEggActivity       | Connection                     | Kitchen Scale |
| PeripheralFish      | PeripheralFishActivity      | Connect                        | Kitchen Scale |
| PeripheralGrapes    | PeripheralGrapesActivity    | Broadcast                      | Kitchen Scale |
| PeripheralHamburger | PeripheralHamburgerActivity | Broadcast                      | Kitchen Scale |
| PeripheralTorre     | PeripheralTorreActivity     | Connection                     | Body Scale    |

- Get the device category name

Scanning the device will return the deviceModel object, through the device method: deviceModel.getDevicePeripheralType(), get the device classification, distinguish your own device according to PPDevicePeripheralType, and correspond to the Activity of the device function example.

    /**
    * Equipment grouping type
    */
    enum class PPDevicePeripheralType {
        //2.x/connect/body scales
        Peripheral Apple,
        //2.x/broadcast/body scales
        Peripheral Banana,
        //3.x/connect/body scales
        Peripheral Coconut,
        //2.x /Connection of device-side calculation/body scale
        Peripheral Durian,
        //2.x/connect/kitchen-scale
        Peripheral Egg,
        //3.x/connect/kitchen-scale
        Peripheral Fish,
        //2.x/broadcast/kitchen scales
        Peripheral Grapes,
        //3.x/broadcast/kitchen scales
        Peripheral Hamburger,
        //torre/connect/body scales
        Peripheral Torre;
    }

-Start scanning- Corresponding method: startSearchDeviceList

```
    /**
    * Get around bluetooth scale devices
    */
    public void startScanDeviceList() {
        if (ppScale == null) {
        ppScale = new PPScale(this);
    }
    ppScale.startSearchDeviceList(300000, searchDeviceInfoInterface，bleStateInterface); //You can  dynamically set the scan time in ms
    }
```

-stop scanning

```
    if (ppScale != null) {
        ppScale. stopSearch();
    }
```

- rescan
```
public void delayScan() {
    new Handler(getMainLooper()). postDelayed(new Runnable() {
    @Override
    public void run() {
        if (isOnResume) {
        startScanDeviceList();
        }
    }
}, 1000);

}
```

-Search device information callback searchDeviceInfoInterface: PPSearchDeviceInfoInterface

Provide a callback method:

```
/**
* Scan the device, return a single device information, and use it as an external search list
*
* @param deviceModel device object
*/
void onSearchDevice(PPDeviceModel deviceModel);
```

- Bluetooth state callback bleStateInterface: PPBleStateInterface

```
/**
* Bluetooth scanning and connection status monitoring
*
* @param ppBleWorkState
*/
public void monitorBluetoothWorkState(PPBleWorkState ppBleWorkState, PPDeviceModel deviceModel) {
}

/**
* Bluetooth switch state callback
*
* @param ppBleSwitchState
*/
public void monitorBluetoothSwitchState(PPBleSwitchState ppBleSwitchState) {

}

/**
* Only supports Torre: Torre device MTU request is returned after success
*
* @param deviceModel
*/
public void monitorMtuChange(PPDeviceModel deviceModel) {

}
```

### Ⅲ WiFi function description

#### PeripheralApple Wifi Functionality = BleConfigWifiActivity.java

Only some devices support WiFi
Check if Wifi is supported:

    /**
    * Whether Wi-Fi is supported
    *
    * @param device
    * @return
    */
    fun isFuncTypeWifi(device: PPDeviceModel?): Boolean {
        return if (device != null) {
        (device.deviceFuncType and PPScaleDefine.PPDeviceFuncType.PPDeviceFuncTypeWifi.getType()
        == PPScaleDefine.PPDeviceFuncType.PPDeviceFuncTypeWifi.getType())
            } else {
            false
        }
    }

##### 1.1 Precautions

The default Server domain name address is: https://api.lefuenergy.com

1. Make sure the Server is normal and the router can connect to the Server normally
2. Make sure the WiFi environment is 2.4G or 2.4/5G mixed mode, does not support single 5G mode
3. Make sure the account password is correct
4. Make sure that the server address used by the scale end corresponds to the server address used by the app

##### 1.2 The basic process of WiFi distribution network

Bluetooth distribution network - this function is used for Bluetooth WiFi scales, used when configuring the network for the scales

1. First make sure that the Bluetooth WiFi scale has been bound
2. The user enters the Wifi account and password
3. Initiate the connection device,
4. After the connection is successful, send the account number and password to the scale in the writable callback (PPBleWorkStateWritable)

ppScale.configWifi(ssid, password)

5. In the monitor of PPConfigWifiInterface, the monitorConfigState method returns the SN code. At this time, the WiFi icon on the scale will flash first (connecting to the router), and then constant (connecting to the router successfully and obtaining the SN),
6. Send the sn to the server to verify whether the scale has been registered
7. If the server returns success, the network distribution is successful, otherwise the network distribution fails

##### 1.3 Data list

Data list - is the offline data of the scale obtained from the server and stored on the server, not the historical data stored on the scale

##### 1.4 Device configuration

On the device management page, if the WiFi scale is bound, the setting entry will be displayed, click the setting entry to enter the device configuration page,
On the device configuration page, you can view the SN, SSID, and PASSWORD of the current device, modify the server DNS address of the scale, and clear the SSID of the current scale.
The corresponding code is under DeveloperActivity.class.

#### Wifi function of PeripheralTorre

1. First read the Wifi list from the device side - PeripheralTorreSearchWifiListActivity
2. Then choose a Wifi
3. Enter password - PeripheralTorreConfigWifiActivity
4. Start distribution network

var pwd = ""
if (etWifiKey. text != null) {
pwd = etWifiKey.text.toString()
}
val domainName = "http://nat.lefuenergy.com:10081"//Dynamic configuration of the server address, which needs to be provided by the background
PPBlutoothPeripheralTorreInstance.instance.controller?.getTorreDeviceManager()?.configWifi(ssid, pwd, domainName, configWifiInterface)
5. The distribution network is completed

val configWifiInterface = object : PPTorreConfigWifiInterface() {

override fun configResult(configStateMenu: PPConfigStateMenu?, resultCode: String?) {
configResultTV?.text = "configResult configStateMenu: $configStateMenu\nresultCode: $resultCode"
}

}

6. Exit distribution network

```
PPBlutoothPeripheralTorreInstance.instance.controller?.getTorreDeviceManager()?.exitConfigWifi()
```
7. Distribution network interface description-PPTorreConfigWifiInterface

```
public class PPTorreConfigWifiInterface {

/**
* Distribution status
*
* @param configStateMenu
* @param resultCode
*/
public void configResult(PPConfigStateMenu configStateMenu, String resultCode) {
}

/**
* Wifi list callback
*
* @param wifiModels Wifi information list returned
*/
public void monitorWiFiListSuccess(List<PPWifiModel> wifiModels) {
}

/**
* Read the SSID of the device
*
* @param ssid
* @param state 0 success 1 failure
*/
public void readDeviceSsidCallBack(String ssid, int state) {
}

/**
* read wifimac
*
* @param wifiMac
*/
public void readDeviceWifiMacCallBack(String wifiMac) {

}

/**
* Distribution status
* 0x00: Unconfigured network (the device is restored to the factory or the APP releases the state after the device is configured with the network)
* 0x01: Configured network (app configured network status)
*
* @param state
*/
public void configWifiState(int state) {
    }   
}
```

### IV kitchen scale enumeration specific parameter description

#### 1.1 Unit enumeration class PPUnitType
```
Unit_KG(0),//KG
Unit_LB(1), //LB
PPUnitST(2), //ST
PPUnitJin(3),//jin
PPUnitG(4), //g
PPUnitLBOZ(5), //lb:oz
PPUnitOZ(6), //oz
PPUnitMLWater(7), //ml(water)
PPUnitMLMilk(8);//milk
```

#### 1.2 Unit precision

```
enum class PPDeviceAccuracyType {
    PPDeviceAccuracyTypeUnknown(0),//unknown precision
    //KG precision 0.1
    PPDeviceAccuracyTypePoint01(1),
    //KG precision 0.05
    PPDeviceAccuracyTypePoint005(2),
    // 1G precision
    PPDeviceAccuracyTypePointG(3),
    // 0.1G precision
    PPDeviceAccuracyTypePoint01G(4);
}
```

#### 1.3 Difference between Food Scale and Body Fat Scale

##### 1.3.1 The interface for receiving data is different

The listener food scale that receives data in ProtocolFilterImpl is FoodScaleDataProtocoInterface, and the body fat scale is composed of PPProcessDateInterface (process data) and PPLockDataInterface (lock data).

##### 1.3.2 The weight unit of the received data is different

The unit corresponding to the weight value of the body fat scale is KG, the unit corresponding to the weight value of the food scale is g, and the unit corresponding to the weight value is fixed. (The unit here is not synchronized with the unit given by PPScale, and the unit given by PPScale is the unit of the current scale end)

### V. Entity class object and specific parameter description

#### 1.1 PPBodyBaseModel parameter description

```
//The weight is magnified by 100 times
@JvmField
var weight = 0

//4 electrode algorithm impedance
@JvmField
var impedance: Long = 0

//Pin-to-pin plaintext impedance value (Ω), range 200.0 ~ 1200.0
var ppZTwoLegs = 0

//Device Information
@JvmField
var deviceModel: PPDeviceModel? = null

//User Info
@JvmField
var userModel: PPUserModel? = null

//Whether the heart rate is being measured
@JvmField
var isHeartRating = false

// Whether this measurement is over
var isEnd = true

//Equipment unit weight unit default kg
@JvmField
var unit: PPUnitType? = null

//heart rate
@JvmField
var heartRate = 0

// is it overloaded
@JvmField
var isOverload = false

// is it a positive number
@JvmField
var isPlus = true

//format yyyy-MM-dd HH:mm:ss
@JvmField
var dateStr = ""

//Data belongs to the torre protocol
@JvmField
var memberId = ""

/****************************8 Electrode (Torre) Impedance************** ***************************/

//100KHz left-hand impedance encryption value (value uploaded by the lower computer)
@JvmField
var z100KhzLeftArmEnCode: Long = 0

//100KHz left foot impedance encrypted value (value uploaded by the lower computer)
@JvmField
var z100KhzLeftLegEnCode: Long = 0

//100KHz right-hand impedance encryption value (value uploaded by the lower computer)
@JvmField
var z100KhzRightArmEnCode: Long = 0

//100KHz right foot impedance encryption value (value uploaded by the lower computer)
@JvmField
var z100KhzRightLegEnCode: Long = 0

//100KHz torso impedance encryption value (upload value from lower computer)
@JvmField
var z100KhzTrunkEnCode: Long = 0

//20KHz left-hand impedance encryption value (value uploaded by the lower computer)
@JvmField
var z20KhzLeftArmEnCode: Long = 0

//20KHz left foot impedance encrypted value (upload value from lower computer)
@JvmField
var z20KhzLeftLegEnCode: Long = 0

//20KHz right-hand impedance encryption value (value uploaded by the lower computer)
@JvmField
var z20KhzRightArmEnCode: Long = 0

//20KHz right foot impedance encrypted value (value uploaded by the lower computer)
@JvmField
var z20KhzRightLegEnCode: Long = 0

//20KHz torso impedance encryption value (value uploaded by the lower computer)
@JvmField
var z20KhzTrunkEnCode: Long = 0

#### 1.2 PPBodyFatModel body fat calculation object parameter description

// used to store the necessary parameters for calculation
var ppBodyBaseModel: PPBodyBaseModel? = null
var ppSDKVersion: String? = null//calculate the library version number

// gender
var ppSex: PPUserGender = ppBodyBaseModel?.userModel?.sex?: PPUserGender.PPUserGenderFemale

// height (cm)
var ppHeightCm: Int = ppBodyBaseModel?.userModel?.userHeight?: 100

// age)
var ppAge: Int = ppBodyBaseModel?.userModel?.age?: 0

// body fat error type
var errorType: BodyFatErrorType = BodyFatErrorType.PP_ERROR_TYPE_NONE

// Data interval range and introduction description
var bodyDetailModel: PPBodyDetailModel? = null

/**************** Four-electrode algorithm****************************/
// body weight (kg)
var ppWeightKg: Float = (ppBodyBaseModel?.weight?.toFloat() ?: 0.0f).div(100.0f)

var ppWeightKgList: List<Float>? = null

// Body Mass Index
var ppBMI: Float = 0f
var ppBMIList: List<Float>? = null

// fat rate (%)
var ppFat: Float = 0f
var ppFatList: List<Float>? = null

// fat mass (kg)
var ppBodyfatKg: Float = 0f
var ppBodyfatKgList: List<Float>? = null

// Muscle rate (%)
var ppMusclePercentage: Float = 0f
var ppMusclePercentageList: List<Float>? = null

// muscle mass (kg)
var ppMuscleKg: Float = 0f
var ppMuscleKgList: List<Float>? = null

// Skeletal muscle rate (%)
var ppBodySkeletal: Float = 0f
var ppBodySkeletalList: List<Float>? = null

// Skeletal muscle mass (kg)
var ppBodySkeletalKg: Float = 0f
var ppBodySkeletalKgList: List<Float>? = null

// Moisture rate (%), resolution 0.1,
var ppWaterPercentage: Float = 0f
var ppWaterPercentageList: List<Float>? = null

//Moisture content (Kg)
var ppWaterKg: Float = 0f
var ppWaterKgList: List<Float>? = null

// protein rate (%)
var ppProteinPercentage: Float = 0f
var ppProteinPercentageList: List<Float>? = null

//Protein amount (Kg)
var ppProteinKg: Float = 0f
var ppProteinKgList: List<Float>? = null

// lean body mass (kg)
var ppLoseFatWeightKg: Float = 0f
var ppLoseFatWeightKgList: List<Float>? = null

// Subcutaneous fat rate (%)
var ppBodyFatSubCutPercentage: Float = 0f
var ppBodyFatSubCutPercentageList: List<Float>? = null

// subcutaneous fat mass
var ppBodyFatSubCutKg: Float = 0f
var ppBodyFatSubCutKgList: List<Float>? = null

// heart rate (bmp)
var ppHeartRate: Int = ppBodyBaseModel?.heartRate?: 0
var ppHeartRateList: List<Float>? = null

// basal metabolism
var ppBMR: Int = 0
var ppBMRList: List<Float>? = null

// visceral fat level
var ppVisceralFat: Int = 0
var ppVisceralFatList: List<Float>? = null

// bone mass (kg)
var ppBoneKg: Float = 0f
var ppBoneKgList: List<Float>? = null

// Muscle control amount (kg)
var ppBodyMuscleControl: Float = 0f

// Fat control amount (kg)
var ppFatControlKg: Float = 0f

// standard weight
var ppBodyStandardWeightKg: Float = 0f

// ideal body weight (kg)
var ppIdealWeightKg: Float = 0f

// control weight (kg)
var ppControlWeightKg: Float = 0f

// body type
var ppBodyType: PPBodyDetailType? = null

// obesity level
var ppFatGrade: PPBodyFatGrade? = null
var ppFatGradeList: List<Float>? = null

// health assessment
var ppBodyHealth: PPBodyHealthAssessment? = null
var ppBodyHealthList: List<Float>? = null

// body age
var ppBodyAge: Int = 0
var ppBodyAgeList: List<Float>? = null

// body score
var ppBodyScore: Int = 0
var ppBodyScoreList: List<Float>? = null

/**************** Eight-electrode algorithm unique ***************************** /

// Output parameter - body composition: body cell mass (kg)
var ppCellMassKg: Float = 0.0f
var ppCellMassKgList: List<Float> = listOf()

// output parameter - evaluation suggestion: recommended calorie intake Kcal/day
var ppDCI: Int = 0

// Output parameter - whole body composition: inorganic salt amount (Kg)
var ppMineralKg: Float = 0.0f
var ppMineralKgList: List<Float> = listOf()

// output parameter - evaluation suggestion: obesity degree (%)
var ppObesity: Float = 0.0f
var ppObesityList: List<Float> = listOf()

// Output parameter - body composition: extracellular water (kg)
var ppWaterECWKg: Float = 0.0f
var ppWaterECWKgList: List<Float> = listOf()

// Output parameter - body composition: intracellular water volume (kg)
var ppWaterICWKg: Float = 0.0f
var ppWaterICWKgList: List<Float> = listOf()

// left hand fat mass (%), resolution 0.1
var ppBodyFatKgLeftArm: Float = 0.0f

// Left foot fat mass (%), resolution 0.1
var ppBodyFatKgLeftLeg: Float = 0.0f

// Right hand fat mass (%), resolution 0.1
var ppBodyFatKgRightArm: Float = 0.0f

// right foot fat mass (%), resolution 0.1
var ppBodyFatKgRightLeg: Float = 0.0f

// Trunk fat mass (%), resolution 0.1
var ppBodyFatKgTrunk: Float = 0.0f

// left hand fat percentage (%), resolution 0.1
var ppBodyFatRateLeftArm: Float = 0.0f

// left foot fat percentage (%), resolution 0.1
var ppBodyFatRateLeftLeg: Float = 0.0f

// Right hand fat percentage (%), resolution 0.1
var ppBodyFatRateRightArm: Float = 0.0f

// Right foot fat percentage (%), resolution 0.1
var ppBodyFatRateRightLeg: Float = 0.0f

// Trunk fat percentage (%), resolution 0.1
var ppBodyFatRateTrunk: Float = 0.0f

// Left hand muscle mass (kg), resolution 0.1
var ppMuscleKgLeftArm: Float = 0.0f

// Left foot muscle mass (kg), resolution 0.1
var ppMuscleKgLeftLeg: Float = 0.0f

// Right hand muscle mass (kg), resolution 0.1
var ppMuscleKgRightArm: Float = 0.0f

// Right foot muscle mass (kg), resolution 0.1
var ppMuscleKgRightLeg: Float = 0.0f

// Trunk muscle mass (kg), resolution 0.1
var ppMuscleKgTrunk: Float = 0.0f
```
Note: To get the object when using it, please call the corresponding get method to get the corresponding value

##### 1.2.1 Error Type PPBodyfatErrorType
```
PP_ERROR_TYPE_NONE(0), //no error
PP_ERROR_TYPE_AGE(1), //The age parameter is wrong, it needs to be between 6 and 99 years old (parameters other than BMI/idealWeightKg are not calculated)
PP_ERROR_TYPE_HEIGHT(2), //The height parameter is wrong, it needs to be 90 ~ 220cm (not counting all parameters)
PP_ERROR_TYPE_WEIGHT(3), //The weight is wrong 10 ~ 200kg
PP_ERROR_TYPE_SEX(4), //Sex is wrong 0 ~ 1
PP_ERROR_TYPE_PEOPLE_TYPE(5), //The height parameter is wrong, it needs to be 90 ~ 220cm (not counting all parameters)
PP_ERROR_TYPE_IMPEDANCE_TWO_LEGS(6), //The impedance is wrong 200~1200
PP_ERROR_TYPE_IMPEDANCE_TWO_ARMS(7), //The impedance is wrong 200~1200
PP_ERROR_TYPE_IMPEDANCE_LEFT_BODY(8), //The impedance is wrong 200~1200
PP_ERROR_TYPE_IMPEDANCE_RIGHT_ARM(9), //The impedance is wrong 200~1200
PP_ERROR_TYPE_IMPEDANCE_LEFT_ARM(10), //The impedance is wrong 200~1200
PP_ERROR_TYPE_IMPEDANCE_LEFT_LEG(11), //The impedance is wrong 200~1200
PP_ERROR_TYPE_IMPEDANCE_RIGHT_LEG(12), //The impedance is wrong 200~1200
PP_ERROR_TYPE_IMPEDANCE_TRUNK(13); //The impedance is wrong 10~100
```
##### 1.2.2 Health assessment PPBodyEnum.PPBodyHealthAssessment

```
PPBodyAssessment1(0), //!< health risks
PPBodyAssessment2(1), //!< sub-health
PPBodyAssessment3(2), //!< General
PPBodyAssessment4(3), //!< good
PPBodyAssessment5(4); //!< very good
```

##### 1.2.3 Fat grade PPBodyEnum.PPBodyFatGrade

```
PPBodyGradeFatThin(0), //!< thin
PPBodyGradeFatStandard(1), //!< standard
PPBodyGradeFatOverweight(2), //!< overweight
PPBodyGradeFatOne(3), //!< obesity level 1
PPBodyGradeFatTwo(4), //!< obesity level 2
PPBodyGradeFatThree(5); //!< Obesity level 3
```

##### 1.2.4 Body type PPBodyDetailType

```
LF_BODY_TYPE_THIN(0),//thin type
LF_BODY_TYPE_THIN_MUSCLE(1), // lean muscle type
LF_BODY_TYPE_MUSCULAR(2),//Muscular type
LF_BODY_TYPE_LACK_EXERCISE(3),//lack of exercise
LF_BODY_TYPE_STANDARD(4),//Standard type
LF_BODY_TYPE_STANDARD_MUSCLE(5),//standard muscle type
LF_BODY_TYPE_OBESE_FAT(6),//Swollen fat type
LF_BODY_TYPE_FAT_MUSCLE(7),//Fat muscle type
LF_BODY_TYPE_MUSCLE_FAT(8);//Muscular fat
```

#### 1.3 Device object PPDeviceModel parameter description
```
String deviceMac;//device mac
String deviceName;//Device Bluetooth name
/**
* Device type
*
* @see com.peng.ppscale.business.device.PPDeviceType.ScaleType
* @deprecated
*/
String scaleType;
/**
* Power
*/
int devicePower = -1;
/**
* Hardware version number
* @deprecated
*/
String firmwareVersion;
/**
* Device type
*
* @see PPScaleDefine.PPDeviceType
*/
public PPScaleDefine.PPDeviceType deviceType;
/**
* Protocol mode
*
* @see PPScaleDefine. PPDeviceProtocolType
*/
public PPScaleDefine.PPDeviceProtocolType deviceProtocolType;
/**
* Calculation method
*
* @see PPScaleDefine.PPDeviceCalcuteType
*/
public PPScaleDefine.PPDeviceCalcuteType deviceCalcuteType;
/**
* Accuracy
*
* @see PPScaleDefine.PPDeviceAccuracyType
*/
public PPScaleDefine.PPDeviceAccuracyType deviceAccuracyType;
/**
* Power supply mode
*
* @see PPScaleDefine.PPDevicePowerType
*/
public PPScaleDefine.PPDevicePowerType devicePowerType;
/**
* Device connection type, used in the state that must be directly connected
*
* @see PPScaleDefine. PPDeviceConnectType
*/
public PPScaleDefine.PPDeviceConnectType deviceConnectType;
/**
* Functional type, multi-functional superposition
*
* @see PPScaleDefine.PPDeviceFuncType
*/
public int deviceFuncType;
/**
* Supported units
*
* @see PPScaleDefine.PPDeviceUnitType
*/
public int deviceUnitType;
/**
* Can connect
*/
public boolean deviceConnectAbled;
```
##### 1.3.1 PPScaleDefine.PPDeviceProtocolType protocol type, specific description
```
//unknown
PPDeviceProtocolTypeUnknown(0),
//Use V2.0 Bluetooth protocol
PPDeviceProtocolTypeV2(1),
//Use V3.0 Bluetooth protocol
PPDeviceProtocolTypeV3(2),
//four-electrode, eight-electrode protocol
PPDeviceProtocolTypeTorre(3);
```
##### 1.3.2 PPScaleDefine.PPDeviceType device type specification
```
PPDeviceTypeUnknow(0), //unknown
PPDeviceTypeCF(1), //body fat scale
PPDeviceTypeCE(2), //weight scale
PPDeviceTypeCB(3), //baby scale
PPDeviceTypeCA(4), //kitchen scale
PPDeviceTypeCC(5); //Bluetooth wifi scale
```
##### 1.3.3 PPScaleDefine.PPDeviceAccuracyType Specific description of the accuracy type of weight
```
PPDeviceAccuracyTypeUnknow(0), //unknown precision
PPDeviceAccuracyTypePoint01(1), //KG accuracy 0.1
PPDeviceAccuracyTypePoint005(2), //KG accuracy 0.05
PPDeviceAccuracyTypePointG(3), // 1G accuracy
PPDeviceAccuracyTypePoint01G(4); // 0.1G accuracy
```
##### 1.3.4 PPScaleDefine.DeviceCalcuteType Body fat calculation type specific description
```
//unknown
PPDeviceCalcuteTypeUnknown(0),
//Scale calculation
PPDeviceCalcuteTypeInScale(1),
//DC
PPDeviceCalcuteTypeDirect(2),
//comminicate
PPDeviceCalcuteTypeAlternate(3),
// 8-electrode AC algorithm
PPDeviceCalcuteTypeAlternate8(4),
//The default calculation library directly uses the body fat rate returned by Hetai
PPDeviceCalcuteTypeNormal(5),
//no need to calculate
PPDeviceCalcuteTypeNeedNot(6);
```
##### 1.3.5 PPScaleDefine.PPDevicePowerType Power supply mode specification
```
//unknown
PPDevicePowerTypeUnknown(0),
//Battery powered
PPDevicePowerTypeBattery(1),
//solar powered
PPDevicePowerTypeSolar(2),
//Charging
PPDevicePowerTypeCharge(3);
```
##### 1.3.6 PPScaleDefine.PPDeviceFuncType Function type, multi-functional superposition, specific instructions
```
// weighing
PPDeviceFuncTypeWeight(0x01),
//measure body fat
PPDeviceFuncTypeFat(0x02),
//heart rate
PPDeviceFuncTypeHeartRate(0x04),
//historical data
PPDeviceFuncTypeHistory(0x08),
//safe mode, pregnant mode
PPDeviceFuncTypeSafe(0x10),
//closing single foot
PPDeviceFuncTypeBMDJ(0x20),
// baby mode
PPDeviceFuncTypeBaby(0x40),
//wifi distribution network
PPDeviceFuncTypeWifi(0x80);
```
##### 1.3.7 PPScaleDefine.PPDeviceUnitType Supported units, specific instructions (temporarily not enabled)
```
PPDeviceUnitTypeKG(0x01), //kg
PPDeviceUnitTypeLB(0x02), //lb
PPDeviceUnitTypeST(0x04), //st
PPDeviceUnitTypeJin(0x08), //Jin
PPDeviceUnitTypeSTLB(0x10);//st:lb
```
#### 1.4 LFFoodScaleGeneral parameter description

private double lfWeightKg; //weight in g
private PPUnitType unit; //unit
private int byteNum; //11 bytes and 16 bytes have the same bluetooth name of the scale, so the scale type can be distinguished according to its own length
private int thanZero; //positive and negative 0 means negative value 1 positive value
private String scaleType;//Scale type, judge the accuracy corresponding to the unit according to the type

### VI . Bluetooth status monitoring callback and system Bluetooth status callback

Contains two callback methods, one is Bluetooth status monitoring, and the other is system Bluetooth callback
```
PPBleStateInterface bleStateInterface = new PPBleStateInterface() {
    //Bluetooth status monitoring
    //deviceModel is null while bluetooth is scanning
    @Override
    public void monitorBluetoothWorkState(PPBleWorkState ppBleWorkState, PPDeviceModel deviceModel) {
        if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnected) {
            Logger.d("Device is connected");
        } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnecting) {
            Logger.d("Device is connecting");
        } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateDisconnected) {
            Logger.d("The device is disconnected");
        } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateStop) {
            Logger.d("Stop scanning");
        } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateSearching) {
            Logger.d("Scanning");
        } else if (ppBleWorkState == PPBleWorkState.PPBleWorkSearchTimeOut) {
            Logger.d("Scan timed out");
        } else {
        }
    }
    //system bluetooth callback
    @Override
    public void monitorBluetoothSwitchState(PPBleSwitchState ppBleSwitchState) {
        if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOff) {
            Logger.e("system bluetooth disconnected");
            Toast.makeText(BindingDeviceActivity.this, "system bluetooth disconnected", Toast.LENGTH_SHORT).show();
        } else if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOn) {
            Logger.d("system bluetooth on");
            Toast.makeText(BindingDeviceActivity.this, "system bluetooth is on", Toast.LENGTH_SHORT).show();
        } else {
            Logger.e("system bluetooth exception");
        }
    }
};
```
### VII. [Version Update Instructions](doc/version_update.md)

### VIII. Third-party libraries used

#### 1. The body fat calculation library provided by the chip solution provider

#### 2. [bluetoothkit1.4.0 Bluetooth library](https://github.com/dingjikerbo/Android-BluetoothKit)

### IX. [FAQ](doc/common_problem.md)

Contact Developer: Email: yanfabu-5@lefu.cc