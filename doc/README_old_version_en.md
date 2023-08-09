[English Docs](README-en.md)  | [中文文档](../README.md)

# LF Bluetooth Scale/WiFi Scale SDK

ppscale is the bluetooth connection logic and data parsing logic. When the developer integrates, please use the integration method of the library downloaded from maven. It is recommended that developers review the README.md document to complete the integration.

### Ⅰ. Integration

##### sdk introduction method

Add it to the build.gradle under the module that needs to be imported into the sdk (for the latest version, please check the libs under the module of ppscalelib)

     dependencies {
        //aar import
         api fileTree(include: ['*.jar', '*.aar'], dir: 'libs')
     }

### Ⅱ . Instructions for use

### 1.1 Bluetooth permission related

#### 1.1.1 Operating Environment

Due to the need for Bluetooth connection, Demo needs to run on a real machine, Android phone 6.0 and above or HarmonyOS 2.0 and above

#### 1.1.2 Bluetooth permission related conventions

In the process of using the demo, you need to turn on the bluetooth, turn on the GPS switch, and make sure to enable and authorize the necessary permissions

#### 1.1.2.1 On Android 6.0 and above system version,

  1. Positioning permission
  2. Positioning switch
  3. Bluetooth switch
  
#### 1.1.2.2 On Android 12.0 and above, make sure to enable and authorize the necessary permissions before starting the scan

You can view the official Bluetooth permissions document, the document address: [Google开发者网站关于Bluetooth permissions说明](https://developer.android.com/guide/topics/connectivity/bluetooth/permissions).
  
  1. Positioning permission
  2. Positioning switch
  3. Bluetooth switch
  4. Scan and connect to nearby devices
   
   
        targetSdkVersion 31
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
    </manifest>

#### 1.1.3 Conventions related to measuring body data

   If you need information other than weight value, you need to enter height, age, gender and bare feet on the scale.

### 1.2 Home page function description

#### 1.2.1 User information editing

User information editing - enter the user's height, age, gender and weight unit, if the scale supports pregnant mode and athlete mode can also be turned on

What can be entered:

    The value range of height: 30-220 cm;
    The value range of age: 10-99 years old;
    The weight unit is 0 for kilograms, 1 for kilograms, and 2 for pounds;
    Gender 1 for male, 0 for female;
    User group value range 0-9 (specific scales require this value)
    Pregnant woman mode 1 on 0 off (requires scale support)
    Athlete mode 1 on 0 off (requires scale support)

PPUserModel parameter description:

    userHeight, age, sex must be true
    userHeight range is 100-220cm
    age range is 10-99
    sex gender 0 for female 1 for male
    isAthleteMode;//Athlete mode false is normal true open (requires scale support)
    isPregnantMode;//Pregnant woman mode false is normal true open (requires scale support)

#### 1.2.2 Bind the device

Bind Device - After this controller is instantiated it will start scanning for nearby peripherals and make a record of your peripherals.
        - After successfully binding on the "Bind Device" page, it will automatically jump to the device management list page
        - Both body fat scale and food scale are bound here

#### 1.2.3 Body Fat Scale Weighing

Body Fat Scale Weighing - This controller will also start scanning for nearby peripherals after being instantiated, filtering to connect to bound devices. Therefore, you can only go to the weighing scale after being bound, otherwise the data will not be received.
        - After receiving the locked data on the "Body Fat Scale" page, it will automatically jump to the data details page

#### 1.2.3 Food Scale Weighing

Food Scale Weighing - Example of scanning and connecting to already bound food scales, receiving and parsing weight data

#### 1.2.4 Device Management

Device Management - This controller will display the peripherals you bind on the "Bind Devices" page in a list. You can delete the bound device by long pressing.
    -It contains both body fat scale and food scale

#### 1.2.5 Simulate body fat calculation

Simulated body fat calculation - According to the weight and impedance parsed by the Bluetooth protocol, plus the height, age, and gender of the user data, the body fat rate and other body fat parameter information are calculated.
            
#### 1.2.6 Scan the list of nearby devices

Scan list of nearby devices - a list of nearby body fat scales, you can set the scan time and filter signal strength, you can select a device to connect and weigh
    - This function is only for body fat scales


### Ⅲ .PPScale use in Bluetooth devices

#### 1.1 PPScale initialization, and basic conventions

//The difference between the binding device and the scanning device is that searchType 0 binds the device 1 scans the specified device
//setProtocalFilterImpl() receives data callback interface, process data, lock data, historical data,
//The parameter of the setDeviceList() function is not empty, you need to pass null (or not call) for binding, and to scan the specified device, please pass the List<DeviceModel> of the specified device. bind
//setBleOptions() Bluetooth parameter configuration
//setBleStateInterface() requires parameter PPBleStateInterface, Bluetooth status monitoring callback and system Bluetooth status callback
//startSearchBluetoothScaleWithMacAddressList() starts scanning for devices
//setUserModel() parameter PPUserModel is normal, setUserModel() is required,
            
       /**
        * sdk entry, instance object
        */
       private void bindingDevice() {
               if (searchType == 0) {
                   ppScale = new PPScale.Builder(this)
                           .setProtocalFilterImpl(getProtocalFilter())
                           .setBleOptions(getBleOptions())
       // .setDeviceList(null)
                           .setBleStateInterface(bleStateInterface)
                           .setUserModel(userModel)
                           .build();
                   ppScale.startSearchBluetoothScaleWithMacAddressList();
               } else {
                   List<DeviceModel> deviceList = DBManager.manager().getDeviceList();
                   List<String> addressList = new ArrayList<>();
                   for (DeviceModel deviceModel : deviceList) {
                       addressList.add(deviceModel.getDeviceMac());
                   }
                   ppScale = new PPScale.Builder(this)
                         .setProtocalFilterImpl(getProtocalFilter())
                         .setBleOptions(getBleOptions())
                         .setDeviceList(addressList)
                         .setBleStateInterface(bleStateInterface)
                          .setUserModel(userModel) parameter PPUserModel Under normal circumstances, setUserModel() is required,
                         .build();
                 ppScale.startSearchBluetoothScaleWithMacAddressList();
             }

Note: If you need automatic loop scanning, you need to re-call ppScale.startSearchBluetoothScaleWithMacAddressList() after lockedData()

#### 1.2 Body Fat Scale

##### 1.2.1 Process data and lock data

      //implement the interface as required
      //Monitor process data setPPProcessDateInterface()
      //Listen to lock data setPPLockDataInterface()
      //Listen to historical data setPPHistoryDataInterface()
     
       ProtocalFilterImpl protocalFilter = new ProtocalFilterImpl();
       protocalFilter.setPPProcessDateInterface(new PPProcessDateInterface() {
      
          // process data
          @Override
          public void monitorProcessData(PPBodyBaseModel bodyBaseModel, PPDeviceModel deviceModel) {
              Logger.d("bodyBaseModel scaleName " + bodyBaseModel);
              String weightStr = PPUtil.getWeight(bodyBaseModel.getUnit(), bodyBaseModel.getPpWeightKg(), deviceModel.deviceAccuracyType.getType());
              weightTextView.setText(weightStr);
          }
      });
      protocalFilter.setPPLockDataInterface(new PPLockDataInterface() {
    
          // lock data
          @Override
          public void monitorLockData(PPBodyFatModel bodyFatModel, PPDeviceModel deviceModel) {
              onDataLock(bodyFatModel, deviceModel);
          }
    
          @Override
          public void monitorOverWeight() {
              Logger.e("over weight ");
          }
      });
  
##### 1.2.2 Historical data

You need to use the "Bind Device" function to bind the corresponding device first, and then read the "Historical Data". For details, please refer to: ReadHistoryListActivity.java
The entrance is in device management, select a device, and then there will be a "read historical data" function, please make sure that your scale supports historical data before use
  
###### 1.2.2.1 Read historical data

       //Directly read historical data, need to pass in the scale to be read
      private void bindingDevice() {
          List<DeviceModel> deviceList = DBManager.manager().getDeviceList();
          if (deviceList != null && !deviceList.isEmpty()) {
              List<String> addressList = new ArrayList<>();
              for (DeviceModel deviceModel : deviceList) {
                  addressList.add(deviceModel.getDeviceMac());
              }
              ppScale = new PPScale.Builder(this)
                      .setProtocalFilterImpl(getProtocalFilter())
                      .setBleOptions(getBleOptions())
                      .setDeviceList(addressList)//Note: here is the required item
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
      
     
    final ProtocalFilterImpl protocalFilter = new ProtocalFilterImpl();
    protocalFilter.setPPHistoryDataInterface(new PPHistoryDataInterface() {
        @Override
        public void monitorHistoryData(PPBodyFatModel bodyFatModel, String dateTime) {
           if (bodyFatModel != null) {
               Logger.d("ppScale_ " + " dateTime = " + dateTime + " bodyBaseModel weight kg = " + bodyFatModel.getPpWeightKg());
           }
           if (bodyFatModel != null) {
               Logger.d("ppScale_ bodyFatModel = " + bodyFatModel.toString());

               String weightStr = PPUtil.getWeight(bodyFatModel.getUnit(), bodyFatModel.getPpWeightKg(), bodyFatModel.getDeviceModel().deviceAccuracyType.getType());

               DeviceModel bodyModel = new DeviceModel(bodyFatModel.getImpedance() + "", weightStr, -1);

               deviceModels.add(bodyModel);
               adapter.notifyDataSetChanged();
           }
        }

       @Override
       public void monitorHistoryEnd(PPDeviceModel deviceModel) {
           if (deviceModels == null || deviceModels.isEmpty()) {
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
      
       // delete history
       if (ppScale != null) {
           ppScale.deleteHistoryData();
       }

#### 1.3 Food Scale

##### 1.3.1 Receiving and parsing data
   Sample code: FoodSclaeDeviceActivity.java
   
    final ProtocalFilterImpl protocalFilter = new ProtocalFilterImpl();
    protocalFilter.setFoodScaleDataProtocoInterface(new FoodScaleDataProtocoInterface() {
      @Override
      public void processData(LFFoodScaleGeneral foodScaleGeneral, PPDeviceModel deviceModel) {
          textView.setText("Process data");
          extracted(foodScaleGeneral, deviceModel);
      }

      @Override
      public void lockedData(LFFoodScaleGeneral foodScaleGeneral, PPDeviceModel deviceModel) {
          textView.setText("Lock data");
          extracted(foodScaleGeneral,deviceModel);
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
     
 ##### 1.3.2 Unit conversion example:
     
     @NonNull
     private String getValue(final LFFoodScaleGeneral foodScaleGeneral, PPDeviceModel deviceModel) {
         String valueStr = "";
 
         float value = (float) foodScaleGeneral.getLfWeightKg();
         if (foodScaleGeneral.getThanZero() == 0) {
             value *= -1;
         }
 
         PPUnitType type = foodScaleGeneral.getUnit();
 
         if (deviceModel.deviceAccuracyType == PPScaleDefine.PPDeviceAccuracyType.PPDeviceAccuracyTypePoint01G) {
             //String num = String.valueOf(value);
             EnergyUnit unit = Energy.toG(value, type);
             String num = unit.format01();
             String unitText = unit.unitText(FoodSclaeDeviceActivity.this);
             valueStr = num + unitText;
         } else {
             EnergyUnit unit = Energy.toG(value, type);
 
             if (unit instanceof EnergyUnitLbOz) {
                 String split = ":";
                 String[] values ​​= unit.format().split(split);
                 String[] units = unit.unitText(FoodSclaeDeviceActivity.this).split(split);
                 valueStr = values[0] + split + values[1] + units[0] + split + units[1];
             } else {
                 String num = unit.format();
                 String unitText = unit.unitText(FoodSclaeDeviceActivity.this);
                 valueStr = num + unitText;
             }
         }
         return valueStr;
     }
     
 
 ##### 1.3.3 Unit enumeration class PPUnitType
 
         Unit_KG(0), //KG
         Unit_LB(1), //LB
         PPUnitST(2), //ST
         PPUnitJin(3),//jin
         PPUnitG(4), //g
         PPUnitLBOZ(5), //lb:oz
         PPUnitOZ(6), //oz
         PPUnitMLWater(7), //ml(water)
         PPUnitMLMilk(8);//milk
 
 ##### 1.3.4 Positive and negative values:
 
    In the fields of PPBodyFatModel:
    int thanZero; // positive and negative 0 means negative value 1 positive value
 
 ##### 1.3.5 Switch unit call:
 
    PPScale.changeKitchenScaleUnit(PPUnitType unitType)
 
 ##### 1.3.6 Zero food scale:
 
    PPScale.toZeroKitchenScale()
 
 ##### 1.3.7 When using the food scale, it needs to be disconnected manually when the connection is not required
 
    PPScale.disConnect()
        
 ##### 1.3.8 Unit Precision
     enum class PPDeviceAccuracyType {
         PPDeviceAccuracyTypeUnknow(0),//Unknown precision
         //KG precision 0.1
         PPDeviceAccuracyTypePoint01(1),
         //KG precision 0.05
         PPDeviceAccuracyTypePoint005(2),
         // 1G precision
         PPDeviceAccuracyTypePointG(3),
         // 0.1G precision
         PPDeviceAccuracyTypePoint01G(4);
     }
 
 ##### 1.4 The difference between food scales and body fat scales
 
 ###### 1.4.1 The interface for receiving data is different
 
 The listener food scale that receives data in ProtocalFilterImpl is FoodScaleDataProtocoInterface, and the body fat scale is composed of PPProcessDateInterface (process data) and PPLockDataInterface (lock data).
 
 ###### 1.4.2 The weight unit of the received data is different
 
 The unit corresponding to the weight value of the body fat scale is KG, the unit corresponding to the weight value of the food scale is g, and the unit corresponding to the weight value is fixed. (The unit here is not synchronized with the unit given by PPScale, and the unit given by PPScale is the unit of the current scale)
 
 ##### 1.4.3 Identification of food scales and body fat scales
      
      /**
       * Equipment type
       */
      enum class PPDeviceType {
          PPDeviceTypeUnknow(0), //unknown
          PPDeviceTypeCF(1), //body fat scale
          PPDeviceTypeCE(2), //Weight scale
          PPDeviceTypeCB(3),//Baby scale
          PPDeviceTypeCA(4), //Kitchen scale
          PPDeviceTypeCC(5); //wifi scale
 
 #### 1.5 Scanning Device List
 
 Search for nearby supported devices ScanDeviceListActivity.java
 
     //Get the surrounding bluetooth scale devices
     1. ppScale.monitorSurroundDevice();
     // You can dynamically set the scan time in milliseconds
     2. ppScale.monitorSurroundDevice(300000);
     //connect to the selected device
     if (ppScale != null) {
        ppScale.connectWithMacAddressList(models);
     }
 
 #### 1.6 [closed eye one foot](#closed eye one foot mode)
 
 ### IV .WiFi Scale
 
 #### 1.1 Notes
 
 The default server domain name address is: https://api.lefuenergy.com
 
 1. Ensure that the server is normal and the router can connect to the server normally
 2. Make sure the WiFi environment is 2.4G or 2.4/5G mixed mode, single 5G mode is not supported
 3. Make sure the account password is correct
 4. Make sure that the server address used by the scale corresponds to the server address used by the App
 
 #### 1.2 The basic process of WiFi distribution network
 Bluetooth configuration network - This function is used for Bluetooth WiFi scales, used when configuring the network for the scale
 
 1. First make sure that the Bluetooth WiFi scale has been bound
 2. The user enters the Wifi account and password
 3. Initiate the connection to the device,
 4. After the connection is successful, in the writable callback (PPBleWorkStateWritable), send the account number and password to the scale
 
     ppScale.configWifi(ssid, password)
 
 5. In the monitor of PPConfigWifiInterface, the monitorConfigState method returns the sn code. At this time, the WiFi icon on the scale will first flash (connecting to the router), and then remain constant (connecting to the router successfully and obtaining the sn),
 6. Pass the sn to the Server to verify whether the scale has been registered
 7. If the server returns successfully, the network configuration is successful, otherwise the network configuration fails
 
#### 1.3 Data List
 
 Data list - is the offline data stored on the server side of the scale obtained from the server side, not the historical data stored on the scale side
 
#### 1.4 Device Configuration
 
 On the device management page, if the WiFi scale is bound, the setting entry will be displayed. Click the setting entry to enter the device configuration page.
 On the device configuration page, you can view the SN, SSID, PASSWORD of the current device, modify the DNS address of the scale server, clear the SSID of the current scale,
 The corresponding code is under DeveloperActivity.class.
 
 
### V . Entity class object and specific parameter description
 
#### 1.1 Data object instantiation method PPBodyFatModel
 
 If you parse the Bluetooth protocol data yourself, you need to instantiate this class to obtain other corresponding body data.
 
##### 1.1.1 Data object PPBodyFatModel initialization
 
     /**
      * Weighing
      *
      * @param ppWeightKg weight required
      * @param scaleType device type {@link com.peng.ppscale.business.device.PPDeviceType#PPDeviceTypeBodyFat} not required
      * @param userModel user information Fat measurement data must be passed{@link PPUserModel}
      * @param scaleName Bluetooth scale name optional
      */
     public PPBodyFatModel(double ppWeightKg, String scaleType, PPUserModel userModel, String scaleName) {
         super(ppWeightKg, scaleType, userModel, scaleName);
     }/**
           * Fat measurement
           *
           * @param ppWeightKg weight required
           * @param impedance Encrypted impedance, fat measurement data must be transmitted
           * @param scaleType device type {@link com.peng.ppscale.business.device.PPDeviceType#PPDeviceTypeBodyFat} not required
           * @param userModel user information Fat measurement data must be passed{@link PPUserModel}
           * @param scaleName Bluetooth scale name optional
           */
          public PPBodyFatModel(double ppWeightKg, int impedance, String scaleType, PPUserModel userModel, String scaleName) {
              super(ppWeightKg, impedance, scaleType, userModel, scaleName, PPUnitType.Unit_KG);
          }
      
##### 1.1.2 Data object PPBodyFatModel parameter description
      
          protected int impedance; //impedance value (encrypted)
          // protected float ppZTwoLegs; // Foot-to-foot impedance (Ω), range 200.0 ~ 1200.0
          protected double ppWeightKg; //weight
          protected int ppHeartRate; //heart rate
          protected int scaleType; //scale type
          protected boolean isHeartRateEnd = true; //heart rate end character
          protected String scaleName; //Call the name
          protected int thanZero; // positive and negative 0 means negative value 1 positive value
          protected PPUnitType unit; //Weight unit default kg
          protected PPUserModel userModel;
          protected PPUserSex ppSex; //gender
          protected double ppHeightCm; //Height (cm), needs to be between 90 ~ 220cm
          protected int ppAge; //Age (years), must be between 6 and 99 years old
          protected double ppProteinPercentage; //Protein, resolution 0.1, range 2.0% ~ 30.0%
          protected int ppBodyAge; //Body age, 6~99 years old
          protected double ppIdealWeightKg; //ideal weight (kg)
          protected double ppBMI; //BMI body mass index, resolution 0.1, range 10.0 ~ 90.0
          protected int ppBMR; //Basal Metabolic Rate, resolution 1, range 500 ~ 10000
          protected int ppVFAL; //Visceral fat area leverl visceral fat, resolution 1, range 1 ~ 60
          protected double ppBoneKg; //Bone mass (kg), resolution 0.1, range 0.5 ~ 8.0
          protected double ppBodyfatPercentage; //fat percentage (%), resolution 0.1, range 5.0% ~ 75.0%
          protected double ppWaterPercentage; //Moisture rate (%), resolution 0.1, range 35.0% ~ 75.0%
          protected double ppMuscleKg; //Muscle mass (kg), resolution 0.1, range 10.0 ~ 120.0
          protected int ppBodyType; // body type
          protected int ppBodyScore; //Body score 50 ~ 100 points
          protected double ppMusclePercentage; //Muscle rate (%), resolution 0.1, range 5%~90%
          protected double ppBodyfatKg; //fat mass (kg)
          protected double ppBodystandard; //standard body weight (kg)
          protected double ppLoseFatWeightKg; // fat free weight (kg)
          protected double ppControlWeightKg; //Weight control (kg)
          protected double ppFatControlKg; // fat control amount (kg)
          protected double ppBonePercentage; //skeletal muscle rate (%)
          protected double ppBodyMuscleControlKg; //Muscle control amount (kg)
          protected double ppVFPercentage; //Subcutaneous fat (%)
          protected PPBodyEnum.PPBodyGrade ppBodyHealth; //Health Assessment
          protected PPBodyEnum.PPBodyFatGrade ppFatGrade; // fat grade
          protected PPBodyEnum.PPBodyHealthAssessment ppBodyHealthGrade; //health grade
          protected PPBodyEnum.PPBodyfatErrorType ppBodyfatErrorType; //Error type
      
Note: To get the object when using it, please call the corresponding get method to get the corresponding value
      
##### 1.1.3 Error Type PPBodyEnum.PPBodyfatErrorType
      
    PPBodyfatErrorTypeNone(0), //!< no error (all parameters can be read)
    PPBodyfatErrorTypeImpedance(-1), //!< Impedance is wrong, when impedance is wrong, parameters other than BMI/idealWeightKg are not calculated (write 0)
    PPBodyfatErrorTypeAge(-1), //!< The age parameter is incorrect, it needs to be between 6 and 99 years old (parameters other than BMI/idealWeightKg are not calculated)
    PPBodyfatErrorTypeWeight(-2), //!< The weight parameter is wrong, it needs to be between 10 ~ 200kg (if it is wrong, all parameters will not be calculated)
    PPBodyfatErrorTypeHeight(-3); //!< The height parameter is incorrect, it needs to be between 90 ~ 220cm (all parameters are not calculated)

##### 1.1.4 Health Assessment PPBodyEnum.PPBodyfatErrorType

    PPBodyGradeThin(0), //!< Thin type
    PPBodyGradeLThinMuscle(1), //!< Standard
    PPBodyGradeMuscular(2), //!< super heavy
    PPBodyGradeLackofexercise(3); //!< fat type

##### 1.1.5 Fat Level PPBodyEnum.PPBodyfatErrorType

    PPBodyGradeFatOne(0), //!< fat grade 1
    PPBodyGradeLFatTwo(1), //!< obesity grade 2
    PPBodyGradeFatThree(2), //!< obese level 3
    PPBodyGradeFatFour(-1); //!< parameter error
                         
 ##### 1.1.6 Health Level PPBodyEnum.PPBodyfatErrorType
 
       PPBodyAssessment1(0), //!< There are hidden dangers to health
       PPBodyAssessment2(1), //!< Sub-health
       PPBodyAssessment3(2), //!< General
       PPBodyAssessment4(3), //!< good
       PPBodyAssessment5(4), //!< very good
       PPBodyAssessmentError(-1); //!< parameter error
 
 ##### 1.1.7 Body Type PPBodyFatModel.ppBodyType
 
      0 thin
      1 lean muscle type
      2 The muscular type
      3 lack of exercise
      4 Standard
      5 Standard Muscle Type
      6 Puffy and obese
      7 fat and muscular
      8 Muscular fat
 
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
      * battery
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
      * @see PPScaleDefine.PPDeviceProtocolType
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
      * Power mode
      *
      * @see PPScaleDefine.PPDevicePowerType
      */
     public PPScaleDefine.PPDevicePowerType devicePowerType;
     /**
      * Device connection type, for the state where direct connection is required
      *
      * @see PPScaleDefine.PPDeviceConnectType
      */
     public PPScaleDefine.PPDeviceConnectType deviceConnectType;
     /**
      * Function type, can be superimposed with multiple functions
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
      * Is it possible to connect
      */
     public boolean deviceConnectAbled;
 
 ##### 1.2.1 PPScaleDefine.PPDeviceProtocolType Protocol type, specific description
 
     PPDeviceProtocolTypeUnknow(0), //Unknown protocol
     PPDeviceProtocolTypeV2(1) //Use V2.0 Bluetooth protocol
     PPDeviceProtocolTypeV3(2), //Use V3.0 Bluetooth protocol
     PPDeviceProtocolTypeTorre(3) //Four-electrode, eight-electrode protocol
 
 ##### 1.2.2 PPScaleDefine.PPDeviceType Device type specific description
 
     PPDeviceTypeUnknow(0), //unknown
     PPDeviceTypeCF(1), //body fat scale
     PPDeviceTypeCE(2), //Weight scale
     PPDeviceTypeCB(3), //Baby scale
     PPDeviceTypeCA(4), //Kitchen scale
     PPDeviceTypeCC(5); //Bluetooth wifi scale
 
 ##### 1.2.3 PPScaleDefine.PPDeviceAccuracyType Weight's precision type specification
 
     PPDeviceAccuracyTypeUnknow(0), //Unknown precision
     PPDeviceAccuracyTypePoint01(1), //KG accuracy 0.1
     PPDeviceAccuracyTypePoint005(2), //KG accuracy 0.05
     PPDeviceAccuracyTypePointG(3), // 1G precision
     PPDeviceAccuracyTypePoint01G(4); // 0.1G precision
 
 ##### 1.2.4 PPScaleDefine.DeviceCalcuteType Specific description of body fat calculation type
 
     PPDeviceCalcuteTypeUnknow(0), //unknown
     PPDeviceCalcuteTypeInScale(1), //scale calculation
     PPDeviceCalcuteTypeDirect(2), //DC
     PPDeviceCalcuteTypeAlternate(3), //communication
     PPDeviceCalcuteTypeNeedNot(4) //No need to calculate
 
 ##### 1.2.5 PPScaleDefine.PPDevicePowerType Power supply mode specification
 
     PPDevicePowerTypeUnknow(0), //unknown
     PPDevicePowerTypeBattery(1), // battery powered
     PPDevicePowerTypeSolar(2), //Solar power supply
     PPDevicePowerTypeCharge(3); //Charging model
 
 ##### 1.2.6 PPScaleDefine.PPDeviceFuncType Function type, which can be superimposed with multiple functions, detailed description
 
     PPDeviceFuncTypeWeight(0x01), //weight
     PPDeviceFuncTypeFat(0x02), //Measure body fat
     PPDeviceFuncTypeHeartRate(0x04), //heart rate
     PPDeviceFuncTypeHistory(0x08), //historical data
     PPDeviceFuncTypeSafe(0x10), //safe mode, pregnant woman mode
     PPDeviceFuncTypeBMDJ(0x20); //Close single pin
 
 ##### 1.2.7 PPScaleDefine.PPDeviceUnitType Supported units, specific description (not enabled for now)
 
     PPDeviceUnitTypeKG(0x01), //kg
     PPDeviceUnitTypeLB(0x02), //lb
     PPDeviceUnitTypeST(0x04), //st
     PPDeviceUnitTypeJin(0x08), //jin
     PPDeviceUnitTypeSTLB(0x10);//st:lb
 
 
 ### VI .Bluetooth status monitoring callback and system bluetooth status callback
 
 Contains two callback methods, one is Bluetooth status monitoring, the other is system Bluetooth callback
 
      PPBleStateInterface bleStateInterface = new PPBleStateInterface() {
         //Bluetooth status monitoring
         //deviceModel is null when bluetooth is scanning
         @Override
         public void monitorBluetoothWorkState(PPBleWorkState ppBleWorkState, PPDeviceModel deviceModel) {
             if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnected) {
                 Logger.d("Device is connected");
             } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnecting) {
                 Logger.d("Device connection");
             } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateDisconnected) {
                 Logger.d("Device disconnected");
             } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateStop) {
                 Logger.d("Stop scanning");
             } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateSearching) {
                 Logger.d("Scanning");
             } else if (ppBleWorkState == PPBleWorkState.PPBleWorkSearchTimeOut) {
                 Logger.d("Scan Timeout");
             } else {
                 Logger.e("Bluetooth status is abnormal");
             }
         }
     
         //System bluetooth callback
         @Override
         public void monitorBluetoothSwitchState(PPBleSwitchState ppBleSwitchState) {
                                       if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOff) {
                                           Logger.e("System Bluetooth disconnected");
                                           Toast.makeText(BindingDeviceActivity.this, "System Bluetooth is disconnected", Toast.LENGTH_SHORT).show();
                                       } else if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOn) {
                                           Logger.d("System Bluetooth is on");
                                           Toast.makeText(BindingDeviceActivity.this, "System Bluetooth is on", Toast.LENGTH_SHORT).show();
                                       } else {
                                           Logger.e("System Bluetooth exception");
                                       }
                                   }
                               };
                           
### VII . One-leg mode with eyes closed

###### One-leg mode with eyes closed

Use the instance object of PPScale to call the scan for nearby devices method to search for nearby closed-eye single-leg Bluetooth scales and connect.

### 1.1 Connecting to a closed-eye monopod

````
       ProtocalFilterImpl protocalFilter = new ProtocalFilterImpl();
               protocalFilter.setBmdjConnectInterface(new PPBMDJConnectInterface() {
                   @Override
                   public void monitorBMDJConnectSuccess() {
                       isAutoPush = true;
                       Intent intent = new Intent(BMDJConnectActivity.this, BMDJIntroduceActivity.class);
                       startActivity(intent);
                   }
       
                   @Override
                   public void monitorBMDJConnectFail() {
       
                   }
               });{
       
               }
               BleOptions bleOptions = new BleOptions.Builder()
                       .setFeaturesFlag(BleOptions.ScaleFeatures.FEATURES_BMDJ)
                       .setDeviceType(PPDeviceType.Contants.FAT_AND_BMDJ)
                       .build();
       
               ppScale = new PPScale.Builder(getApplicationContext())
                       .setDeviceList(addressList)
                       .setBleOptions(bleOptions)
                       .setProtocalFilterImpl(protocalFilter)
                       .build();
       
               ppScale.enterBMDJModel();
````

### 1.2 Exit closed-eye single-leg mode and stop scanning to disconnect

````
// Stop scanning for devices that cut off the opening and closing eyes
- ppScale.exitBMDJModel();
````

### 1.3 Monitor the state of closed eyes and one foot

````
(interface)PPBMDJStatesInterface
````

### 1.4 Send the command to make the device enter the closed-eye single-leg mode

````
// The closed-eye single-leg device enters the ready state
- (void)enterBMDJModel()
````

### 1.5 Return the time of standing on one foot with closed eyes in the callback function

````
/// The device exits the closed-eye single-leg state
- (interface)PPBMDJDataInterface;
````

### VIII. Bluetooth operation related

#### 1.1 Reserve the Bluetooth operation object

    BluetoothClient client = ppScale.getBleClient();

#### 1.2 Stop scanning

      ppScale.stopSearch();

#### 1.3 Disconnect the device

       ppScale.disConnect();

Finally you need to call the stopSearch method before leaving the page. For the specific implementation, please refer to the code in BindingDeviceActivity and ScaleWeightActivity in Demo.

### IX. [Version Update Instructions](doc/version_update.md)

### X. Third-party libraries used

##### 1. The body fat calculation library provided by the chip solution provider

##### 2. [bluetoothkit1.4.0 Bluetooth library](https://github.com/dingjikerbo/Android-BluetoothKit)

### XI . [FAQ](doc/common_problem.md)

Contact Developer: Email: yanfabu-5@lefu.cc