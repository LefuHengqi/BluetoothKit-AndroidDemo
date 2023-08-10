[English Docs](README_EN.md) | [Chinese Docs](README.md)

## [PPScale iOS SDK](https://gitee.com/shenzhen-lfscale/bluetooth-kit-iosdemo)

# PPScale Android SDK

## -LF Bluetooth Scale/Food Scale/WiFi Scale

ppscale is the Bluetooth connection logic and data parsing logic. When developers integrate, they need to integrate by importing aar. It is recommended that developers check the README.md document to complete the integration.

### Ⅰ. Integration method

#### sdk import method

Add it to the build.gradle under the module that needs to import the sdk (please check the libs under the module of ppscalelib for the latest version)

     dependencies {
         //aar import
         api fileTree(include: ['*.jar', '*.aar'], dir: 'libs')
     }

### Ⅱ . Instructions for use

### 1.1 Bluetooth permission related

#### 1.1.1 Operating environment

Due to the need for Bluetooth connection, Demo needs to run on a real device, Android phone 6.0 and above or HarmonyOS2.0 and above

#### 1.1.2 Conventions related to Bluetooth permissions

In the process of using the demo, you need to turn on the bluetooth and turn on the positioning switch, and you need to ensure that the necessary permissions are turned on and authorized

##### 1.1.2.1 In Android 6.0 and above system version,

1. Positioning authority
2. Position switch
3. Bluetooth switch

##### 1.1.2.2 In Android 12.0 and above system versions, before starting the scan, make sure to enable and authorize the necessary permissions

You can view the official Bluetooth permissions document, the document address: [Google developer website about Bluetooth permissions] (https://developer.android.com/guide/topics/connectivity/bluetooth/permissions).

1. Positioning authority
2. Position switch
3. Bluetooth switch
4. Scan and connect nearby devices

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

#### 1.1.3 Conventions related to measuring body data

If you need body fat information other than weight, you need to enter height, age, gender and go on the scale barefoot.

### 1.2 Home page function description

#### 1.2.1 oldVersionSDK

[Old Version SDK English Docs](doc/README_old_version_en.md) | [Old Version SDK Chinese Document](doc/README_old_version.md)

#### 1.2.2 Caclulate - CalculateManagerActivity

According to the body weight and impedance analyzed by the Bluetooth protocol, plus the height, age, and gender of the user data, the body fat rate and other body fat parameter information are calculated.

##### 1.2.2.1 8-electrode AC body fat calculation - 8AC - Calculate8Activitiy

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

##### 1.2.2.2 4 Electrode DC Body Fat Calculation - 4DC - Calculate4DCActivitiy

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

### Ⅲ .PPScale in the use of Bluetooth devices

#### 1.1 PPScale initialization, and basic conventions

         //The difference between binding a device and scanning a device is that searchType 0 binds a device and 1 scans a specified device
         //setProtocalFilterImpl() Receive data callback interface, process data, lock data, historical data,
         //The parameter of the setDeviceList() function is not empty, you need to pass null (or not call) for binding, and please pass the List<DeviceModel> of the specified device to scan the specified device. to bind
         //setBleOptions() Bluetooth parameter configuration
         //setBleStateInterface() requires parameters PPBleStateInterface, Bluetooth status monitoring callback and system Bluetooth status callback
         //startSearchBluetoothScaleWithMacAddressList() starts scanning devices
         //setUserModel() parameter PPUserModel is normal, setUserModel() is necessary,
            
        /**
         * sdk entry, instance object
         */
        private void bindingDevice() {
                if (searchType == 0) {
                    ppScale = new PPScale. Builder(this)
                            .setProtocalFilterImpl(getProtocalFilter())
                            .setBleOptions(getBleOptions())
        // .setDeviceList(null)
                            .setBleStateInterface(bleStateInterface)
                            .setUserModel(userModel)
                            .build();
                    ppScale.startSearchBluetoothScaleWithMacAddressList();
                } else {
                    List<DeviceModel> deviceList =DBManager.manager().getDeviceList();
                    List<String> addressList = new ArrayList<>();
                    for (DeviceModel deviceModel : deviceList) {
                        addressList.add(deviceModel.getDeviceMac());
                    }
                    ppScale = new PPScale. Builder(this)
                            .setProtocalFilterImpl(getProtocalFilter())
                            .setBleOptions(getBleOptions())
                            .setDeviceList(addressList)
                            .setBleStateInterface(bleStateInterface)
                             .setUserModel(userModel) parameter PPUserModel In normal circumstances, setUserModel() is required,
                            .build();
                    ppScale.startSearchBluetoothScaleWithMacAddressList();
                }

Note: If automatic cycle scanning is required, it needs to be called again after lockedData()

     ppScale.startSearchBluetoothScaleWithMacAddressList()

#### 1.2 Body Fat Scale

##### 1.2.1 Process data and lock data

     //Implement the interface according to the requirements
     //Monitor process data setPPProcessDateInterface()
     //Monitor lock data setPPLockDataInterface()
     //Monitor historical data setPPHistoryDataInterface()
   
      ProtocolFilterImpl protocolFilter = new ProtocolFilterImpl();
      protocolFilter.setPPProcessDateInterface(new PPProcessDateInterface() {
    
         // process data
         @Override
         public void monitorProcessData(PPBodyBaseModel bodyBaseModel, PPDeviceModel deviceModel) {
             Logger.d("bodyBaseModel scaleName " + bodyBaseModel);
             String weightStr = PPUtil.getWeight(bodyBaseModel.getUnit(), bodyBaseModel.getPpWeightKg(), deviceModel.deviceAccuracyType.getType());
             weightTextView.setText(weightStr);
         }
     });
     protocolFilter.setPPLockDataInterface(new PPLockDataInterface() {

         // lock data
         @Override
         public void monitorLockData(PPBodyFatModel bodyFatModel, PPDeviceModel deviceModel) {
             onDataLock(bodyFatModel, deviceModel);
         }

         @Override
         public void monitorOverWeight() {
             Logger.e("overweight");
         }
     });

##### 1.2.2 Historical data

You need to use the "Bind Device" function to bind the corresponding device first, and then read the "Historical Data". For details, refer to: ReadHistoryListActivity.java
The entrance is in device management, select a device, and then there will be a "read historical data" function, please make sure your scale supports historical data before use

###### 1.2.2.1 Read historical data

      //To read historical data directly, you need to pass in the scale to be read
     private void bindingDevice() {
         List<DeviceModel> deviceList = DBManager. manager(). getDeviceList();
         if (deviceList != null && !deviceList.isEmpty()) {
             List<String> addressList = new ArrayList<>();
             for (DeviceModel deviceModel : deviceList) {
                 addressList.add(deviceModel.getDeviceMac());
             }
             ppScale = new PPScale. Builder(this)
                     .setProtocalFilterImpl(getProtocalFilter())
                     .setBleOptions(getBleOptions())
                     .setDeviceList(addressList)//Note: This is a mandatory item
                     .setUserModel(userModel)
                     .setBleStateInterface(bleStateInterface)
                     .build();
             tv_starts.setText("Start reading offline data");
             ppScale.fetchHistoryData();//Read historical data
         } else {
             tv_starts.setText("Please bind the device first");
         }
     }

###### 1.2.2.2 Historical data callback

      final ProtocolFilterImpl protocolFilter = new ProtocolFilterImpl();
      protocolFilter.setPPHistoryDataInterface(new PPHistoryDataInterface() {
          @Override
          public void monitorHistoryData(PPBodyFatModel bodyFatModel, String dateTime) {
              if (bodyFatModel != null) {
                  Logger.d("ppScale_ " + " dateTime = " + dateTime + " bodyBaseModel weight kg = " + bodyFatModel.getPpWeightKg());
              }
              if (bodyFatModel != null) {
                  Logger.d("ppScale_ bodyFatModel = " + bodyFatModel.toString());

                  String weightStr = PPUtil.getWeight(bodyFatModel.getUnit(), bodyFatModel.getPpWeightKg(), bodyFatModel.getDeviceModel().deviceAccuracyType.getType());

                  DeviceModel bodyModel = new DeviceModel(bodyFatModel. getImpedance() + "", weightStr, -1);

                  deviceModels.add(bodyModel);
                  adapter. notifyDataSetChanged();
              }
          }

          @Override
          public void monitorHistoryEnd(PPDeviceModel deviceModel) {
              if (deviceModels == null || deviceModels. isEmpty()) {
                  tv_starts.setText("No offline data");
              } else {
                  tv_starts.setText("End of reading historical data");
              }
              //End of historical data, delete historical data
         // deleteHistoryData();
          }

          @Override
          public void monitorHistoryFail() {

          }
      });

###### 1.2.2.3 Delete historical data

      //delete history
      if (ppScale != null) {
          ppScale. deleteHistoryData();
      }

#### 1.3 Food Scale

##### 1.3.1 Receive and parse data

     Sample code: FoodSclaeDeviceActivity.java
     final ProtocolFilterImpl protocolFilter = new ProtocolFilterImpl();
     protocolFilter.setFoodScaleDataProtocoInterface(new FoodScaleDataProtocoInterface() {
         @Override
         public void processData(LFFoodScaleGeneral foodScaleGeneral, PPDeviceModel deviceModel) {
             textView.setText("process data");
                extracted(foodScaleGeneral, deviceModel);
                }

        @Override
        public void lockedData(LFFoodScaleGeneral foodScaleGeneral, PPDeviceModel deviceModel) {
            textView.setText("锁定数据");
            extracted(foodScaleGeneral, deviceModel);
        }

        @Override
        public void historyData(LFFoodScaleGeneral foodScaleGeneral, PPDeviceModel deviceModel, String yyyyMMdd, boolean isEnd) {
            if (foodScaleGeneral != null && !isEnd) {
                String valueStr = getValue(foodScaleGeneral, deviceModel);
                historyDataText.append(valueStr + "\n");
            } else {
                historyDataText.append("history end");
            }
        }
    });

##### 1.3.2 Example of unit conversion:

     @NonNull
     private String getValue(final LFFoodScaleGeneral foodScaleGeneral, PPDeviceModel deviceModel) {
         String valueStr = "";

         float value = (float) foodScaleGeneral. getLfWeightKg();
         if (foodScaleGeneral. getThanZero() == 0) {
             value *= -1;
         }

         PPUnitType type = foodScaleGeneral. getUnit();

         if (deviceModel.deviceAccuracyType == PPScaleDefine.PPDeviceAccuracyType.PPDeviceAccuracyTypePoint01G) {
             //String num = String. valueOf(value);
             EnergyUnit unit = Energy.toG(value, type);
             String num = unit. format01();
             String unitText = unit. unitText(FoodSclaeDeviceActivity. this);
             valueStr = num + unitText;
         } else {
             EnergyUnit unit = Energy.toG(value, type);

             if (unit instanceof EnergyUnitLbOz) {
                 String split = ":";
                 String[] values = unit. format(). split(split);
                 String[] units = unit.unitText(FoodSclaeDeviceActivity.this).split(split);
                 valueStr = values[0] + split + values[1] + units[0] + split + units[1];
             } else {
                 String num = unit. format();
                 String unitText = unit. unitText(FoodSclaeDeviceActivity. this);
                 valueStr = num + unitText;
             }
         }
         return valueStr;
     }

##### 1.3.3 Unit enumeration class PPUnitType

         Unit_KG(0),//KG
         Unit_LB(1), //LB
         PPUnitST(2), //ST
         PPUnitJin(3),//jin
         PPUnitG(4), //g
         PPUnitLBOZ(5), //lb:oz
         PPUnitOZ(6), //oz
         PPUnitMLWater(7), //ml(water)
         PPUnitMLMilk(8);//milk

##### 1.3.4 Positive and negative values:

        In the field of PPBodyFatModel:
        int thanZero; //positive and negative 0 means negative value 1 positive value

##### 1.3.5 Switch unit call:

         PPScale. changeKitchenScaleUnit(PPUnitType unitType)

##### 1.3.6 Reset the food scale to zero:

         PPScale.toZeroKitchenScale()

##### 1.3.7 When the food scale is not needed to be connected, it needs to be disconnected manually

        PPScale.disConnect()

##### 1.3.8 Unit precision

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

##### 1.4 The difference between food scale and body fat scale

###### 1.4.1 The interface for receiving data is different

The listener food scale that receives data in ProtocolFilterImpl is FoodScaleDataProtocoInterface, and the body fat scale is composed of PPProcessDateInterface (process data) and PPLockDataInterface (lock data).

###### 1.4.2 The weight unit of the received data is different

The unit corresponding to the weight value of the body fat scale is KG, the unit corresponding to the weight value of the food scale is g, and the unit corresponding to the weight value is fixed. (The unit here is not synchronized with the unit given by PPScale, and the unit given by PPScale is the unit of the current scale end)

##### 1.4.3 Identification of Food Scales and Body Fat Scales

      /**
       * Equipment type
       */
      enum class PPDeviceType {
          PPDeviceTypeUnknown(0), //unknown
          PPDeviceTypeCF(1), //body fat scale
          PPDeviceTypeCE(2), //weight scale
          PPDeviceTypeCB(3),//baby scale
          PPDeviceTypeCA(4), //kitchen scale
          PPDeviceTypeCC(5); //wifi scale

#### 1.5 Scan device list

Search nearby supported devices ScanDeviceListActivity.java

//Get the surrounding bluetooth scale devices
1. ppScale.monitorSurroundDevice();
// You can dynamically set the scan time in milliseconds
2. ppScale.monitorSurroundDevice(300000);
//Connect to the selected device
if (ppScale != null) {
ppScale.connectWithMacAddressList(models);
}

#### 1.6 [Single foot with eyes closed](#Single foot with eyes closed)

### IV .WiFi Function

#### 1.1 Precautions

The default Server domain name address is: https://api.lefuenergy.com

1. Make sure the server is normal and the router can connect to the server normally
2. Make sure the WiFi environment is 2.4G or 2.4/5G mixed mode, does not support single 5G mode
3. Make sure the account password is correct
4. Make sure that the server address used by the scale end corresponds to the server address used by the app

#### 1.2 The basic process of WiFi distribution network

Bluetooth distribution network - this function is used for Bluetooth WiFi scales, used when configuring the network for the scales

1. First make sure that the Bluetooth WiFi scale has been bound
2. The user enters the Wifi account and password
3. Initiate the connection device,
4. After the connection is successful, send the account number and password to the scale in the writable callback (PPBleWorkStateWritable)

   ppScale.configWifi(ssid, password)

5. In the monitor of PPConfigWifiInterface, the monitorConfigState method returns the SN code. At this time, the WiFi icon on the scale will flash first (connecting to the router), and then constant (connecting to the router successfully and obtaining the SN),
6. Send the sn to the server to verify whether the scale has been registered
7. If the server returns success, the network distribution is successful, otherwise the network distribution fails

#### 1.3 Data list

Data List - It is the offline data of the scale obtained from the server and stored on the server, not the historical data stored on the scale

#### 1.4 Device configuration

On the device management page, if the WiFi scale is bound, the setting entry will be displayed, click the setting entry to enter the device configuration page,
On the device configuration page, you can view the SN, SSID, and PASSWORD of the current device, modify the server DNS address of the scale, and clear the SSID of the current scale.
The corresponding code is under DeveloperActivity.class.

### V. Entity class object and specific parameter description

#### 1.1 Data object PPBodyFatModel parameter description

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

     /**************** Four-electrode algorithm *****************************/
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
     var ppBodyfatKgList: List<Float>? = null// Muscle rate (%)
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

     // obesity class
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

Note: To get the object when using it, please call the corresponding get method to get the corresponding value

##### 1.1.1 Error Type PPBodyfatErrorType

     PP_ERROR_TYPE_NONE(0), //no error
     PP_ERROR_TYPE_AGE(1), //The age parameter is incorrect, it needs to be between 6 and 99 years old (parameters other than BMI/idealWeightKg are not calculated)
     PP_ERROR_TYPE_HEIGHT(2), //height parameter is wrong, it needs to be 90 ~ 220cm (not counting all parameters)
     PP_ERROR_TYPE_WEIGHT(3), //The weight is wrong 10 ~ 200kg
     PP_ERROR_TYPE_SEX(4), //Sex is wrong 0 ~ 1
     PP_ERROR_TYPE_PEOPLE_TYPE(5), //Height parameter is wrong, it needs to be 90 ~ 220cm (not counting all parameters)
     PP_ERROR_TYPE_IMPEDANCE_TWO_LEGS(6), //The impedance is wrong 200~1200
     PP_ERROR_TYPE_IMPEDANCE_TWO_ARMS(7), //The impedance is wrong 200~1200
     PP_ERROR_TYPE_IMPEDANCE_LEFT_BODY(8), //The impedance is wrong 200~1200
     PP_ERROR_TYPE_IMPEDANCE_RIGHT_ARM(9), //The impedance is wrong 200~1200
     PP_ERROR_TYPE_IMPEDANCE_LEFT_ARM(10), //The impedance is wrong 200~1200
     PP_ERROR_TYPE_IMPEDANCE_LEFT_LEG(11), //The impedance is wrong 200~1200
     PP_ERROR_TYPE_IMPEDANCE_RIGHT_LEG(12), //The impedance is wrong 200~1200
     PP_ERROR_TYPE_IMPEDANCE_TRUNK(13); //The impedance is wrong 10~100

##### 1.1.2 Health Assessment PPBodyEnum.PPBodyHealthAssessment

     PPBodyAssessment1(0), //!< health risks
     PPBodyAssessment2(1), //!< sub-health
     PPBodyAssessment3(2), //!< General
     PPBodyAssessment4(3), //!< good
     PPBodyAssessment5(4); //!< very good

##### 1.1.3 Fat grade PPBodyEnum.PPBodyFatGrade

     PPBodyGradeFatThin(0), //!< thin
     PPBodyGradeFatStandard(1), //!< standard
     PPBodyGradeFatOverweight(2), //!< overweight
     PPBodyGradeFatOne(3), //!< obesity level 1
     PPBodyGradeFatTwo(4), //!< obesity level 2
     PPBodyGradeFatThree(5); //!< Obesity level 3

##### 1.1.4 Body type PPBodyDetailType

     LF_BODY_TYPE_THIN(0),//thin type
     LF_BODY_TYPE_THIN_MUSCLE(1), // lean muscle type
     LF_BODY_TYPE_MUSCULAR(2),//Muscular type
     LF_BODY_TYPE_LACK_EXERCISE(3),//lack of exercise
     LF_BODY_TYPE_STANDARD(4),//Standard type
     LF_BODY_TYPE_STANDARD_MUSCLE(5),//standard muscle type
     LF_BODY_TYPE_OBESE_FAT(6),//Swollen fat type
     LF_BODY_TYPE_FAT_MUSCLE(7),//Fat muscle type
     LF_BODY_TYPE_MUSCLE_FAT(8);//Muscular fat

#### 1.2 Device object PPDeviceModel parameter description

     String deviceMac;//device mac
     String deviceName;//Device Bluetooth name
     /**
      * Equipment type
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
      * Equipment type
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
      * Calculation
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
      * Function type, multi-functional superposition
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

##### 1.2.1 PPScaleDefine.PPDeviceProtocolType protocol type, specific instructions

         //unknown
         PPDeviceProtocolTypeUnknown(0),
         //Use V2.0 Bluetooth protocol
         PPDeviceProtocolTypeV2(1),
         //Use V3.0 Bluetooth protocol
         PPDeviceProtocolTypeV3(2),
         //four-electrode, eight-electrode protocol
         PPDeviceProtocolTypeTorre(3);

##### 1.2.2 PPScaleDefine.PPDeviceType device type specification

     PPDeviceTypeUnknow(0), //unknown
     PPDeviceTypeCF(1), //body fat scale
     PPDeviceTypeCE(2), //weight scale
     PPDeviceTypeCB(3), //baby scale
     PPDeviceTypeCA(4), //kitchen scale
     PPDeviceTypeCC(5); //Bluetooth wifi scale

##### 1.2.3 PPScaleDefine.PPDeviceAccuracyType Specific description of the accuracy type of weight

     PPDeviceAccuracyTypeUnknow(0), //unknown precision
     PPDeviceAccuracyTypePoint01(1), //KG accuracy 0.1
     PPDeviceAccuracyTypePoint005(2), //KG accuracy 0.05
     PPDeviceAccuracyTypePointG(3), // 1G accuracy
     PPDeviceAccuracyTypePoint01G(4); // 0.1G accuracy

##### 1.2.4 PPScaleDefine.DeviceCalcuteType Body fat calculation type specific description

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

##### 1.2.5 PPScaleDefine.PPDevicePowerType Power supply mode specification

     //unknown
     PPDevicePowerTypeUnknown(0),
     //Battery powered
     PPDevicePowerTypeBattery(1),
     //solar powered
     PPDevicePowerTypeSolar(2),
     //Charging
     PPDevicePowerTypeCharge(3);

##### 1.2.6 PPScaleDefine.PPDeviceFuncType Function type, can be multi-functional superposition, specific instructions

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

##### 1.2.7 PPScaleDefine.PPDeviceUnitType Supported units, specific instructions (temporarily not enabled)

     PPDeviceUnitTypeKG(0x01), //kg
     PPDeviceUnitTypeLB(0x02), //lb
     PPDeviceUnitTypeST(0x04), //st
     PPDeviceUnitTypeJin(0x08), //Jin
     PPDeviceUnitTypeSTLB(0x10);//st:lb

### VI . Bluetooth status monitoring callback and system Bluetooth status callback

Contains two callback methods, one is Bluetooth status monitoring, and the other is system Bluetooth callback

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

### VIII . Bluetooth operation related

#### 1.1 Reserved Bluetooth operation object

     BluetoothClient client = ppScale. getBleClient();

#### 1.2 Stop scanning

     ppScale. stopSearch();

#### 1.3 Disconnect the device

      ppScale.disConnect();

Finally you need to call the stopSearch method before leaving the page. For specific implementation, please refer to the codes in BindingDeviceActivity and ScaleWeightActivity in Demo.

### IX. [Version Update Instructions](doc/version_update.md)

### X. Third-party libraries used

#### 1. The body fat calculation library provided by the chip solution provider

#### 2. [bluetoothkit1.4.0 Bluetooth library](https://github.com/dingjikerbo/Android-BluetoothKit)

### XI. [FAQ](doc/common_problem.md)

Contact Developer: Email: yanfabu-5@lefu.cc