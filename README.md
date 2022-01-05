[English Docs](doc/README-en.md)  |  [中文文档](README.md)

# LF蓝牙秤/WiFi秤SDK

ppscale是蓝牙连接逻辑以及数据解析逻辑。 在开发者集成的时候，请采用从maven下载的库的集成方式集成。建议开发者查看README.md文档，完成集成。

## 一、 蓝牙wiFi相关的文档

示例module是：BleWifiScaleDemo
[蓝牙WIFI秤文档中文](BleWifiScaleDemo/README_CN.md)    
[Bluetooth WIFI Scale example English](BleWifiScaleDemo/README_EN.md)

## 二、蓝牙体脂秤示例

### Ⅰ. 集成方式

##### sdk引入方式

在需要引入sdk的module下的build.gradle中加入(最新版本请查看ppscalelib的module下的libs)

         dependencies {
                    //1.aar引入
                    implementation files('libs/ppscale-213-20220105-0327015.aar')
                    //2.github maven引入, 需要参考Project下的build.gradle引入 maven { url "https://raw.githubusercontent.com/PPScale/ppscale-android-maven/main" }
                    //implementation 'com.lefu.ppscale:ppscale:2.1.3-SNAPSHOT'
         }

### Ⅱ .使用说明

### 1.1 蓝牙权限相关

* 由于需要蓝牙连接，Demo需要真机运行。

* 在Android 6.0及以上系统版本，启动扫描前，需确保开启

  1、定位权限  
  2、定位开关  
  3、蓝牙开关

* 如果需要体重值以外的信息需要输入身高、年龄、性别并且光脚上秤。

* 使用Demo过程中需要您打开蓝牙，同时给予Demo定位权限

### 1.2 通用功能

#### 1.2.1 用户信息编辑

    用户信息编辑 - 输入用户身高，年龄、性别以及体重单位，如果秤支持孕妇模式和运动员模式也可开启

    身高的取值范围：30-220厘米；
    年龄的取值范围：10-99岁；
    体重单位 0代表千克，1代表斤，2代表镑；
    性别 1代表男，0代表女；
    用户组取值范围 0-9（特定的秤需要这个值）
    孕妇模式 1开启 0关闭(需要秤支持)
    和运动员模式1开启 0关闭(需要秤支持)

#### 1.2.2 绑定设备

	绑定设备 - 在这个控制器在被实例化后会开始扫描附近的外设，并将您的外设做一个记录。

#### 1.2.3 上秤称重

	上秤称重 - 这个控制器在被实例化后也会开始扫描附近的外设，通过过滤去连接已绑定过的设备。所以只有被绑定过后才能去进行上秤称重，否则无法接收到数据。

#### 1.2.4 设备管理

    设备管理 - 这个控制器会用列表的方式展示你在“绑定设备”页面绑定的外设。你可以通过长按的方式去删除已绑定设备。

#### 1.2.5 数据详情

    在“绑定设备”和“上秤称重”页面接收到外设返回的数据后，会自动跳转到数据详情页面

### 1.3 蓝牙功能

#### 1.3.1 扫描设备

	搜索附近支持的设备ScanDeviceListActivity.java

	 	//获取周围蓝牙秤设备
        ppScale.monitorSurroundDevice();
		//连接选定设备
		if (ppScale != null) {
               ppScale.connectWithMacAddressList(models);
        }

#### 1.3.2 读取历史数据

	需要先“绑定设备”然后再读取“历史数据”	ReadHistoryListActivity.java
	 //直接读取历史数据，需要传入要读取的秤
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
                    .setDeviceList(addressList)//注意：这里是必传项
                    .setUserModel(userModel)
                    .setBleStateInterface(bleStateInterface)
                    .build();
            //获取历史数据
            tv_starts.setText("开始读取离线数据");
            ppScale.fetchHistoryData();
        } else {
            tv_starts.setText("请先绑定设备");
        }

    }

#### 1.3.3 [闭目单脚](#闭目单脚模式)

<a href="#anchor">Link to Anchor</a>

### 1.4 WiFi功能

#### 1.4.1 蓝牙配网

    蓝牙配网 - 该功能用于蓝牙WiFi秤，在给秤配置网络时使用


### Ⅲ .PPScale在蓝牙设备的使用

#### 1.1 绑定或扫描指定蓝牙设备

        //绑定设备和扫描设备的区别在于  searchType  0绑定设备 1扫描指定设备
        //setProtocalFilterImpl() 接收数据回调接口，过程数据、锁定数据、历史数据，
        //setDeviceList()函数的参数为不为空，绑定需传null(或不调用)，扫描指定设备请传指定设备的List<DeviceModel>。绑定
        //setBleOptions()蓝牙参数配置
        //setBleStateInterface() 需要参数PPBleStateInterface，蓝牙状态监控回调和系统蓝牙状态回调
        //startSearchBluetoothScaleWithMacAddressList（）启动扫描设备
        //setUserModel() 参数PPUserModel 正常情况， setUserModel()是必须的，
            
       /**
        * sdk入口,实例对象 
        */
       private void bindingDevice() {
               if (searchType == 0) {
                   ppScale = new PPScale.Builder(this)
                           .setProtocalFilterImpl(getProtocalFilter())
                           .setBleOptions(getBleOptions())
       //                    .setDeviceList(null)
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
                            .setUserModel(userModel) 参数PPUserModel 正常情况， setUserModel()是必须的，
                           .build();
                   ppScale.startSearchBluetoothScaleWithMacAddressList();
               }

注意：如果需要自动循环扫描，需要在lockedData()后重新调用 ppScale.startSearchBluetoothScaleWithMacAddressList()

#### 1.2  BleOptions 蓝牙参数配置

##### 1.2.2 用户基础信息

PPUserModel参数说明：

        userHeight、age、sex必须是真实的 
        userHeight范围是100-220cm
        age范围是10-99
        sex 0为女 1为男   
        maternityMode 0为正常 1为孕妇   

#### 1.3 数据处理

##### 1.3.1 过程数据和锁定数据

    //根据需求实现接口
    //监听过程数据setPPProcessDateInterface()
    //监听锁定数据setPPLockDataInterface()
    //监听历史数据setPPHistoryDataInterface()
   
     ProtocalFilterImpl protocalFilter = new ProtocalFilterImpl();
            protocalFilter.setPPProcessDateInterface(new PPProcessDateInterface() {
            //过程数据
                @Override
                public void monitorProcessData(PPBodyBaseModel bodyBaseModel) {
                    Logger.d("bodyBaseModel scaleName " + bodyBaseModel.getScaleName());
                    //体重
                    String weightStr = PPUtil.getWeight(unitType, bodyBaseModel.getPpWeightKg());
                    
                }
            });
            protocalFilter.setPPLockDataInterface(new PPLockDataInterface() {
                //监听锁定数据
                @Override
                public void monitorLockData(PPBodyFatModel bodyFatModel, PPDeviceModel deviceModel) {  
                    if (bodyFatModel.isHeartRateEnd()) {
                        if (bodyFatModel != null) {
                            Logger.d("monitorLockData  bodyFatModel weightKg = " + bodyFatModel.getPpWeightKg());
                        } else {
                            Logger.d("monitorLockData  bodyFatModel heartRate = " + bodyFatModel.getPpHeartRate());
                        }
                        String weightStr = PPUtil.getWeight(unitType, bodyFatModel.getPpWeightKg());
                        if (weightTextView != null) {
                            weightTextView.setText(weightStr);
                            showDialog(deviceModel, bodyFatModel);
                        }
                    } else {
                        Logger.d("正在测量心率");
                    }
                }
            });

##### 1.3.2 历史数据

            if (searchType != 0) {
                //Do not receive offline data when binding the device， If you need to receive offline data, please implement this interface
                protocalFilter.setPPHistoryDataInterface(new PPHistoryDataInterface() {
                    @Override
                    public void monitorHistoryData(PPBodyFatModel bodyBaseModel, boolean isEnd, String dateTime) {
                        if (bodyBaseModel != null) {
                            Logger.d("ppScale_ isEnd = " + isEnd + " dateTime = " + dateTime + " bodyBaseModel weight kg = " + bodyBaseModel.getPpWeightKg());
                        } else {
                            Logger.d("ppScale_ isEnd = " + isEnd);
                        }
                    }
                });
            }

#### 1.4 数据对象实例化方式 PPBodyFatModel

如果是自行解析蓝牙协议数据的，需要实例化该类，来获取对应的其他身体数据，

    /**
     * 单称重
     *
     * @param ppWeightKg 体重 必传
     * @param scaleType  设备类型 {@link com.peng.ppscale.business.device.PPDeviceType#PPDeviceTypeBodyFat} 非必传
     * @param userModel  用户信息 测脂肪数据必传{@link PPUserModel}
     * @param scaleName  蓝牙秤名称 非必传
     */
    public PPBodyFatModel(double ppWeightKg, String scaleType, PPUserModel userModel, String scaleName) {
        super(ppWeightKg, scaleType, userModel, scaleName);
    }

    /**
     * 测脂
     *
     * @param ppWeightKg 体重 必传
     * @param impedance  加密阻抗 测脂肪数据必传
     * @param scaleType  设备类型 {@link com.peng.ppscale.business.device.PPDeviceType#PPDeviceTypeBodyFat} 非必传
     * @param userModel  用户信息 测脂肪数据必传{@link PPUserModel}
     * @param scaleName  蓝牙秤名称 非必传
     */
    public PPBodyFatModel(double ppWeightKg, int impedance, String scaleType, PPUserModel userModel, String scaleName) {
        super(ppWeightKg, impedance, scaleType, userModel, scaleName, PPUnitType.Unit_KG);
    }

##### 1.4.1 数据对象PPBodyFatModel 参数说明

    protected int impedance;                                                  //阻抗值（加密）
    //    protected float ppZTwoLegs;                                         //脚对脚阻抗值(Ω), 范围200.0 ~ 1200.0
    protected double ppWeightKg;                                              //体重
    protected int ppHeartRate;                                                //心率
    protected int scaleType;                                                  //称类型
    protected boolean isHeartRateEnd = true;                                  //心率结束符
    protected String scaleName;                                               //称名称
    protected int thanZero;                                                    //正负 0表示负值 1 正值
    protected PPUnitType unit;                                                 //重量单位 默认kg
    protected PPUserModel userModel;
    protected PPUserSex ppSex;                                                //性别
    protected double ppHeightCm;                                              //身高(cm)，需在 90 ~ 220cm
    protected int ppAge;                                                      //年龄(岁)，需在6 ~ 99岁
    protected double ppProteinPercentage;                                     //蛋白质,分辨率0.1, 范围2.0% ~ 30.0%
    protected int ppBodyAge;                                                  //身体年龄,6~99岁
    protected double ppIdealWeightKg;                                         //理想体重(kg)
    protected double ppBMI;                                                   //BMI 人体质量指数, 分辨率0.1, 范围10.0 ~ 90.0
    protected int ppBMR;                                                      //Basal Metabolic Rate基础代谢, 分辨率1, 范围500 ~ 10000
    protected int ppVFAL;                                                     //Visceral fat area leverl内脏脂肪, 分辨率1, 范围1 ~ 60
    protected double ppBoneKg;                                                //骨量(kg), 分辨率0.1, 范围0.5 ~ 8.0
    protected double ppBodyfatPercentage;                                     //脂肪率(%), 分辨率0.1, 范围5.0% ~ 75.0%
    protected double ppWaterPercentage;                                       //水分率(%), 分辨率0.1, 范围35.0% ~ 75.0%
    protected double ppMuscleKg;                                              //肌肉量(kg), 分辨率0.1, 范围10.0 ~ 120.0
    protected int ppBodyType;                                                 //身体类型
    protected int ppBodyScore;                                                //身体得分 50 ~ 100分
    protected double ppMusclePercentage;                                      //肌肉率(%),分辨率0.1，范围5%~90%
    protected double ppBodyfatKg;                                             //脂肪量(kg)
    protected double ppBodystandard;                                          //标准体重(kg)
    protected double ppLoseFatWeightKg;                                       //去脂体重(kg)
    protected double ppControlWeightKg;                                       //体重控制(kg)
    protected double ppFatControlKg;                                          //脂肪控制量(kg)
    protected double ppBonePercentage;                                        //骨骼肌率(%)
    protected double ppBodyMuscleControlKg;                                   //肌肉控制量(kg)
    protected double ppVFPercentage;                                          //皮下脂肪(%)
    protected PPBodyEnum.PPBodyGrade ppBodyHealth;                            //健康评估
    protected PPBodyEnum.PPBodyFatGrade ppFatGrade;                           //肥胖等级
    protected PPBodyEnum.PPBodyHealthAssessment ppBodyHealthGrade;            //健康等级
    protected PPBodyEnum.PPBodyfatErrorType ppBodyfatErrorType;                //错误类型

注意：在使用时拿到对象，请调用对应的get方法来获取对应的值

###### 1.4.2.1 错误类型 PPBodyEnum.PPBodyfatErrorType

      PPBodyfatErrorTypeNone(0),         //!< 无错误(可读取所有参数)
      PPBodyfatErrorTypeImpedance(-1),    //!< 阻抗有误,阻抗有误时, 不计算除BMI/idealWeightKg以外参数(写0)
      PPBodyfatErrorTypeAge(-1),          //!< 年龄参数有误，需在 6   ~ 99岁(不计算除BMI/idealWeightKg以外参数)
      PPBodyfatErrorTypeWeight(-2),       //!< 体重参数有误，需在 10  ~ 200kg(有误不计算所有参数)
      PPBodyfatErrorTypeHeight(-3);       //!< 身高参数有误，需在 90 ~ 220cm(不计算所有参数)

###### 1.4.2.2 健康评估 PPBodyEnum.PPBodyfatErrorType

      PPBodyGradeThin(0),             //!< 偏瘦型
      PPBodyGradeLThinMuscle(1),      //!< 标准型
      PPBodyGradeMuscular(2),         //!< 超重型
      PPBodyGradeLackofexercise(3);   //!< 肥胖型

###### 1.4.2.3 肥胖等级 PPBodyEnum.PPBodyfatErrorType

      PPBodyGradeFatOne(0),             //!< 肥胖1级
      PPBodyGradeLFatTwo(1),            //!< 肥胖2级
      PPBodyGradeFatThree(2),           //!< 肥胖3级
      PPBodyGradeFatFour(-1);           //!< 参数错误

###### 1.4.2.4 健康等级 PPBodyEnum.PPBodyfatErrorType

      PPBodyAssessment1(0),          //!< 健康存在隐患
      PPBodyAssessment2(1),          //!< 亚健康
      PPBodyAssessment3(2),          //!< 一般
      PPBodyAssessment4(3),          //!< 良好
      PPBodyAssessment5(4),          //!< 非常好
      PPBodyAssessmentError(-1);          //!< 参数错误

###### 1.4.2.5、身体类型 PPBodyFatModel.ppBodyType

     0 偏瘦型
     1 偏瘦肌肉型
     2 肌肉发达型
     3 缺乏运动型
     4 标准型
     5 标准肌肉型
     6 浮肿肥胖型
     7 偏胖肌肉型
     8 肌肉型偏胖

#### 1.5 蓝牙状态监控回调和系统蓝牙状态回调

包含两个回调方法，一个是蓝牙状态监控，一个是系统蓝牙回调

     PPBleStateInterface bleStateInterface = new PPBleStateInterface() {
            //蓝牙状态监控
            //deviceModel 在蓝牙处于扫描过程中，它是null
            @Override
            public void monitorBluetoothWorkState(PPBleWorkState ppBleWorkState, PPDeviceModel deviceModel) {
                if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnected) {
                    Logger.d("设备已连接");
                } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnecting) {
                    Logger.d("设备连接中");
                } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateDisconnected) {
                    Logger.d("设备已断开");
                } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateStop) {
                    Logger.d("停止扫描");
                } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateSearching) {
                    Logger.d("扫描中");
                } else if (ppBleWorkState == PPBleWorkState.PPBleWorkSearchTimeOut) {
                    Logger.d("扫描超时");
                } else {
                    Logger.e("蓝牙状态异常");
                }
            }
    
            //系统蓝牙回调
            @Override
            public void monitorBluetoothSwitchState(PPBleSwitchState ppBleSwitchState) {
                if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOff) {
                    Logger.e("系统蓝牙断开");
                    Toast.makeText(BindingDeviceActivity.this, "系统蓝牙断开", Toast.LENGTH_SHORT).show();
                } else if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOn) {
                    Logger.d("系统蓝牙打开");
                    Toast.makeText(BindingDeviceActivity.this, "系统蓝牙打开", Toast.LENGTH_SHORT).show();
                } else {
                    Logger.e("系统蓝牙异常");
                }
            }
        };

## 三 .食物秤说明

### 1.1 单位枚举类 PPUnitType

        Unit_KG(0),//KG
    
        Unit_LB(1),//LB
    
        PPUnitST(2),//ST
    
        PPUnitJin(3),//斤
    
        PPUnitG(4),//g
    
        PPUnitLBOZ(5),//lb:oz
    
        PPUnitOZ(6),//oz
    
        PPUnitMLWater(7),//ml(water)
    
        PPUnitMLMilk(8);//milk

### 1.2 正负值问题：

       PPBodyFatModel的字段里:
       int thanZero; //正负 0表示负值 1 正值

### 1.3 切换单位调用：

        PPScale.changeKitchenScaleUnit(PPUnitType unitType)

### 1.4 食物秤归零：

        PPScale.toZeroKitchenScale()

### 1.5 使用食物秤在不需要连接时，需要手动断开连接

       PPScale.disConnect()

## 四 .闭目单脚模式
# 闭目单脚模式    
<a id="anchor"></a> Anchor

使用PPScale的实例对象调用扫描附近设备的方法来搜索附近的闭目单脚蓝牙秤并进行连接。

### 1.1 连接闭目单脚设备

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

### 1.2 退出闭目单脚模式并停止扫描断开连接

```
/// 停止扫描切断开闭目单脚的设备
-  ppScale.exitBMDJModel();
```

### 1.3 监听闭目单脚状态

```
(interface)PPBMDJStatesInterface
```

### 1.4 发送指令使设备进入闭目单脚模式

```
/// 闭目单脚设备进入准备状态
- (void)enterBMDJModel()
```

### 1.5 在回调函数中返回闭目单脚站立的时间

```
/// 设备退出闭目单脚状态
- (interface)PPBMDJDataInterface;
```

## 五 .蓝牙操作相关

### 1.1 预留蓝牙操作对象

     BluetoothClient client = ppScale.getBleClient();

### 1.2 停止扫描

       ppScale.stopSearch();

### 1.3 断开设备连接

        ppScale.disConnect();

最后你需要在离开页面的之前调用stopSearch方法。 具体的实现请参考Demo中BindingDeviceActivity和ScaleWeightActivity中的代码。


----------------------------------------------------------------------------------------

## 六.版本更新说明

## 七. [使用的第三方库](doc/version_update.md)

## 八. [常见问题](doc/common_problem.md)

1、芯片方案商提供的体脂计算库 2、[bluetoothkit1.4.0 蓝牙库](https://github.com/dingjikerbo/Android-BluetoothKit)

Contact Developer： Email: yanfabu-5@lefu.cc

   
   
   
