[English Docs](README-en.md)  | [中文文档](../README.md)

# LF Bluetooth scale/WiFi scale SDK

## One. Bluetooth WiFi related documents

ppscale is the Bluetooth connection logic and data analysis logic. When the developer integrates,
please use the integration method of the library downloaded from maven. It is recommended that
developers check the README.md document to complete the integration.

The example module is: BleWifiScaleDemo
[Bluetooth WIFI Scale Document Chinese](BleWifiScaleDemo/README_CN.md)
[Bluetooth WIFI Scale example English](BleWifiScaleDemo/README_EN.md)

## Two, Bluetooth body fat scale example

### Ⅰ. Integration method

##### sdk introduction method

Add to the build.gradle under the module that needs to import the SDK (for the latest version,
please check the libs under the module of ppscalelib)

         dependencies {
                    //1.aar introduction
                    implementation files('libs/ppscale-213-20220105-0327015.aar')
                    //2.github maven introduced, you need to refer to build.gradle under Project to introduce maven {url "https://raw.githubusercontent.com/PPScale/ppscale-android-maven/main"}
                    //implementation'com.lefu.ppscale:ppscale:2.1.3-SNAPSHOT'
         }

### Ⅱ. Instructions for use

### 1.1 Bluetooth permissions related

* Due to the need for Bluetooth connection, Demo needs to run on a real machine.

* In Android 6.0 and above system versions, make sure to turn it on before starting the scan

    1. Positioning permissions
    2. Positioning switch
    3. Bluetooth switch

* If you need information other than the weight value, you need to enter your height, age, gender
  and go on the scale barefoot.

* During the use of Demo, you need to turn on Bluetooth and give Demo location permission

### 1.2 General functions

#### 1.2.1 User information editing

User information editing-enter the user's height, age, gender and weight unit, if the scale supports
pregnant women mode and athlete mode can also be turned on

What can be entered:

    The value range of height: 30-220 cm;
    The range of age: 10-99 years old;
    Weight unit 0 stands for kilograms, 1 stands for catties, and 2 stands for pounds;
    Gender 1 represents male, 0 represents female;
    User group value range 0-9 (this value is required for specific scales)
    Maternity mode 1 on 0 off (requires scale support)
    Athlete mode 1 is on, 0 is off (requires scale support)

PPUserModel parameter description:

    userHeight, age, sex must be real
    userHeight range is 100-220cm
    age range is 10-99
    sex 0 for female 1 for male
    isAthleteMode;//Athlete mode false is normal true open (requires scale support)
    isPregnantMode;//Pregnant woman mode false is normal true open (requires scale support)

#### 1.2.2 Binding device

Binding device-After this controller is instantiated, it will scan for nearby peripherals and make a
record of your peripherals.

#### 1.2.3 Weighing on the scale

Weighing on the scale-This controller will also start scanning nearby peripherals after being
instantiated, and connect the bound devices through filtering. Therefore, the weighing can only be
carried out after being bound, otherwise the data cannot be received.

#### 1.2.4 Device Management

Device Management-This controller will display the peripherals you bind on the "Bind Devices" page in a list. You can delete the bound device by long pressing.

#### 1.2.5 Data details

    After receiving the data returned by the peripherals on the "Bind Device" and "Weighing on Scale" pages, it will automatically jump to the data details page

### 1.3 Bluetooth function

#### 1.3.1 Scan device

Search nearby supported devices ScanDeviceListActivity.java

    //Get surrounding Bluetooth scale equipment 
    ppScale.monitorSurroundDevice(); 
    //Connect to selected device
    if (ppScale != null){
        ppScale.connectWithMacAddressList(models);
    }

#### 1.3.2 Read historical data

Need to "bind the device" and then read the "historical data" ReadHistoryListActivity.java //Read
historical data directly, you need to pass in the scale to be read private void bindingDevice() {

        List<DeviceModel> deviceList = DBManager.manager().getDeviceList();

        if (deviceList != null && !deviceList.isEmpty()) {
            List<String> addressList = new ArrayList<>();
            for (DeviceModel deviceModel: deviceList) {
                addressList.add(deviceModel.getDeviceMac());
            }
            ppScale = new PPScale.Builder(this)
                    .setProtocalFilterImpl(getProtocalFilter())
                    .setBleOptions(getBleOptions())
                    .setDeviceList(addressList)//Note: This is a mandatory item
                    .setUserModel(userModel)
                    .setBleStateInterface(bleStateInterface)
                    .build();
            //Get historical data
            tv_starts.setText("Start reading offline data");
            ppScale.fetchHistoryData();
        } else {
            tv_starts.setText("Please bind the device first");
        }

    }

#### 1.3.3 [Close eyes single foot] (#Close eyes single foot mode)

### 1.4 WiFi function

#### 1.4.1 Matters needing attention

The default Server domain name address is: https://api.lefuenergy.com

    1. Make sure that the server is normal and the router can connect to the server normally
    2. Make sure that the WiFi environment is 2.4G or 2.4/5G mixed mode, and single 5G mode is not
       supported
    3. Make sure the account password is correct
    4. Make sure that the Server address used by the scale corresponds to the Server address used by the
       App

#### 1.4.2 The basic process of WiFi distribution network

Bluetooth network configuration-this function is used for Bluetooth WiFi scales, used when
configuring the network for the scale

    1. First make sure that the Bluetooth WiFi scale has been bound
    2. The user enters the Wifi account and password
    3. Initiate a connection to the device,
    4. After the connection is successful, in the writable callback (PPBleWorkStateWritable), send the
       account and password to the scale
    
       ppScale.configWifi(ssid, password)
    
    5. In the monitor of PPConfigWifiInterface, the monitorConfigState method returns the sn code. At
       this time, the WiFi icon on the scale will flash first (connecting to the router), and then
       constant (connecting to the router successfully and obtaining sn),
    6. Pass sn to Server to verify whether the scale has been registered
    7. If the server returns success, the network configuration is successful, otherwise the network
       configuration fails

#### 1.4.3 Data List

Data list-is the offline data of the scale obtained from the server and stored on the server, not
the historical data stored on the scale

### Ⅲ. Use of PPScale in Bluetooth devices

#### 1.1 Bind or scan specified Bluetooth devices

        //The difference between binding device and scanning device is searchType 0 binding device 1 scanning specified device
        //setProtocalFilterImpl() Receive data callback interface, process data, lock data, historical data,
        //The parameter of the setDeviceList() function is not empty, and the binding needs to pass null (or not call). To scan the specified device, please pass the List<DeviceModel> of the specified device. Bind
        //setBleOptions() Bluetooth parameter configuration
        //setBleStateInterface() requires the parameter PPBleStateInterface, Bluetooth status monitoring callback and system Bluetooth status callback
        //startSearchBluetoothScaleWithMacAddressList() start scanning device
        //setUserModel() parameter PPUserModel is normal, setUserModel() is necessary,
            
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
               for (DeviceModel deviceModel: deviceList) {
                   addressList.add(deviceModel.getDeviceMac());

            } ppScale = new PPScale.Builder(this)
            .setProtocalFilterImpl(getProtocalFilter())
            .setBleOptions(getBleOptions())
            .setDeviceList(addressList)
            .setBleStateInterface(bleStateInterface)
            .setUserModel(userModel) parameter PPUserModel is normal, setUserModel() is required, .build();
                ppScale.startSearchBluetoothScaleWithMacAddressList(); }

Note: If you need to scan automatically in a loop, you need to call
ppScale.startSearchBluetoothScaleWithMacAddressList() again after lockedData()

#### 1.3 Data Processing

##### 1.3.1 Process data and lock data

    //Implement the interface according to the needs
    //Monitor process data setPPProcessDateInterface()
    //Monitor lock data setPPLockDataInterface()
    //Monitor historical data setPPHistoryDataInterface()
   
     ProtocalFilterImpl protocalFilter = new ProtocalFilterImpl();
            protocalFilter.setPPProcessDateInterface(new PPProcessDateInterface() {
            //Process data
                @Override
                public void monitorProcessData(PPBodyBaseModel bodyBaseModel) {
                    Logger.d("bodyBaseModel scaleName "+ bodyBaseModel.getScaleName());
                    //weight
                    String weightStr = PPUtil.getWeight(unitType, bodyBaseModel.getPpWeightKg());
                    
                }
            });
            protocalFilter.setPPLockDataInterface(new PPLockDataInterface() {
                //Monitor lock data
                @Override
                public void monitorLockData(PPBodyFatModel bodyFatModel, PPDeviceModel deviceModel) {
                    if (bodyFatModel.isHeartRateEnd()) {
                        if (bodyFatModel != null) {
                            Logger.d("monitorLockData bodyFatModel weightKg = "+ bodyFatModel.getPpWeightKg());
                        } else {
                            Logger.d("monitorLockData bodyFatModel heartRate = "+ bodyFatModel.getPpHeartRate());
                        }
                        String weightStr = PPUtil.getWeight(unitType, bodyFatModel.getPpWeightKg());
                        if (weightTextView != null) {
                            weightTextView.setText(weightStr);
                            showDialog(deviceModel, bodyFatModel);
                        }
                    } else {
                        Logger.d("Measuring heart rate");
                    }
                }
            });

##### 1.3.2 Historical data

            if (searchType != 0) {
                //Do not receive offline data when binding the device, If you need to receive offline data, please implement this interface
                protocalFilter.setPPHistoryDataInterface(new PPHistoryDataInterface() {
                    @Override
                    public void monitorHistoryData(PPBodyFatModel bodyBaseModel, boolean isEnd, String dateTime) {
                        if (bodyBaseModel != null) {
                            Logger.d("ppScale_ isEnd = "+ isEnd +" dateTime = "+ dateTime +" bodyBaseModel weight kg = "+ bodyBaseModel.getPpWeightKg());
                        } else {
                            Logger.d("ppScale_ isEnd = "+ isEnd);
                        }
                    }
                });
            }

#### 1.4 Data object instantiation method PPBodyFatModel

If you parse the Bluetooth protocol data by yourself, you need to instantiate this class to obtain
the corresponding other body data.

    /**
     * Single weighing
     *
     * @param ppWeightKg weight must be passed
     * @param scaleType device type {@link com.peng.ppscale.business.device.PPDeviceType#PPDeviceTypeBodyFat} optional
     * @param userModel user information, fat measurement data must be transmitted {@link PPUserModel}
     * @param scaleName Bluetooth scale name optional
     */
    public PPBodyFatModel(double ppWeightKg, String scaleType, PPUserModel userModel, String scaleName) {
        super(ppWeightKg, scaleType, userModel, scaleName);
    }

    /**
     * Fat test
     *
     * @param ppWeightKg weight must be passed
     * @param impedance Encrypted impedance, fat measurement data must be transmitted
     * @param scaleType device type {@link com.peng.ppscale.business.device.PPDeviceType#PPDeviceTypeBodyFat} optional
     * @param userModel user information, fat measurement data must be transmitted {@link PPUserModel}
     * @param scaleName Bluetooth scale name optional
     */
    public PPBodyFatModel(double ppWeightKg, int impedance, String scaleType, PPUserModel userModel, String scaleName) {
        super(ppWeightKg, impedance, scaleType, userModel, scaleName, PPUnitType.Unit_KG);
    }

##### 1.4.1 Data object PPBodyFatModel parameter description

    protected int impedance; //Impedance value (encrypted)
    // protected float ppZTwoLegs; //Pin-to-pin impedance value (Ω), range 200.0 ~ 1200.0
    protected double ppWeightKg; //Weight
    protected int ppHeartRate; //Heart rate
    protected int scaleType; // said type
    protected boolean isHeartRateEnd = true; //Heart rate end symbol
    protected String scaleName; //Name
    protected int thanZero;//Positive and negative 0 means negative value 1 positive value 
    protected PPUnitType unit; //weight unit default kg 
    protected PPUserModel userModel; 
    protected PPUserSex ppSex; //Gender protected double ppHeightCm; //Height (cm), needs to be 90 ~ 220cm 
    protected int ppAge; //Age (years old),must be 6 ~ 99 years old 
    protected double ppProteinPercentage; //Protein, resolution 0.1, range 2.0% ~ 30.0% 
    protected int ppBodyAge; //Body age, 6~99 years old 
    protected double ppIdealWeightKg; //Ideal weight (kg)
    protected double ppBMI; //BMI body mass index, resolution 0.1, range 10.0 ~ 90.0 
    protected int ppBMR; //Basal Metabolic Rate, resolution 1, range 500 ~ 10000 
    protected int ppVFAL; //Visceral fat area leverl Visceral fat, resolution 1, range 1 ~ 60 
    protected double ppBoneKg; //Bone mass (kg), resolution 0.1, range 0.5 ~ 8.0 
    protected double ppBodyfatPercentage; //Fat percentage (%),resolution 0.1, range 5.0% ~ 75.0% 
    protected double ppWaterPercentage; //Water content (%),resolution 0.1, range 35.0% ~ 75.0% 
    protected double ppMuscleKg; //Muscle mass (kg), resolution 0.1,range 10.0 ~ 120.0 
    protected int ppBodyType; //Body type 
    protected int ppBodyScore; //Body score 50 ~ 100 points 
    protected double ppMusclePercentage; //Muscle rate (%), resolution 0.1, range 5%~90%
    protected double ppBodyfatKg; //Fat mass (kg)
    protected double ppBodystandard; //standard weight (kg)
    protected double ppLoseFatWeightKg; //Fat-free body weight (kg)
    protected double ppControlWeightKg; //Weight control (kg)
    protected double ppFatControlKg; //Fat control volume (kg)
    protected double ppBonePercentage; //Skeletal muscle rate (%)
    protected double ppBodyMuscleControlKg; //Muscle control volume (kg)
    protected double ppVFPercentage; //Subcutaneous fat (%)
    protected PPBodyEnum.PPBodyGrade ppBodyHealth; //health assessment 
    protected PPBodyEnum.PPBodyFatGrade ppFatGrade; //obesity grade 
    protected PPBodyEnum.PPBodyHealthAssessment ppBodyHealthGrade; //health level 
    protected PPBodyEnum.PPBodyfatErrorType ppBodyfatErrorType;//error type

Note: When you get the object when using it, please call the corresponding get method to get the
corresponding value

###### 1.4.1.1 Error Type PPBodyEnum.PPBodyfatErrorType

      PPBodyfatErrorTypeNone(0), //! <No error (all parameters can be read)
      PPBodyfatErrorTypeImpedance(-1), //!< If the impedance is wrong, when the impedance is wrong, the parameters other than BMI/idealWeightKg will not be calculated (write 0)
      PPBodyfatErrorTypeAge(-1), //!< The age parameter is wrong, it needs to be between 6 and 99 years old (does not calculate parameters other than BMI/idealWeightKg)
      PPBodyfatErrorTypeWeight(-2), //!< The weight parameter is wrong, it needs to be 10 ~ 200kg (if there is an error, all parameters will not be calculated)
      PPBodyfatErrorTypeHeight(-3); //!< The height parameter is wrong, it needs to be within 90 ~ 220cm (not counting all parameters)

###### 1.4.1.2 Health Assessment PPBodyEnum.PPBodyfatErrorType

      PPBodyGradeThin(0), //! <Thin type
      PPBodyGradeLThinMuscle(1), //!< Standard type
      PPBodyGradeMuscular(2), //! <Super Heavy
      PPBodyGradeLackofexercise(3); //!< Obesity

###### 1.4.1.3 Obesity level PPBodyEnum.PPBodyfatErrorType

      PPBodyGradeFatOne(0), //! <Obesity Level 1
      PPBodyGradeLFatTwo(1), //! <Obesity Level 2
      PPBodyGradeFatThree(2), //! <Fatty Grade 3
      PPBodyGradeFatFour(-1); //!< parameter error

###### 1.4.1.4 Health Level PPBodyEnum.PPBodyfatErrorType

      PPBodyAssessment1(0), //!< There are hidden dangers to health
      PPBodyAssessment2(1), //! <Sub-health
      PPBodyAssessment3(2), //! <General
      PPBodyAssessment4(3), //! <good
      PPBodyAssessment5(4), //! <Very good
      PPBodyAssessmentError(-1); //!< parameter error

###### 1.4.1.5, body type PPBodyFatModel.ppBodyType

     0 Lean
     1 Lean muscle type
     2 Muscular
     3 lack of exercise
     4 Standard
     5 Standard muscle type
     6 Puffiness and obesity
     7 Fat muscle type
     8 Muscular overweight

##### 1.4.2 Device object PPDeviceModel parameter description

    String deviceMac;//device mac
    String deviceName;//device Bluetooth name
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
     * @see PPScaleDefine.PPDeviceProtocolType
     */
    public PPScaleDefine.PPDeviceProtocolType deviceProtocolType;
    /**
     * Calculation
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
     * Device connection type, used for the state that must be directly connected
     *
     * @see PPScaleDefine.PPDeviceConnectType
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
      * Can you connect
      */
      public boolean deviceConnectAbled;

###### 1.4.2.1 PPScaleDefine.PPDeviceProtocolType Protocol type, specific description

    PPDeviceProtocolTypeUnknow(0), //Unknown protocol
    PPDeviceProtocolTypeV2(1) //Use V2.0 Bluetooth protocol
    PPDeviceProtocolTypeV3(2), //Use V3.0 Bluetooth protocol
    PPDeviceProtocolTypeTorre(3) //Four-electrode, eight-electrode protocol

###### 1.4.2.2 PPScaleDefine.PPDeviceType Device type specific description

    PPDeviceTypeUnknow(0), //unknown
    PPDeviceTypeCF(1), //body fat scale
    PPDeviceTypeCE(2), //Weight scale
    PPDeviceTypeCB(3), //Baby scale
    PPDeviceTypeCA(4), //Kitchen scale
    PPDeviceTypeCC(5); //Bluetooth wifi scale

###### 1.4.2.3 PPScaleDefine.PPDeviceAccuracyType Weight's precision type specification

    PPDeviceAccuracyTypeUnknow(0), //Unknown precision
    PPDeviceAccuracyTypePoint01(1), //KG accuracy 0.1
    PPDeviceAccuracyTypePoint005(2), //KG accuracy 0.05
    PPDeviceAccuracyTypePointG(3), // 1G precision
    PPDeviceAccuracyTypePoint01G(4); // 0.1G precision

###### 1.4.2.4 PPScaleDefine.DeviceCalcuteType Body fat calculation type specific description

    PPDeviceCalcuteTypeUnknow(0), //unknown
    PPDeviceCalcuteTypeInScale(1), //scale calculation
    PPDeviceCalcuteTypeDirect(2), //DC
    PPDeviceCalcuteTypeAlternate(3), //communication
    PPDeviceCalcuteTypeNeedNot(4) //No need to calculate

###### 1.4.2.5 PPScaleDefine.PPDevicePowerType Power supply mode specification

    PPDevicePowerTypeUnknow(0), //unknown
    PPDevicePowerTypeBattery(1), // battery powered
    PPDevicePowerTypeSolar(2), //Solar power supply
    PPDevicePowerTypeCharge(3); //Charging model

###### 1.4.2.6 PPScaleDefine.PPDeviceFuncType Function type, which can be multi-function superimposed, detailed description

    PPDeviceFuncTypeWeight(0x01), //weight
    PPDeviceFuncTypeFat(0x02), //Measure body fat
    PPDeviceFuncTypeHeartRate(0x04), //heart rate
    PPDeviceFuncTypeHistory(0x08), //historical data
    PPDeviceFuncTypeSafe(0x10), //safe mode, pregnant woman mode
    PPDeviceFuncTypeBMDJ(0x20); //Close single pin

###### 1.4.2.7 PPScaleDefine.PPDeviceUnitType Supported units, specific description (not enabled for now)

    PPDeviceUnitTypeKG(0x01), //kg
    PPDeviceUnitTypeLB(0x02), //lb
    PPDeviceUnitTypeST(0x04), //st
    PPDeviceUnitTypeJin(0x08), //jin
    PPDeviceUnitTypeSTLB(0x10);//st:lb

#### 1.5 Bluetooth status monitoring callback and system Bluetooth status callback

Contains two callback methods, one is Bluetooth status monitoring, the other is system Bluetooth
callback

     PPBleStateInterface bleStateInterface = new PPBleStateInterface() {
            //Bluetooth status monitoring
            //deviceModel is in the scanning process of Bluetooth, it is null
            @Override
            public void monitorBluetoothWorkState(PPBleWorkState ppBleWorkState, PPDeviceModel deviceModel) {
                if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnected) {
                    Logger.d("The device is connected");
                } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnecting) {
                    Logger.d("device connecting");
                } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateDisconnected) {
                    Logger.d("The device has been disconnected");
                } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateStop) {
                    Logger.d("Stop scanning");
                } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateSearching) {
                    Logger.d("Scanning");
                } else if (ppBleWorkState == PPBleWorkState.PPBleWorkSearchTimeOut) {
                    Logger.d("Scan timeout");
                } else {
                    Logger.e("Bluetooth status abnormal");
                }
            }
    
            //System Bluetooth callback
            @Override
            public void monitorBluetoothSwitchState(PPBleSwitchState ppBleSwitchState) {
                if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOff) {
                    Logger.e("System Bluetooth disconnected");
                    Toast.makeText(BindingDeviceActivity.this, "System Bluetooth is disconnected", Toast.LENGTH_SHORT).show();
                } else if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOn) {
                    Logger.d("System Bluetooth is on");
                    Toast.makeText(BindingDeviceActivity.this, "System Bluetooth is on", Toast.LENGTH_SHORT).show();
                } else {
                    Logger.e("System Bluetooth Abnormal");
                }
            }
        };

## 3. Food scale instructions

### 1.1 Unit enumeration class PPUnitType

        Unit_KG(0),//KG
    
        Unit_LB(1),//LB
    
        PPUnitST(2),//ST
    
        PPUnitJin(3),//jin
    
        PPUnitG(4),//g
    
        PPUnitLBOZ(5),//lb:oz
    
        PPUnitOZ(6),//oz
    
        PPUnitMLWater(7),//ml(water)
    
        PPUnitMLMilk(8);//milk

### 1.2 Positive and negative value problems:

       In the field of PPBodyFatModel:
       int thanZero; //Positive and negative 0 means negative value 1 positive value

### 1.3 Switch unit call:

        PPScale.changeKitchenScaleUnit(PPUnitType unitType)

### 1.4 Food scale reset:

        PPScale.toZeroKitchenScale()

### 1.5 When using a food scale, you need to disconnect it manually when you don’t need to connect it

       PPScale.disConnect()

## Four. Closed eyes and single foot mode

###### Closed eyes and single foot mode

Use PPScale's instance object to call the method of scanning nearby devices to search for nearby
closed-eye single-leg Bluetooth scales and connect.

### 1.1 Connect a closed-eye single-leg device

```
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
```

### 1.2 Exit the closed-eye single-leg mode and stop scanning to disconnect

```
// Stop scanning and cut off the device that opens and closes the single foot
-ppScale.exitBMDJModel();
```

### 1.3 Monitor the status of closed eyes and one foot

```
(interface)PPBMDJStatesInterface
```

### 1.4 Send instructions to make the device enter the closed-eye single-leg mode

```
// The closed-eye single-leg device enters the ready state
-(void)enterBMDJModel()
```

### 1.5 Return the time of standing on one foot with closed eyes in the callback function

```
/// The device exits the closed-eye single-foot state
-(interface)PPBMDJDataInterface;
```

## Five. Bluetooth operation related

### 1.1 Reserved Bluetooth operation object

     BluetoothClient client = ppScale.getBleClient();

### 1.2 Stop scanning

        ppScale.stopSearch();

### 1.3 Disconnect the device

         ppScale.disConnect();

Finally, you need to call the stopSearch method before leaving the page. For specific
implementation, please refer to the code in BindingDeviceActivity and ScaleWeightActivity in Demo.

## 六. [Version update instructions](doc/version_update.md)

## Seven. Third-party libraries used

#### 1. Body fat calculation library provided by chip solution provider

#### 2. [bluetoothkit1.4.0 Bluetooth library](https://github.com/dingjikerbo/Android-BluetoothKit)

## Eight. [Common problem](doc/common_problem.md)

Contact Developer: Email: yanfabu-5@lefu.cc


   
   
   
