

[English Docs](README_EN.md) | [Chinese Docs](README.md)

Related documents

[Lefu Open Platform](https://uniquehealth.lefuenergy.com/unique-open-web/#/document) | [PPBluetoothKit iOS SDK](https://uniquehealth.lefuenergy.com/unique-open-web/ #/document?url=https://lefuhengqi.apifox.cn/doc-2625647) | [PPBluetoothKit WeChat applet plug-in](https://uniquehealth.lefuenergy.com/unique-open-web/#/document?url =https://lefuhengqi.apifox.cn/doc-2625745)



[Android sample program address](https://gitee.com/shenzhen-lfscale/bluetooth-kit-android-demo.git)



# PPBluetoothKit Android SDK



PPBluetoothKit is an SDK packaged for human body scales and food scales, including Bluetooth connection logic, data analysis logic, and body fat calculation.



### Sample program



In order to allow customers to quickly implement weighing and corresponding functions, a sample program is provided, which includes a body fat calculation module and a device function module.



- The devices currently supported by the device function module include: Bluetooth scales, food scales, and Torre series Bluetooth WiFi body fat scales.

- The body fat calculation module supports 4-electrode AC algorithm, 4-electrode DC algorithm, and 8-electrode AC algorithm.



### Commercial version program



- You can search and download "Unique Health" in various application markets



## Ⅰ. Integration method



### 1.1 Apply for AppKey, AppSecret and config files



- First go to [Lefu Open Platform] (https://uniquehealth.lefuenergy.com/unique-open-web/#/document) to apply for AppKey, AppSecret and config files

- Place the config file in the project's assets directory



```mermaid    
 graph TD    
 A[Enter the open platform] --> B[Register/Login] -- Personal information entrance --> C[Get AppKey and AppSecret] --> D[Contact business authorization] -- Authorization successful --> F[Download config File] --> G[Copy the config file to the project's assets directory] --> H[Get the AppKey/AppSecret and config files] -- PPBlutoothKit.initSdk --> I[(Initialize SDK)]    
 ```   
### 1.2 SDK initialization



```    
 //Please be sure to replace it with your own AppKey/AppSecret when using it. If you need to add device configuration, please contact our sales consultant.    
 val appKey = ""    
 val appSecret = ""    
 /************************The following content is the configuration items of the SDK************************ ******************/    
 //SDK log printing control, true will print    
 PPBlutoothKit.setDebug(BuildConfig.DEBUG)    
 /**    
 * The parameters required for SDK initialization need to be applied by yourself on the open platform. Please do not use the parameters in the Demo directly.    
 * @param appKey App identification    
 * @param appSecret Appp’s key    
 * @param configPath Download the corresponding configuration file ending with .config on the open platform and place it in the assets directory. Pass the full name of the config file to the SDK    
 */    
 PPBlutoothKit.initSdk(this, appKey, appSecret, "lefu.config")    
 ```   
### 1.3 aar file import

- Add it to build.gradle under the module where sdk needs to be introduced (for the latest version, please check the libs under the module of ppscalelib)

```    
 dependencies {    
 ​ //aar introduction ​ api(name: 'ppblutoothkit-3.2.5-20231129.115853-1', ext: 'aar') }    
 ```   


### 1.4 Integration FAQ

- If you encounter an error related to "AndroidManifest.xml" after integration, please try adding the following code to the main module to solve the problem:

```    
 android {    
 ​ packagingOptions { ​ exclude 'AndroidManifest.xml' ​ } }    
 ```   


- If you encounter a ".so" type file error, please try clearing the cache and changing the SDK integration method to api



- If you encounter other integration problems, please consult: yanfabu-5@lefu.cc or contact our sales consultants



- If you have good suggestions or excellent code, you can submit your request on Gitee, we will thank you very much



## Ⅱ .Instructions for use



### 1.1 Bluetooth permission related



#### 1.1.1 Operating environment



Due to the need for Bluetooth connection, the Demo needs to be run on a real device, Android phone 6.0 and above or HarmonyOS 2.0 and above



#### 1.1.2 Agreements related to Bluetooth permissions



During the use of the Demo, you need to turn on Bluetooth and turn on the positioning switch. Make sure to enable and authorize the necessary permissions: For precise positioning permissions and nearby device permissions, you can view the official Bluetooth permissions document. The document address is: [Instructions on Bluetooth permissions on the Google Developer Website] (https://developer.android.com/guide/topics/connectivity/bluetooth/permissions).



- Accurate positioning permissions
- Nearby device permissions
- Position switch
- Bluetooth switch


```    
 <manifest> 
	 <!-- Request legacy Bluetooth permissions on older devices. --> 
	 <uses-permission android:name="android.permission.BLUETOOTH" android:maxSdkVersion="30" /> 
	 <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" android:maxSdkVersion="30" /> 
	 <!-- Needed only if your app looks for Bluetooth devices. If your app doesn't use Bluetooth scan results to derive physical location information, you can strongly assert that your app doesn't derive physical location. --> 
	 <uses-permission android:name="android.permission.BLUETOOTH_SCAN" /> 
	 <!-- Needed only if your app makes the device discoverable to Bluetooth devices. --> 
	 <uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE" /> 
	 <!-- Needed only if your app communicates with already-paired Bluetooth devices. --> 
	 <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" /> 
	 <!-- Needed only if your app uses Bluetooth scan results to derive physical location. --> 
	 <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" /> ...    
 </manifest>
```

### 1.2 Conventions related to measuring body data



#### 1.2.1 Precautions for weighing and fat measurement



- The scale supports fat measurement

- Weigh on your bare feet and touch the corresponding electrode pads

- The weighing interface returns weight (kg) and impedance information

- Human body parameters height and age are entered correctly

#### 1.2.2 Body fat calculation

##### Basic parameter agreement

| Category | Input Range | Units |  
| :-------- | :----- | :----: |  
| Height | 100-220 | cm |  
| Age | 10-99 | Years |  
| Gender| 0/1 | Female/Male|  
| Weight | 10-200 | kg |  


- Height, age, gender and corresponding impedance are required, and the corresponding calculation library is called to obtain them.
- The body fat data items involved in 8-electrode require an 8-electrode scale to be used.


## Ⅲ. Calculate body fat - Caclulate - CalculateManagerActivity

### 1.1 Description of parameters required for body fat calculation

Based on the weight and impedance parsed by the Bluetooth protocol, plus the height, age, and gender of the user data, multiple body fat parameter information such as body fat rate is calculated.

#### 1.1.1 PPBodyBaseModel

| Parameters | Comments | Description |  
| :-------- | :----- | :----: |  
| weight | weight | actual weight * rounded to 100 |  
|impedance|Four-electrode algorithm impedance|Four-electrode algorithm field|  
|zTwoLegsDeCode|Four-electrode pin-to-pin plaintext impedance value|Four-electrode algorithm field|  
| userModel|User basic information object|PPUserModel|  
| deviceModel-deviceCalcuteType| The calculation method of the corresponding device|Detailed description later|  
| isHeartRating| Whether the heart rate is being measured|4&8 common fields|  
| unit | Current unit of the scale | Real-time unit |  
| heartRate| Heart rate|The scale supports heart rate validation|  
| isPlus| Is it a positive number | Food scale takes effect |  
| memberId| Member Id | Valid when the scale terminal supports users |  
| z100KhzLeftArmEnCode| 100KHz left-hand impedance encrypted value | eight-electrode field |  
| z100KhzLeftLegEnCode| 100KHz left foot impedance encrypted value|Eight-electrode field|  
| z100KhzRightArmEnCode| 100KHz right-hand impedance encrypted value | eight-electrode field |  
| z100KhzRightLegEnCode| 100KHz right leg impedance encrypted value|Eight-electrode field|  
| z100KhzTrunkEnCode| 100KHz trunk impedance encrypted value|Eight-electrode field|  
|z20KhzLeftArmEnCode| 20KHz left-hand impedance encrypted value|Eight-electrode field|  
| z20KhzLeftLegEnCode| 20KHz left foot impedance encrypted value|Eight-electrode field|  
| z20KhzRightArmEnCode|20KHz right-hand impedance encrypted value|Eight-electrode field|  
|z20KhzRightLegEnCode| 20KHz right leg impedance encrypted value|Eight-electrode field|  
| z20KhzTrunkEnCode| 20KHz trunk impedance encrypted value|Eight-electrode field|  
| z100KhzLeftArmDeCode| 100KHz left-hand impedance decrypted value | eight-electrode field |  
|z100KhzLeftLegDeCode| 100KHz left foot impedance decrypted value|Eight-electrode field|  
| z100KhzRightArmDeCode| 100KHz right-hand impedance decrypted value | eight-electrode field |  
| z100KhzRightLegDeCode| 100KHz right leg impedance decrypted value|Eight-electrode field|  
| z100KhzRightArmDeCode| 100KHz left-hand impedance decrypted value | eight-electrode field |  
| z100KhzTrunkDeCode| 100KHz trunk impedance decrypted value|Eight-electrode field|  
|z20KhzLeftArmDeCode| 20KHz left-hand impedance decrypted value|Eight-electrode field|  
|z20KhzLeftLegDeCode| 20KHz left foot impedance decrypted value|Eight-electrode field|  
|z20KhzRightArmDeCode| 20KHz right-hand impedance decrypted value|Eight-electrode field|  
|z20KhzRightLegDeCode| 20KHz right leg impedance decrypted value|Eight-electrode field|  
| z20KhzTrunkDeCode| 20KHz trunk impedance decrypted value|Eight-electrode field|  



#### 1.1.2 Calculation type description PPDeviceModel - deviceCalcuteType

| PPDeviceCalcuteType | Comments | Scope of use |  
| :-------- | :----- | :----: |  
| PPDeviceCalcuteTypeInScale | Scale side calculations | Scale side calculations |  
| PPDeviceCalcuteTypeDirect| DC| Four-electrode DC grease scale|  
| PPDeviceCalcuteTypeAlternate| Four-electrode AC | Four-electrode body fat scale |  
| PPDeviceCalcuteTypeAlternate8| 8-electrode AC algorithm | Eight-electrode body fat scale |  
| PPDeviceCalcuteTypeNormal| Default calculation method | 4-electrode AC |  
| PPDeviceCalcuteTypeNeedNot| No calculation required | Food scale or body scale |  
| PPDeviceCalcuteTypeAlternate8_0| 8-electrode algorithm | Eight-electrode body fat scale |  



#### 1.1.3 Basic user information description PPUserModel

| Parameters | Comments | Description |  
| :-------- | :----- | :----: |   
| userHeight| Height|All body fat scales|  
| age| age|all body fat scales|  
| sex| Gender|All body fat scales| 


### 1.2 Eight-electrode body fat calculation-Calculate8Activitiy

**Eight-electrode body fat calculation example:**

```    
 //Eight-electrode calculation type    
 val userModel = PPUserModel.Builder()    
 .setSex(PPUserGender.PPUserGenderFemale) //gender    
 .setHeight(168)//height 100-220    
 .setAge(35)//age 10-99    
 .build()    
 val weight = 83.00    
 val deviceModel = PPDeviceModel("", "CF577")//Replace with your own device Bluetooth name    
 var calculateType: PPScaleDefine.PPDeviceCalcuteType? = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8//CF577 series    
 if (position == 0) {    
	 calculateType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8//8 electrode algorithm, bhProduct=1--CF577    
 } else if (position == 1) {    
	 calculateType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_1//8 electrode algorithm, bhProduct =3--CF586    
 } else if (position == 2) {    
	 calculateType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_0//8 electrode algorithm, bhProduct =4--CF597    
 }    
 deviceModel.deviceCalcuteType = calculateType    
 val bodyBaseModel = PPBodyBaseModel()    
 bodyBaseModel.weight = ((weight + 0.005) * 100).toInt()    
 bodyBaseModel.deviceModel = deviceModel    
 bodyBaseModel.userModel = userModel    
 bodyBaseModel.z100KhzLeftArmEnCode = 294794323L    
 bodyBaseModel.z100KhzLeftLegEnCode = 806102147L    
 bodyBaseModel.z100KhzRightArmEnCode = 26360525L    
 bodyBaseModel.z100KhzRightLegEnCode = 816581534L    
 bodyBaseModel.z100KhzTrunkEnCode = 1080247226L    
 bodyBaseModel.z20KhzLeftArmEnCode = 27983001L    
 bodyBaseModel.z20KhzLeftLegEnCode = 837194050L    
 bodyBaseModel.z20KhzRightArmEnCode = 1634195706L    
 bodyBaseModel.z20KhzRightLegEnCode = 29868463L    
 bodyBaseModel.z20KhzTrunkEnCode = 1881406429L    
 val fatModel = PPBodyFatModel(bodyBaseModel)    
 Log.d("liyp_", fatModel.toString())    
```  

### 1.3 Four-electrode DC fluid grease calculation-Calculate4DCActivitiy

**Four-electrode DC body fat calculation example:**

```
 val userModel = PPUserModel.Builder()    
		 .setSex(sex) //gender    
 ​ 		 .setHeight(height)//height 100-220 ​ 
		 .setAge(age)//age 10-99
		 .build()    
 val deviceModel = PPDeviceModel("", DeviceManager.FL_SCALE)//Replace with your own device Bluetooth name    
 deviceModel.deviceCalcuteType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeDirect    
 val bodyBaseModel = PPBodyBaseModel()    
 bodyBaseModel.weight = UnitUtil.getWeight(weight)    
 bodyBaseModel.impedance = impedance    
 bodyBaseModel.deviceModel = deviceModel    
 bodyBaseModel.userModel = userModel    
 bodyBaseModel.unit = PPUnitType.Unit_KG    
 val ppBodyFatModel = PPBodyFatModel(bodyBaseModel)    
 Log.d("liyp_", ppBodyFatModel.toString())    
 ```   

### 1.4 Four-electrode AC body fat calculation-Calculate4ACActivitiy

**Four-electrode AC body fat calculation example:**


```    
 val userModel = PPUserModel.Builder()    
 .setSex(sex) //gender    
 .setHeight(height)//height 100-220    
 .setAge(age)//age 10-99    
 .build()    
 val deviceModel = PPDeviceModel("", DeviceManager.CF568)///Replace with your own device Bluetooth name    
 deviceModel.deviceCalcuteType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate    
 val bodyBaseModel = PPBodyBaseModel()    
 bodyBaseModel.weight = UnitUtil.getWeight(weight)    
 bodyBaseModel.impedance = impedance    
 bodyBaseModel.deviceModel = deviceModel    
 bodyBaseModel.userModel = userModel    
 bodyBaseModel.unit = PPUnitType.Unit_KG    
 val ppBodyFatModel = PPBodyFatModel(bodyBaseModel)    
 Log.d("liyp_", ppBodyFatModel.toString())    
 ```   

## Ⅳ. Device scanning - Device-ScanDeviceListActivity

### 1.1 Device classification definition-PPDevicePeripheralType



Scanning the device will return a PPDeviceModel object. Through the device method: deviceModel.getDevicePeripheralType(), you can obtain the device classification, distinguish your device according to PPDevicePeripheralType, and correspond to the activity of the device function example.



| Classification enumeration | Usage example class | Connection method | Device type | Protocol type |  
|------|--------|--------|--------|-----|  
| PeripheralApple | PeripheralAppleActivity | Connection | Body Scale | 2.x |  
| PeripheralBanana | PeripheralBananaActivity | Broadcast | Body Scale | 2.x |  
| PeripheralCoconut | PeripheralCoconutActivity | Connection | Body Scale | 3.x |  
| PeripheralDurian | PeripheralDutianActivity | Connections for on-device computing | Body Scale | 2.x |  
| PeripheralEgg | PeripheralEggActivity | Connections | Kitchen Scales | 2.x |  
| PeripheralFish | PeripheralFishActivity | Connections | Kitchen Scales | 3.x |  
| PeripheralGrapes | PeripheralGrapesActivity | Broadcast | Kitchen Scale | 2.x |  
| PeripheralHamburger | PeripheralHamburgerActivity | Broadcast | Kitchen Scale | 3.x |  
| PeripheralTorre | PeripheralTorreActivity | Connection | Body Scale | Torre |  
| PeripheralIce | PeripheralIceActivity | Connection | Body Scale | 4.x |  
| PeripheralJambul | PeripheralJambulActivity | Broadcast | Body Scale | 3.x |  


### 1.2 Scan surrounding supported devices-ScanDeviceListActivity

**Notice:**

- If you need to start scanning between multiple pages, it is recommended to put the scanning logic into a tool class and wrap it with a singleton

- If there are consecutive pages that need to be scanned, please make sure that the Bluetooth on the previous page has stopped scanning before scanning on the second page. It is recommended that the second page be delayed by 1000ms before starting.

- If you need to scan Bluetooth all the time, you need to restart the scan when ppBleWorkState returns PPBleWorkState.PPBleWorkSearchTimeOut in the monitorBluetoothWorkState method to ensure circular scanning.



```mermaid    
 graph TD    
 A[Detect Bluetooth related permissions] --Have permissions--> B[Detect positioning switch and Bluetooth switch]    
 A[Detect Bluetooth related permissions] --No permissions--> X[Apply permissions]    
 X[Apply for permission] --Apply for permission--> B[Detect positioning switch and Bluetooth switch]    
 B[Detect positioning switch and Bluetooth switch] --Both switches are on--> C[Start Bluetooth scan]    
 B[Detect positioning switch and Bluetooth switch] --The switch is not turned on --> Y[Guide the user to turn on the corresponding switch] --The switch is turned on --> C[Start Bluetooth scanning]    
 C[Start Bluetooth scanning<br>PPSearchManager.startSearchDeviceList] --> D[Scan to device<br>onSearchDevice] -->E[Get device classification<br>deviceModel.getDevicePeripheralType] -->F[Process according to different classifications Different business logic]    
 C[Start Bluetooth scanning]-->G[Bluetooth status monitoring<br>PPBleStateInterface]    
 G[Bluetooth status monitoring<br>PPBleStateInterface]-->G1[Actively cancel scanning<br>PPBleStateSearchCanceled]    
 G[Bluetooth status monitoring<br>PPBleStateInterface]-->G2[Scan timeout<br>PPBleWorkSearchTimeOut]    
 G[Bluetooth status monitoring<br>PPBleStateInterface]-->G3[System Bluetooth off<br>PPBleSwitchStateOff]    
 G[Bluetooth status monitoring<br>PPBleStateInterface]-->G4[System Bluetooth on<br>PPBleSwitchStateOn]    
 G2[Scan timeout<br>PPBleWorkSearchTimeOut]--Loop scan-->C[Start Bluetooth scan<br>PPSearchManager.startSearchDeviceList]    
 G4[System Bluetooth on<br>PPBleSwitchStateOn]--Cycle scan-->C[Start Bluetooth scan<br>PPSearchManager.startSearchDeviceList]    
 F[Process different business logic according to different classifications]-->H[Stop Bluetooth scanning<br>stopSearch]    
 ```   


#### 1.2.1 Start scanning - startSearchDeviceList



```    
 /** * Get around bluetooth scale devices */    
 public void startScanDeviceList() { if (ppScale == null) { ppScale = new PPSearchManager();    
 }    
 //You can dynamically set the scan time in ms    
 ppScale.startSearchDeviceList(300000, searchDeviceInfoInterface, bleStateInterface);    
 }    
 ```   
**Processing scan results-PPSearchDeviceInfoInterface**



```    
 /**    
 * @param ppDeviceModel device object    
 * @param data broadcast data    
 */    
 public void onSearchDevice(PPDeviceModel ppDeviceModel, String data) {}    
 ```   
#### 1.2.2 Bluetooth related status monitoring-PPBleStateInterface

```    
 /**    
 * Bluetooth scanning and connection status callback    
 * @param ppBleWorkState Bluetooth status identifier    
 * @param deviceModel device object    
 */    
 @Override    
 public void monitorBluetoothWorkState(PPBleWorkState ppBleWorkState, PPDeviceModel deviceModel) {    
 }    
 /**    
 * System Bluetooth status callback    
 * @param ppBleSwitchState system Bluetooth status identifier    
 */    
 @Override    
 public void monitorBluetoothSwitchState(PPBleSwitchState ppBleSwitchState) {    
 }    
 ```   

#### 1.2.3 Bluetooth state PPBleWorkState

| Category enumeration | Description | Remarks |  
|------|--------|--------|  
| PPBleWorkStateSearching | Scanning |
| PPBleWorkSearchTimeOut| Scan timeout| Restart scanning if necessary|  
| PPBleWorkSearchFail | Scan failed | Restart scan if necessary |  
| PPBleStateSearchCanceled| Stop scanning | Actively call to stop scanning |  
| PPBleWorkStateConnecting| Device connecting| |  
| PPBleWorkStateConnected | The device is connected | After being connected, it is recommended to deliver data in PPBleWorkStateWritable |  
| PPBleWorkStateConnectFailed| Connection failed| |  
| PPBleWorkStateDisconnected| The device has been disconnected| |  
| PPBleWorkStateWritable | Writable | If you need to send information to the device after connecting, you can send it here in sequence |  



#### 1.2.4 Stop scanning

```    
 ppScale.stopSearch();    
 ```   
#### 1.2.5 Restart scanning



It is recommended to delay restarting the scan for 1-2 seconds to prevent frequent scanning of the Android system.



```    
 public void delayScan() {
	 new Handler(getMainLooper()).postDelayed(new Runnable() {    
		 @Override    
		 public void run() {    
			 if (isOnResume) {    
					startScanDeviceList();    
				}    
			 }    
		 }, 1000);    
	 }    
 ```   
## Ⅴ. Function description

### 2.1 PeripheralApple-PeripheralAppleActivity



**Notice:**

- By default, Bluetooth permissions and switch detection have been processed, and the type matched to PPDevicePeripheralType is PeripheralApple, 2.x/Connection/Body Scale
- You need to judge whether history is supported before handling history-related functions.
- You need to judge whether it supports Wifi by yourself, and then handle Wifi related functions.
- The Wifi scale needs to read the SSID of the Wifi currently connected to the mobile phone by itself, and only supports 2.4G or 2.4G&5G dual-mode Wifi, and does not support single 5GWiFi.
- Before configuring the Wifi scale, you need to configure the domain name first, and then issue the ssid and pwd after success.
- The Wifi historical data uploaded by the Wifi scale to the background requires the Server to have corresponding interface capabilities. Please consult the Server developer.



| Function | Method name | Parameters | Return data type | Remarks |  
|------|--------|--------|--------|--------|  
|Bluetooth connection| startConnect|PPDeviceModel|PPBleStateInterface| Bluetooth related status|  
|Disconnect (non-Wifi scale)| disConnect||PPBleStateInterface| Bluetooth related status|  
|Disconnect (Wifi scale)| disWifi||PPBleSendResultCallBack| It is necessary to issue a disconnection command. After the issuance is successful, call disConnect to actively disconnect|  
|Synchronization time| syncTime||PPBleSendResultCallBack-onResult(PPScaleSendState sendState)|Send status callback|  
|Read device information| readDeviceInfo||PPDeviceInfoInterface-readDeviceInfoComplete|Device information callback, supported by some scales (including modelNumber, softwareVersion, serialNumber)|  
|Synchronization unit| syncUnit |PPUnitType,PPUserModel|PPBleSendResultCallBack|Send status callback|  
|Register data change listening| registDataChangeListener |PPDataChangeListener|monitorProcessData process data<br>monitorLockData lock data<br>monitorOverWeight overweight|
|Read Bluetooth history| getHistoryData||PPHistoryDataInterface|monitorHistoryData historical data callback<br>monitorHistoryEnd historical data end<br>monitorHistoryFail historical data failure|  
|Delete Bluetooth history| deleteHistoryData ||PPBleSendResultCallBack|Send status callback|  
|Whether history is supported| PPScaleHelper-<br> isSupportHistoryData |deviceFuncType|Boolean|true supported false not supported|  
|Whether Wifi is supported| PPScaleHelper-<br>isFuncTypeWifi |deviceFuncType|Boolean|true supported false not supported|  
|Configure domain name| sendModifyServerDomain |domain: String|PPConfigWifiInfoInterface|monitorModifyServerDomainSuccess Modify Domain success callback|  
|Network distribution| configWifiData |ssid: String, password: String?|PPConfigWifiInfoInterface|monitorConfigSn(sn:String)<br>If sn is not empty, it means the network configuration is successful<br>monitorConfigFail() The network configuration fails|  
|Read the Wifi-SSID of the device| getWiFiParmameters ||PPConfigWifiInfoInterface|monitorConfigSsid(ssid:String?)|  


#### 2.1.1 Weighing logic

```mermaid    
 graph TD    
 A[Initiate connection device<br>startConnect]-->B[Monitor Bluetooth connection status<br>PPBleStateInterface]    
 B-->B1[monitorBluetoothWorkState<br>Bluetooth scanning and connection status monitoring<br>PPBleWorkStateConnected connection successful<br>PPBleWorkStateConnectFailed connection failed<br>PPBleWorkStateDisconnected disconnected<br>PPBleWorkStateWritable writable]    
 B1-->C2[Current writable<br>PPBleWorkStateWritable] --> D[Switch unit<br>syncUnit]    
 D[Switch unit<br>syncUnit]--Monitor Bluetooth write callback<br>PPBleSendResultCallBack-->D1[Synchronization time<br>syncTime]    
    
    
 A-->F[Device data change monitoring<br>PPDataChangeListener]    
 F[Device data change monitoring<br>PPDataChangeListener]-->G1[Process data<br>monitorProcessData]    
 F[Device data change monitoring<br>PPDataChangeListener]-->G2[Lock data<br>monitorLockData]    
 G1--Get the current unit of your App<br> and convert it to PPUnitType-->H1[Unit conversion<br>PPUtil.getWeightValueD]-->I1[Real-time display of weight data UI]    
 G2--Get the current unit of your App<br> and convert it to PPUnitType-->H2[Unit conversion<br>PPUtil.getWeightValueD]-->I2[Judge heart rate status]    
 I2[Judge heart rate status]-->J1[Measuring heart rate]-->K1[Display heart rate measurement UI]    
 I2[Judge heart rate status]-->J2[Heart rate measurement completed]-->K2[Match user information<br>Give PPBodyBaseModelbodyBaseModel.PPUserModel]-->L[Call the calculation library to calculate body fat<br>Four-electrode AC example: Calculate4ACActivitiy<br>Four-electrode DC example: Calculate4DCActivitiy]    
 ```   

#### 2.1.2 Read complete Bluetooth history data


**Prerequisite: Bluetooth is connected**

```mermaid    
 graph TD    
 A[Read historical data<br>getHistoryData]-->A1[History data monitoring]-->B1[Historical data callback<br>monitorHistoryData]    
 B1[Historical data callback<br>monitorHistoryData]-->C[Use List to store historical data]    
 A1-->B2[Historical data end callback<br>monitorHistoryEnd]    
 B2-->D[Get the historical data of the list]-->E[Match user information<br>Give PPBodyBaseModelbodyBaseModel.PPUserModel]-->F[Call the calculation library to calculate body fat<br>Example: Calculate4ACActivitiy]    
 B2-->G[Delete historical data<br>deleteHistoryData]    
 A1-->B3[Historical data failure callback<br>monitorHistoryFail]    
 ```   


#### 2.1.3 Complete Wifi distribution process



**Prerequisite: Bluetooth is connected**

**Notice**

1. Make sure the server is normal and the router can connect to the server normally.

2. Make sure the WiFi environment is 2.4G or 2.4/5G mixed mode. Single 5G mode is not supported.

3. Make sure the account password is correct

4. Make sure that the server address used by the scale corresponds to the server address used by the App.

Note: The domain name in your own App needs to be replaced with your App’s own server domain name, and ensure that the server has completed the development of Wifi body fat scale related functions.

Document address: https://uniquehealth.lefuenergy.com/unique-open-web/#/document

Find "Lefu Body Fat Scale Self-Built Server Access Solution" -> PeripheralApple corresponding "V2.0 protocol device"


```mermaid    
 graph TD    
 A[Get the current mobile phone Wifi]-->A1[Check whether the current Wifi supports 2.4G]-->B1[Not supported]-->B3[Prompt to guide the user to switch Wifi]-->B4[After successful switch to Wifi]-->B2    
 A1-->B2[support]
 B2-->C[Let the user enter the password of the Wifi]-->D[Start network configuration]-->E[Configure domain name<br>sendModifyServerDomain]     
 E-->E1[Distribution network interface monitoring<br>PPConfigWifiInfoInterface]-->F[Received domain name configuration successfully<br>monitorModifyServerDomainSuccess]    
 F-->G[Start delivering SSID and PWD]-->H1[The distribution network interface successfully received the SN identification<br>monitorConfigSn]-->I1[The network distribution was successful]-->J1[Return to the page before network distribution And display the SSID of the distribution network]    
 G[Start delivering SSID and PWD]-->H2[Failed to return from distribution network interface<br>monitorConfigFail]-->I2[Failed to distribute network]-->J[Prompt user for possible reasons for failure]--Self-examination completed -->A[Get current mobile phone Wifi]    
 ```   


### 2.2 PeripheralBanana Function Description-PeripheralBananaActivity



**Notice:**



- By default, Bluetooth permissions and switch detection have been processed, and the type matched to PPDevicePeripheralType is PeripheralBanana, 2.x/broadcast/body scale



#### 2.2.1 Complete weighing logic



```mermaid    
 graph TD    
 A[Initiate Bluetooth scan<br>startSearch]-->B[Monitor Bluetooth scan status<br>PPBleStateInterface]-->C[monitorBluetoothWorkState<br>PPBleStateSearchCanceled to stop scanning<br>PPBleWorkSearchTimeOutScan timeout<br>PPBleWorkStateSearching scanning]    
 A-->F[Device data change monitoring<br>PPDataChangeListener]    
 F-->G1[Process data<br>monitorProcessData]    
 F-->G2[Lock data<br>monitorLockData]    
 G1--Get the current unit of your App<br> and convert it to PPUnitType-->H1[Unit conversion<br>PPUtil.getWeightValueD]-->I1[Real-time display of weight data UI]    
 G2--Get the current unit of your App<br> and convert it to PPUnitType-->H2[Unit conversion<br>PPUtil.getWeightValueD]-->I2[Judge heart rate status]    
 I2[Judge heart rate status]-->J1[Measuring heart rate]-->K1[Display heart rate measurement UI]    
 I2[Judge heart rate status]-->J2[Heart rate measurement completed]-->K2[Match user information<br>Give PPBodyBaseModelbodyBaseModel.PPUserModel]-->L[Call the calculation library to calculate body fat<br>Example: Calculate4ACActivitiy]    
 ```   


### 2.3 PeripheralCoconut function description-PeripheralCoconutActivity



**Notice:**



- By default, Bluetooth permission and switch detection have been processed, and the type of PPDevicePeripheralType is matched to PeripheralCoconut, 3.x/Connection/Body Scale



#### 2.3.1 Weighing logic



```mermaid    
 graph TD    
 A[Initiate connection device<br>startConnect]-->B[Monitor Bluetooth connection status<br>PPBleStateInterface]    
 B-->B1[monitorBluetoothWorkState<br>Bluetooth scanning and connection status monitoring<br>PPBleWorkStateConnected connection successful<br>PPBleWorkStateConnectFailed connection failed<br>PPBleWorkStateDisconnected disconnected<br>PPBleWorkStateWritable writable]    
 B1-->C2[Current writable<br>PPBleWorkStateWritable] --> D[Switch unit<br>syncUnit]    
 D[Switch unit<br>syncUnit]--Monitor Bluetooth write callback<br>PPBleSendResultCallBack-->D1[Synchronization time<br>syncTime]    
    
    
 A-->F[Device data change monitoring<br>PPDataChangeListener]    
 F[Device data change monitoring<br>PPDataChangeListener]-->G1[Process data<br>monitorProcessData]    
 F[Device data change monitoring<br>PPDataChangeListener]-->G2[Lock data<br>monitorLockData]    
 G1--Get the current unit of your App<br> and convert it to PPUnitType-->H1[Unit conversion<br>PPUtil.getWeightValueD]-->I1[Real-time display of weight data UI]    
 G2--Get the current unit of your App<br> and convert it to PPUnitType-->H2[Unit conversion<br>PPUtil.getWeightValueD]-->I2[Judge heart rate status]    
 I2[Judge heart rate status]-->J1[Measuring heart rate]-->K1[Display heart rate measurement UI]    
 I2[Judge heart rate status]-->J2[Heart rate measurement completed]-->K2[Match user information<br>Give PPBodyBaseModelbodyBaseModel.PPUserModel]-->L[Call the calculation library to calculate body fat<br>Four-electrode AC example: Calculate4ACActivitiy<br>Four-electrode DC example: Calculate4DCActivitiy]    
 ```   


#### 2.3.2 Read Bluetooth historical data



**Prerequisite: Bluetooth is connected**



```mermaid    
 graph TD    
 A[Read historical data<br>getHistoryData]-->A1[History data monitoring]-->B1[Historical data callback<br>monitorHistoryData]    
 B1[Historical data callback<br>monitorHistoryData]-->C[Use List to store historical data]    
 A1-->B2[Historical data end callback<br>monitorHistoryEnd]    
 B2-->D[Get the historical data of the list]-->E[Match user information<br>Give PPBodyBaseModelbodyBaseModel.PPUserModel]-->F[Call the calculation library to calculate body fat<br>Example: Calculate4ACActivitiy]    
 B2-->G[Delete historical data<br>deleteHistoryData]    
 A1-->B3[Historical data failure callback<br>monitorHistoryFail]    
 ```   


### 2.4 PeripheralDurian function description-PeripheralDurianActivity



**Notice:**

- By default, Bluetooth permissions and switch detection have been processed, and the type of PPDevicePeripheralType is matched to PeripheralDurian, 2.x/device-side calculated connection/body scale



#### 2.4.1 Weighing logic



```mermaid    
 graph TD    
 A[Initiate connection device<br>startConnect]-->B[Monitor Bluetooth connection status<br>PPBleStateInterface]    
 B-->B1[monitorBluetoothWorkState<br>Bluetooth scanning and connection status monitoring<br>PPBleWorkStateConnected connection successful<br>PPBleWorkStateConnectFailed connection failed<br>PPBleWorkStateDisconnected disconnected<br>PPBleWorkStateWritable writable]    
 B1-->C2[Current writable<br>PPBleWorkStateWritable] --> D[Switch unit<br>syncUnit]    
 D[Switch unit<br>syncUnit]--Monitor Bluetooth write callback<br>PPBleSendResultCallBack-->D1[Synchronization time<br>syncTime]    
    
    
 A-->F[Device data change monitoring<br>PPDataChangeListener]    
 F[Device data change monitoring<br>PPDataChangeListener]-->G1[Process data<br>monitorProcessData]    
 F[Device data change monitoring<br>PPDataChangeListener]-->G2[Lock data<br>monitorLockData]    
 G1--Get the current unit of your App<br> and convert it to PPUnitType-->H1[Unit conversion<br>PPUtil.getWeightValueD]-->I1[Real-time display of weight data UI]    
 G2--Get the current unit of your App<br> and convert it to PPUnitType-->H2[Unit conversion<br>PPUtil.getWeightValueD]-->I2[Judge heart rate status]    
 I2[Judge heart rate status]-->J1[Measuring heart rate]-->K1[Display heart rate measurement UI]    
 I2[Judge heart rate status]-->J2[Heart rate measurement completed]-->K2[Match user information<br>Give PPBodyBaseModelbodyBaseModel.PPUserModel]-->L[Call the calculation library to calculate body fat<br>Four-electrode AC example: Calculate4ACActivitiy<br>Four-electrode DC example: Calculate4DCActivitiy]    
 ```   


### 2.5 PeripheralEgg Function Description-PeripheralEggActivity



**Notice:**



- By default, Bluetooth permissions and switch detection have been processed, and the type matched to PPDevicePeripheralType is PeripheralEgg, 2.x/Connection/Kitchen Scale

#### 2.5.1 Weighing logic



```mermaid    
 graph TD    
 A[Initiate connection device<br>startConnect]-->B[Monitor Bluetooth connection status<br>PPBleStateInterface]    
 B-->B1[monitorBluetoothWorkState<br>Bluetooth scanning and connection status monitoring<br>PPBleWorkStateConnected connection successful<br>PPBleWorkStateConnectFailed connection failed<br>PPBleWorkStateDisconnected disconnected<br>PPBleWorkStateWritable writable]    
 B1-->C2[Current writable<br>PPBleWorkStateWritable] --> D[Switch unit<br>syncUnit]    
    
    
 A-->F[Device data change monitoring<br>PPDataChangeListener]    
 F[Equipment data change monitoring<br>FoodScaleDataChangeListener]-->G1[Process data<br>monitorProcessData]    
 F-->G2[Lock data<br>monitorLockData]    
 G1--Get the current unit of your App<br> and convert it to PPUnitType-->H1[Unit conversion<br>PPUtil.getWeightValueD]-->I1[Real-time display of weight data UI]    
 G2--Get the current unit of your App<br> and convert it to PPUnitType-->H2[Unit conversion<br>PPUtil.getWeightValueD]-->I1[Real-time display of weight data UI]    
 ```   


### 2.6 PeripheralFish function description-PeripheralFishActivity



**Notice:**



- By default, Bluetooth permissions and switch detection have been processed, and the type matched to PPDevicePeripheralType is PeripheralFish, 3.x/Connection/Kitchen Scale



```mermaid    
 graph TD    
 A[Initiate connection device<br>startConnect]-->B[Monitor Bluetooth connection status<br>PPBleStateInterface]    
 B-->B1[monitorBluetoothWorkState<br>Bluetooth scanning and connection status monitoring<br>PPBleWorkStateConnected connection successful<br>PPBleWorkStateConnectFailed connection failed<br>PPBleWorkStateDisconnected disconnected<br>PPBleWorkStateWritable writable]    
 B1-->C2[Current writable<br>PPBleWorkStateWritable] --> D[Switch unit<br>syncUnit]    
    
    
 A-->F[Device data change monitoring<br>PPDataChangeListener]    
 F[Device data change monitoring<br>PPDataChangeListener]-->G1[Process data<br>monitorProcessData]    
 F[Device data change monitoring<br>PPDataChangeListener]-->G2[Lock data<br>monitorLockData]    
 G1--Get the current unit of your App<br> and convert it to PPUnitType-->H1[Unit conversion<br>PPUtil.getWeightValueD]-->I1[Real-time display of weight data UI]    
 G2--Get the current unit of your App<br> and convert it to PPUnitType-->H2[Unit conversion<br>PPUtil.getWeightValueD]-->I1[Real-time display of weight data UI]    
 ```   


### 2.7 PeripheralGrapes function description-PeripheralGrapesActivity



**Notice:**



- By default, Bluetooth permissions and switch detection have been processed, and the type matched to PPDevicePeripheralType is PeripheralGrapes, 2.x/Broadcast/Kitchen Scale



```mermaid    
 graph TD    
 A[Initiate Bluetooth scan<br>startSearch]-->B[Monitor Bluetooth scan status<br>PPBleStateInterface]-->C[monitorBluetoothWorkState<br>PPBleStateSearchCanceled to stop scanning<br>PPBleWorkSearchTimeOutScan timeout<br>PPBleWorkStateSearching scanning]    
 A-->F[Device data change monitoring<br>PPDataChangeListener]    
 F-->G1[Process data<br>monitorProcessData]    
 F-->G2[Lock data<br>monitorLockData]    
 G1--Get the current unit of your App<br> and convert it to PPUnitType-->H1[Unit conversion<br>PPUtil.getWeightValueD]-->I1[Real-time display of weight data UI]    
 G2--Get the current unit of your App<br> and convert it to PPUnitType-->H2[Unit conversion<br>PPUtil.getWeightValueD]-->I1[Real-time display of weight data UI]    
 ```   


### 2.8 PeripheralHamburger function description-PeripheralHamburgerActivity



**Notice:**



- By default, Bluetooth permissions and switch detection have been processed, and the type matched to PPDevicePeripheralType is PeripheralHamburger, 3.x/Broadcast/Kitchen Scale

```mermaid    
 graph TD    
 A[Initiate Bluetooth scan<br>startSearch]-->B[Monitor Bluetooth scan status<br>PPBleStateInterface]-->C[monitorBluetoothWorkState<br>PPBleStateSearchCanceled to stop scanning<br>PPBleWorkSearchTimeOutScan timeout<br>PPBleWorkStateSearching scanning]    
 A-->F[Device data change monitoring<br>PPDataChangeListener]    
 F-->G1[Process data<br>monitorProcessData]    
 F-->G2[Lock data<br>monitorLockData]    
 G1--Get the current unit of your App<br> and convert it to PPUnitType-->H1[Unit conversion<br>PPUtil.getWeightValueD]-->I1[Real-time display of weight data UI]    
 G2--Get the current unit of your App<br> and convert it to PPUnitType-->H2[Unit conversion<br>PPUtil.getWeightValueD]-->I1[Real-time display of weight data UI]    
 ```   


### 2.9 PeripheralIce function description-PeripheralIceActivity



**Notice:**



- By default, Bluetooth permissions and switch detection have been processed, and the type matched to PPDevicePeripheralType is PeripheralIce, 4.0/Connection/Body Scale



#### 2.9.1 Weighing logic



```mermaid    
 graph TD    
 A[Initiate Bluetooth scan<br>startSearch]-->B[Monitor Bluetooth scan status<br>PPBleStateInterface]-->C[monitorBluetoothWorkState<br>PPBleStateSearchCanceled to stop scanning<br>PPBleWorkSearchTimeOutScan timeout<br>PPBleWorkStateSearching scanning]    
 A-->F[Device data change monitoring<br>PPDataChangeListener]    
 F-->G1[Process data<br>monitorProcessData]    
 F-->G2[Lock data<br>monitorLockData]    
 G1--Get the current unit of your App<br> and convert it to PPUnitType-->H1[Unit conversion<br>PPUtil.getWeightValueD]-->I1[Real-time display of weight data UI]    
 G2--Get the current unit of your App<br> and convert it to PPUnitType-->H2[Unit conversion<br>PPUtil.getWeightValueD]-->I2[Judge heart rate status]    
 I2[Judge heart rate status]-->J1[Measuring heart rate]-->K1[Display heart rate measurement UI]    
 I2[Judge heart rate status]-->J2[Heart rate measurement completed]-->K2[Match user information<br>Give PPBodyBaseModelbodyBaseModel.PPUserModel]-->L[Call the calculation library to calculate body fat<br>Example: Calculate4ACActivitiy]    
 ```   


#### 2.9.2 Read Bluetooth historical data



**Prerequisite: Bluetooth is connected**



```mermaid    
 graph TD    
 A[Read historical data<br>getHistoryData]-->A1[History data monitoring]-->B1[Historical data callback<br>monitorHistoryData]    
 B1[Historical data callback<br>monitorHistoryData]-->C[Use List to store historical data]    
 A1-->B2[Historical data end callback<br>monitorHistoryEnd]    
 B2-->D[Get the historical data of the list]-->E[Match user information<br>Give PPBodyBaseModelbodyBaseModel.PPUserModel]-->F[Call the calculation library to calculate body fat<br>Example: Calculate4ACActivitiy]    
 B2-->G[Delete historical data<br>deleteHistoryData]    
 A1-->B3[Historical data failure callback<br>monitorHistoryFail]    
 ```   


#### 2.9.3 Network distribution process



**Prerequisite: Bluetooth is connected**

Notice:



- Make sure the Server is normal and the router can connect to the Server normally

- Make sure the WiFi environment is 2.4G or 2.4/5G mixed mode, single 5G mode is not supported

- Make sure the account password is correct

- Make sure that the server address used by the scale corresponds to the server address used by the App



**Notice**

Note: The domain name in your own App needs to be replaced with your App’s own server domain name, and ensure that the server has completed the development of Wifi body fat scale related functions.

Document address: https://uniquehealth.lefuenergy.com/unique-open-web/#/document

Find "Lefu Body Fat Scale Self-Built Server Access Solution" -> PeripheralIce/PeripheralTorre corresponds to "Torre Series Products"



```mermaid    
 graph TD    
 A[Check whether network distribution is supported<br>PPScaleHelper.isFuncTypeWifi]--Support-->A1[Get Wifi list<br>getWifiList<br>Register Wifi list callback<br>PPConfigWifiInfoInterface]    
 A--Not supported-->A2[Processing UI display]    
 A1-->B1[Wifi list returned successfully<br>monitorWiFiListSuccess]-->B2[Empty-there is no supported Wifi around]    
 B1-->B3[not empty]-->B4[display Wifi list]-->B5[user selects a Wifi]-->B6[enter password]    
 B6-->D[Start issuing server domain name<br>sendModifyServerDomain:domainName, PPConfigWifiInfoInterface]-->F[Server domain name modified successfully<br>monitorModifyServerDomainSuccess]    
 F-->G[Configure SSID and password<br>configWifiData:ssid, pwd,PPConfigWifiInfoInterface]    
 G-->H1[SSID and password configured successfully<br>monitorConfigSn]    
 G-->H2[Failed to configure SSID and password<br>monitorConfigFail]-->C21[Prompt user, troubleshoot the cause]-->C22[Reconfiguration of network]    
 D--The user exits during the distribution network-->E[The user returns manually-exits the distribution network<br>exitConfigWifi]    
 A1-->C[Wifi list return failure<br>monitorWiFiListFail]-->C21    
 ```   


### 2.10 PeripheralJambul function description-PeripheralJambulActivity



**Notice:**



- By default, Bluetooth permissions and switch detection have been processed, and the type of PPDevicePeripheralType is matched to PeripheralJambul, 3.x/broadcast/body scale



#### 2.10.1 Complete weighing logic



```mermaid    
 graph TD    
 A[Initiate Bluetooth scan<br>startSearch]-->B[Monitor Bluetooth scan status<br>PPBleStateInterface]-->C[monitorBluetoothWorkState<br>PPBleStateSearchCanceled to stop scanning<br>PPBleWorkSearchTimeOutScan timeout<br>PPBleWorkStateSearching scanning]    
 A-->F[Device data change monitoring<br>PPDataChangeListener]    
 F-->G1[Process data<br>monitorProcessData]    
 F-->G2[Lock data<br>monitorLockData]    
 G1--Get the current unit of your App<br> and convert it to PPUnitType-->H1[Unit conversion<br>PPUtil.getWeightValueD]-->I1[Real-time display of weight data UI]    
 G2--Get the current unit of your App<br> and convert it to PPUnitType-->H2[Unit conversion<br>PPUtil.getWeightValueD]-->I2[Judge heart rate status]    
 I2[Judge heart rate status]-->J1[Measuring heart rate]-->K1[Display heart rate measurement UI]    
 I2[Judge heart rate status]-->J2[Heart rate measurement completed]-->K2[Match user information<br>Give PPBodyBaseModelbodyBaseModel.PPUserModel]-->L[Call the calculation library to calculate body fat<br>Example: Calculate4ACActivitiy]    
 ```   


### 2.11 PeripheralTorre function description-PeripheralTorreActivity



**Notice:**



- By default, Bluetooth permission and switch detection have been processed, and the type matched to PPDevicePeripheralType is PeripheralTorre, TORRE/Connection/Body Scale

- You need to judge whether history is supported before handling history-related functions.

- You need to judge whether it supports Wifi by yourself, and then handle Wifi related functions.



#### 2.11.1 Weighing process



**Prerequisite: Bluetooth is connected**

```mermaid    
 graph TD  
 A[Initiate connection device<br>startConnect]-->B[Monitor Bluetooth status<br>PPBleStateInterface]-->C1[Bluetooth device status<br>monitorBluetoothWorkState]    
 B-->C2[System Bluetooth Status<br>monitorBluetoothSwitchState]    
 B-->C3[Mtu status<br>monitorMtuChange]-->D[synchronization time<br>]-->E[synchronization unit<br>syncUnit]    
 E--User information needs to be synchronized-->F[Go to 2.9.2 business logic]-->F1[Synchronization of information completed]-->G  
 E--No need to synchronize user information-->G[Select the current user<br>confirmCurrentUser]-->G1[Select the current user successfully<br>confirmCurrentUserInfoSuccess]
 G1-->H[Start measurement<br>startMeasure]--Register data change listening<br>registDataChangeListener-->I[Measurement result callback<br>PPDataChangeListener]
 I-->I1[Process Data<br>monitorProcessData]--Get the current unit of your App<br> and convert it to PPUnitType-->I11[Unit conversion<br>PPUtil.getWeightValueD]-->I12[Real-time display of weight data UI]
 I-->I2[Lock data<br>monitorLockData]-->I21[Heart rate status<br>isHeartRating]  
 I21--true-->I211[Measuring heart rate]-->K1[Showing heart rate measurement UI]    
 I21--false-->I212[End of measurement]-->J[Match user information based on memberID<br>Give PPBodyBaseModelbodyBaseModel.PPUserModel]-->K[Call the calculation library to calculate body fat<br>Eight-electrode example: Calculate8ACActivitiy< br>Four-electrode example: Calculate4ACActivitiy]    
 I-->I3[Impedance measurement<br>onImpedanceFatting]-->I31[Display UI according to your own business]    
 I-->I4[Overweight<br>monitorOverWeight]-->I41[Display UI according to your own business]    
 I-->I5[Device shutdown callback<br>onDeviceShutdown]-->I51[Display UI according to your own business]    
 I-->I6[Historical data changes<br>onHistoryDataChange]-->I61[Actively read historical data]    
 G-->G2[Current user selection failed]-->H    

```      


#### 2.11.2 Complete user information synchronization process



**Prerequisite: Bluetooth is connected**



```mermaid    
 graph TD    
 E[Need to synchronize user information<br>App itself records whether the current device needs to be synchronized]-->F1[Delete all users on the device<br>deleteAllUserInfo]    
 F1--Delete successfully-->G[Synchronize user information<br>syncUserInfo cycle synchronization<br>until synchronization is completed-supports 5 accounts, each account/10 users]    
 G-->G1[One user synchronization completed<br>syncUserInfoSuccess]-->G2[Synchronization of the next]--Loop synchronization-->G    
 G-->H[All users synchronized<br>syncUserInfoSuccess]    
 G-->H1[User synchronization failed<br>syncUserInfoFail]-->H11[Prompt user]    
    
    
 ```   


#### 2.11.3 Complete network distribution process



**Prerequisite: Bluetooth is connected**



Notice:

- Make sure the Server is normal and the router can connect to the Server normally

- Make sure the WiFi environment is 2.4G or 2.4/5G mixed mode, single 5G mode is not supported

- Make sure the account password is correct

- Make sure that the server address used by the scale corresponds to the server address used by the App



**Notice**

Note: The domain name in your own App needs to be replaced with your App’s own server domain name, and ensure that the server has completed the development of Wifi body fat scale related functions.

Document address: https://uniquehealth.lefuenergy.com/unique-open-web/#/document

Find "Lefu Body Fat Scale Self-Built Server Access Solution" -> PeripheralIce/PeripheralTorre corresponds to "Torre Series Products"



```mermaid    
 graph TD    
 A[Check whether network distribution is supported<br>PPScaleHelper.isFuncTypeWifi]--Support-->A1[Get Wifi list<br>getWifiList<br>Register Wifi list callback<br>PPTorreConfigWifiInterface]    
 A--Not supported-->A2[Processing UI display]    
 A1-->B1[Wifi list returned successfully<br>monitorWiFiListSuccess]-->B2[Empty-there is no supported Wifi around]    
 B1-->B3[not empty]-->B4[display Wifi list]-->B5[user selects a Wifi]-->B6[enter password]    
 B6-->D[Start delivering distribution network information<br>configWifi:domainName,ssid,password,configWifiInterface]-->C    
 D--The user exits during the distribution network-->E[The user returns manually-exits the distribution network<br>exitConfigWifi]    
 A1-->C[Distribution network status<br>configResult]    
 C-->C1[Network distribution successful<br>CONFIG_STATE_SUCCESS]-->C11[Return to the entrance and re-obtain WifiSSID<br>getWifiSSID]    
 C-->C2[Network configuration failed<br>See enumeration class PPConfigStateMenu for details]-->C21[Prompt user, troubleshoot the reason]-->C22[Re-configuration network]    
 C-->C3[Exit configuration network status<br>CONFIG_STATE_EXIT]    
 ```   


#### 2.11.4 Master user historical data synchronization



**Prerequisite: Bluetooth is connected**



- With master user history: refers to historical data with valid memberID



```mermaid    
 graph TD    
 A[With master user historical data synchronization<br>syncUserHistory]-->B[Register historical data listening<br>OnTorreHistoryDataListener]    
 B-->B1[Master historical data synchronization is successful<br>onSyncUserHistorySuccess]    
 B-->B2[Synchronizing xx user history data<br>onStartSyncUserHistory]    
 B-->B3[Synchronization failed<br>onHistoryFail]    
 B-->B4[Synchronization completed with master user<br>onHistoryEnd]    
 B4-->C[Match user information based on memberID<br>Give to PPBodyBaseModelbodyBaseModel.PPUserModel]-->D[Calculate the calculation library to calculate body fat<br>Eight-electrode example: Calculate8ACActivitiy<br>Four-electrode example: Calculate4ACActivitiy]--> E[Store data to data table]    
 ```   


#### 2.11.5 Unowned user historical data synchronization



**Prerequisite: Bluetooth is connected**



- Unowned user history: refers to historical data without a valid memberID. The memberID is all 64 zeros or empty.



```mermaid    
 graph TD    
 A[Unowned user historical data synchronization<br>syncUserHistory]-->B[Register historical data listening<br>OnTorreHistoryDataListener]    
 B-->B1[Unowned historical data synchronization successful<br>onTouristUserHistorySuccess]    
 B-->B2[Synchronization failed<br>onHistoryFail]    
 B-->B3[Unowned user synchronization completed<br>onHistoryEnd]    
 B3-->D[Storage data as unowned data and let users claim the data themselves]    
 ```   


#### 2.11.5 Function list

| Function | Method name | Parameters | Return data type | Remarks |  
|--------|--------|--------|--------|----------|  
|Bluetooth connection| startConnect|PPDeviceModel|PPBleStateInterface| Bluetooth related status|  
|Start measurement| startMeasure | | PPDataChangeListener | Bluetooth related status|  
|Stop measurement| stopMeasure|PPDeviceModel|PPBleStateInterface| Bluetooth related status|  
|Synchronization time| syncTime | | PPBleSendResultCallBack-onResult(PPScaleSendState sendState) | Send status callback|  
|Read device information| readDeviceInfo | | PPDeviceInfoInterface-readDeviceInfoComplete | Device information callback, supported by some scales (including modelNumber, softwareVersion, serialNumber) |  
|Synchronization unit| syncUnit | PPUnitType,PPUserModel | PPBleSendResultCallBack|Send status callback|  
|Register data change listening| registDataChangeListener |PPDataChangeListener|monitorProcessData process data<br>monitorLockData lock data<br>monitorOverWeight overweight|
|Whether history is supported| PPScaleHelper-<br> isSupportHistoryData |deviceFuncType|Boolean|true supported false not supported|  
|Whether Wifi is supported| PPScaleHelper-<br>isFuncTypeWifi |deviceFuncType|Boolean|true supported false not supported|  
|Synchronize user data| syncUserInfo |PPUserModel|PPUserInfoInterface|Synchronize multiple users one by one|  
|Delete user data| deleteAllUserInfo |deviceFuncType|PPUserInfoInterface|Delete all sub-members under the userId based on userID|  
|Confirm current user| confirmCurrentUser |PPUserModel|PPUserInfoInterface|
|Get user list| getUserList ||PPUserInfoInterface|true supported false not supported|  
|Start network configuration| startConfigWifi ||Boolean|true supported false not supported|  
|Get SSID| getWifiSSID |deviceFuncType|Boolean|true supported false not supported|  
|GetWifiMac| getWifiMac |deviceFuncType|Boolean|true supported false not supported|  
|Clear user| clearDeviceUserInfo |deviceFuncType|Boolean|true supported false not supported|  
|Get unit| getUnit |deviceFuncType|Boolean|true supported false not supported|  
|Device information| readDeviceInfoFromCharacter |deviceFuncType|Boolean|true supported false not supported|  
|Get battery power| readDeviceBattery |deviceFuncType|Boolean|true supported false not supported|  
|Get brightness| getLight |deviceFuncType|Boolean|true supported false not supported|  
|Set brightness| setLight |light|Boolean|0-100|  
|User history data| syncUserHistory |PPUserModel|PPHistoryDataInterface|
|Tourist History Data| syncTouristHistory ||PPHistoryDataInterface|
|Restore factory settings| resetDevice |deviceFuncType|Boolean|true supported false not supported|  
|Start keep-alive| startKeepAlive |||No need to actively exit keep-alive|  



## Ⅵ. Entity class objects and specific parameter descriptions



### 1.1 PPBodyFatModel body fat calculation object parameter description



24 items of data corresponding to four electrodes

48 items of data corresponding to eight electrodes

| Parameters | Parameter type | Description | Data type | Remarks|  
|------|--------|--------|--------|--------|  
|ppBodyBaseModel| PPBodyBaseModel |Input parameters for body fat calculation|Basic input parameters|Contains device information, user basic information, weight and heart rate|Body fat scale|  
|ppSDKVersion| String |Computation library version number|Return parameters|
|ppSex| PPUserGender|Gender|Return Parameters| PPUserGenderFemaleFemale<br>PPUserGenderMaleMale|  
|ppHeightCm|Int |Height|Return parameters|cm|  
|ppAge|Int |Age|Return Parameters|Years|  
|errorType|BodyFatErrorType |Error type|Return parameters|PP_ERROR_TYPE_NONE(0), no error<br>PP_ERROR_TYPE_AGE(1), age is wrong<br>PP_ERROR_TYPE_HEIGHT(2), height is wrong<br>PP_ERROR_TYPE_WEIGHT(3), weight is Wrong<br>PP_ERROR_TYPE_SEX(4) Wrong gender<br>PP_ERROR_TYPE_PEOPLE_TYPE(5) <br>The following is wrong impedance<br>PP_ERROR_TYPE_IMPEDANCE_TWO_LEGS(6) <br>PP_ERROR_TYPE_IMPEDANCE_TWO_ARMS(7)<br>PP_ERROR_TYPE_IMPEDANCE_LEFT _BODY(8) <br> PP_ERROR_TYPE_IMPEDANCE_RIGHT_ARM(9)<br>PP_ERROR_TYPE_IMPEDANCE_LEFT_ARM(10) <br>PP_ERROR_TYPE_IMPEDANCE_LEFT_LEG(11) <br>PP_ERROR_TYPE_IMPEDANCE_RIGHT_LEG(12) <br>PP_ERROR_TYPE_IMPEDANCE_TRUNK(13) )|  
|bodyDetailModel|PPBodyDetailModel|Data interval range and introduction description|
|ppWeightKg|Float |Weight|24&48|kg|  
|ppBMI|Float|Body Mass Index|24&48|
|ppFat|Float |Fat rate|24&48|%|  
|ppBodyfatKg|Float |Fat mass|24&48|kg|  
|ppMusclePercentage|Float |Muscle Percentage|24&48|%|  
|ppMuscleKg|Float |Muscle mass|24&48|kg|  
|ppBodySkeletal|Float |Skeletal muscle rate|24&48|%|  
|ppBodySkeletalKg|Float |Skeletal muscle mass|24&48|kg|  
|ppWaterPercentage|Float |Moisture percentage|24&48|%|  
|ppWaterKg|Float |Moisture content|24&48|kg|  
|ppProteinPercentage|Float |Protein rate|24&48|%|  
|ppProteinKg|Float |Protein mass|24&48|kg|  
|ppLoseFatWeightKg|Float |Lean body mass|24&48|kg|  
|ppBodyFatSubCutPercentage|Float |Subcutaneous fat rate|24&48|%|  
|ppBodyFatSubCutKg|Float |Subcutaneous fat mass|24&48|kg|  
|ppHeartRate|Int |Heart rate|24&48|bmp This value is related to the scale, and is valid if it is greater than 0|  
|ppBMR|Int |Basal Metabolism|24&48|
|ppVisceralFat|Int |Visceral fat level|24&48|
|ppBoneKg|Float |Bone mass|24&48|kg|  
|ppBodyMuscleControl|Float |Muscle control volume|24&48|kg|  
|ppFatControlKg|Float |Fat control volume|24&48|kg|  
|ppBodyStandardWeightKg|Float |Standard weight|24&48|kg|  
|ppIdealWeightKg|Float |Ideal Weight|24&48|kg|  
|ppControlWeightKg|Float |Control weight|24&48|kg|  
|ppBodyType|PPBodyDetailType |Body type|24&48|PPBodyDetailType has a separate description|  
|ppFatGrade|PPBodyFatGrade|Obesity grade|24&48|PPBodyGradeFatThin(0), //!< Thin<br>PPBodyGradeFatStandard(1),//!< Standard<br>PPBodyGradeFatOverwight(2), //!< Overweight<br> PPBodyGradeFatOne(3),//!< Obesity level 1<br>PPBodyGradeFatTwo(4),//!< Obesity level 2<br>PPBodyGradeFatThree(5);//!< Obesity level 3|  
|ppBodyHealth|PPBodyHealthAssessment |Health Assessment|24&48|PPBodyAssessment1(0), //!< Hidden health risks<br>PPBodyAssessment2(1), //!< Sub-health<br>PPBodyAssessment3(2), //!< General< br>PPBodyAssessment4(3), //!< Good<br> PPBodyAssessment5(4); //!< Very good|  
|ppBodyAge|Int|Body Age|24&48|Years|  
|ppBodyScore|Int |Body Score|24&48|Points|  
|ppCellMassKg|Float |Body Cell Mass|48|kg|  
|ppDCI|Int |Recommended calorie intake|48|Kcal/day|  
|ppMineralKg|Float |Inorganic salt amount|48|kg|  
|ppObesity|Float |Obesity|48|%|  
|ppWaterECWKg|Float |Extracellular water volume|48|kg|  
|ppWaterICWKg|Float |Intracellular water volume|48|kg|  
|ppBodyFatKgLeftArm|Float |Left hand fat mass|48|kg|  
|ppBodyFatKgLeftLeg|Float |Left foot fat amount|48|kg|  
|ppBodyFatKgRightArm|Float |Right hand fat mass|48|kg|  
|ppBodyFatKgRightLeg|Float |Fat mass of right foot|48|kg|  
|ppBodyFatKgTrunk|Float |Trunk fat mass|48|kg|  
|ppBodyFatRateLeftArm|Float |Left hand fat rate|48|%|  
|ppBodyFatRateLeftLeg|Float |Left foot fat rate|48|%|  
|ppBodyFatRateRightArm|Float |Right hand fat rate|48|%|  
|ppBodyFatRateRightLeg|Float |Right foot fat rate|48|%|  
|ppBodyFatRateTrunk|Float |Trunk fat rate|48|%|  
|ppMuscleKgLeftArm|Float |Left hand muscle mass|48|kg|  
|ppMuscleKgLeftLeg|Float |Left foot muscle mass|48|kg|  
|ppMuscleKgRightArm|Float |Right hand muscle mass|48|kg|  
|ppMuscleKgRightLeg|Float |Right foot muscle mass|48|kg|  
|ppMuscleKgTrunk|Float |Trunk muscle mass|48|kg|  



Note: When using the object, please call the corresponding get method to obtain the corresponding value.



### 1.2 Body type-PPBodyDetailType



| Parameters | Description | type |  
|------|--------|--------|  
|LF_BODY_TYPE_THIN|Thin type|0|  
|LF_BODY_TYPE_THIN_MUSCLE|Thin muscular type|1|  
|LF_BODY_TYPE_MUSCULAR|Muscular type|2|  
|LF_BODY_TYPE_LACK_EXERCISE|Lack of athleticism|3|  
|LF_BODY_TYPE_STANDARD|Standard type|4|  
|LF_BODY_TYPE_STANDARD_MUSCLE|Standard muscle type|5|  
|LF_BODY_TYPE_OBESE_FAT|Puffy and obese type|6|  
|LF_BODY_TYPE_FAT_MUSCLE|Fat muscular type|7|  
|LF_BODY_TYPE_MUSCLE_FAT|Muscle type fat|8|  



### 1.3 Device object-PPDeviceModel



|Attribute name|Type|Description|Remarks|  
| ------ | ----- | ----- | ----- |  
| deviceMac | String | device mac|device unique identifier| | deviceName | String | Device Bluetooth name | Device name identification | | devicePower | Int | Power | -1 flag is not supported > 0 is a valid value | | rssi | Int | Bluetooth signal strength |  
| firmwareVersion | String? | Firmware version number | To actively call readDeviceInfo after connection |  
| hardwareVersion | String? | Hardware version number | To actively call readDeviceInfo after connection |  
| manufacturerName | String? | Manufacturer | To actively call readDeviceInfo after connection |  
| softwareVersion | String? | Software version number | To actively call readDeviceInfo after connection |  
| serialNumber | String? | Serial number | Actively call readDeviceInfo after connection |  
| modelNumber | String? | Time zone number | To actively call readDeviceInfo after connection |  
| deviceType | PPDeviceType | Device type|PPDeviceTypeUnknow, //Unknown<br>PPDeviceTypeCF,//Body fat scale<br>PPDeviceTypeCE, //Weight scale<br>PPDeviceTypeCB,// Baby scale<br>PPDeviceTypeCA; // Kitchen scale |  
| deviceProtocolType | PPDeviceProtocolType | Protocol mode | PPDeviceProtocolTypeUnknow(0),//Unknown<br>PPDeviceProtocolTypeV2(1),//Use V2.x Bluetooth protocol<br> PPDeviceProtocolTypeV3(2),//Use V3.x Bluetooth protocol<br >PPDeviceProtocolTypeTorre(3),//Torre protocol<br> PPDeviceProtocolTypeV4(4);//V4.0 protocol|  
| deviceCalcuteType | PPDeviceCalcuteType | Calculation method |PPDeviceCalcuteTypeUnknow(0),//Unknown<br> PPDeviceCalcuteTypeInScale(1), //Scale side calculation<br> PPDeviceCalcuteTypeDirect(2), //DC 4DC <br> PPDeviceCalcuteTypeAlternate(3),/ /AC 4AC br> PPDeviceCalcuteTypeAlternate8(4),// 8-electrode AC algorithm<br> PPDeviceCalcuteTypeNormal(5), //The default body fat rate adopts the original value -4AC <br> PPDeviceCalcuteTypeNeedNot(6),//No calculation required< br> PPDeviceCalcuteTypeAlternate8_0(7);//8 electrode algorithm, bhProduct =0 |  
| deviceAccuracyType | PPDeviceAccuracyType | Accuracy|PPDeviceAccuracyTypeUnknow(0), //Unknown accuracy<br> PPDeviceAccuracyTypePoint01(1), //Accuracy 0.1 <br> PPDeviceAccuracyTypePoint005(2), //Accuracy 0.05 <br> PPDeviceAccuracyTypePointG(3), // 1G accuracy<br> PPDeviceAccuracyTypePoint01G(4), // 0.1G accuracy<br> PPDeviceAccuracyTypePoint001(5); //0.01KG accuracy|  
| devicePowerType | PPDevicePowerType | Power supply mode|PPDevicePowerTypeUnknow(0),//Unknown<br>PPDevicePowerTypeBattery(1),//Battery powered<br>PPDevicePowerTypeSolar(2),//Solar powered<br>PPDevicePowerTypeCharge(3); // Rechargeable model |  
| deviceConnectType | PPDeviceConnectType | Device connection type |PPDeviceConnectTypeUnknow(0), <br>PPDeviceConnectTypeBroadcast(1), //Broadcast<br>PPDeviceConnectTypeDirect(2), //Direct connection<br>PPDeviceConnectTypeBroadcastOrDirect(3); //Broadcast or direct even|  
| deviceFuncType | Int | Function type | PPScaleHelper-isSupportHistoryData//Determine whether history is supported<br>PPScaleHelper-isFuncTypeWifi//Determine whether Wifi is supported <br>PPScaleHelper-isFat//Determine whether fat measurement is supported|  
| deviceUnitType | String | Supported units | Separated by ",", corresponding to the type of PPUnitType |  



### 1.4 Equipment unit-PPUnitType



| enum type | type | unit |  
| -- | ---- |---- |  
|Unit_KG| 0 | KG |  
|Unit_LB| 1 | LB |  
|PPUnitST_LB| 2 | ST_LB |  
|PPUnitJin| 3 | Jin|  
|PPUnitG| 4 | g |  
|PPUnitLBOZ| 5 | lb:oz |  
|PPUnitOZ| 6 | oz |  
|PPUnitMLWater| 7 | ml (water) |  
|PPUnitMLMilk| 8 | ml (milk) |  
|PPUnitFL_OZ_WATER| 9 | fl_oz (water) |  
|PPUnitFL_OZ_MILK| 10 | fl_oz (milk) |  
|PPUnitST| 11 | ST |



### 2.0 Food Scale-LFFoodScaleGeneral Parameter Description


| Attribute name | Type | Description |
| ------ | ---- | ---- |
| lfWeightKg | double | weight |  
| unit | PPUnitType | unit |  
| byteNum | int | Number of bytes |  
| thanZero | int | positive and negative identifiers|  
| scaleType | String | Scale type |  


## Ⅶ. [Version update instructions](doc/version_update.md)



## Ⅷ. Third-party libraries used



### 1. Body fat calculation library provided by chip solution provider



### 2. [bluetoothkit1.4.0 Bluetooth library](https://github.com/dingjikerbo/Android-BluetoothKit)



## Ⅸ. [FAQ](doc/common_problem.md)



Contact Developer: Email: yanfabu-5@lefu.cc