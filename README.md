[English Docs](doc/README-en.md)  |  [中文文档](README.md)
## [PPScale iOS SDK](https://gitee.com/pengsiyuan777/ppscale-demo-ios)

# PPScale Android SDK 

## -LF蓝牙秤/食物秤/WiFi秤

ppscale是蓝牙连接逻辑以及数据解析逻辑。 在开发者集成的时候，请采用从maven下载的库的集成方式集成。建议开发者查看README.md文档，完成集成。
        
### Ⅰ. 集成方式

#### sdk引入方式

在需要引入sdk的module下的build.gradle中加入(最新版本请查看ppscalelib的module下的libs)

    dependencies {
        //aar引入
        api fileTree(include: ['*.jar', '*.aar'], dir: 'libs')
    }

### Ⅱ .使用说明

### 1.1 蓝牙权限相关

#### 1.1.1 运行环境

由于需要蓝牙连接，Demo需要真机运行，Android手机6.0及以上或HarmonyOS2.0及以上

#### 1.1.2 蓝牙权限相关约定

使用Demo过程中需要您打开蓝牙，打开GPS开关，需确保开启和授权相关必要的权限

##### 1.1.2.1 在Android 6.0及以上系统版本，

  1、定位权限  
  2、定位开关  
  3、蓝牙开关

##### 1.1.2.2 在Android 12.0及以上系统版本，启动扫描前，需确保开启和授权相关必要的权限

  可以查看官方蓝牙权限文档，文档地址：[Google开发者网站关于Bluetooth permissions说明](https://developer.android.com/guide/topics/connectivity/bluetooth/permissions).
  
  1、定位权限
  2、定位开关  
  3、蓝牙开关
  4、扫描和连接附近的设备
   
   
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
       ...
    </manifest>

#### 1.1.3 测量身体数据相关约定

   如果需要体重值以外的信息需要输入身高、年龄、性别并且光脚上秤。


### 1.2 主页功能说明

#### 1.2.1 用户信息编辑

用户信息编辑 - 输入用户身高，年龄、性别以及体重单位，如果秤支持孕妇模式和运动员模式也可开启

可输入的内容:

    身高的取值范围：30-220厘米；
    年龄的取值范围：10-99岁；
    体重单位 0代表千克，1代表斤，2代表镑；
    性别 1代表男，0代表女；
    用户组取值范围 0-9（特定的秤需要这个值）
    孕妇模式 1开启 0关闭(需要秤支持)
    运动员模式1开启 0关闭(需要秤支持)

PPUserModel参数说明：

    userHeight、age、sex必须是真实的 
    userHeight范围是100-220cm
    age年龄 范围是10-99
    sex 性别 0为女 1为男
    isAthleteMode;//运动员模式 false为正常 true开启(需要秤支持)
    isPregnantMode;//孕妇模式 false为正常 true开启(需要秤支持)

#### 1.2.2 绑定设备

绑定设备 - 在这个控制器在被实例化后会开始扫描附近的外设，并将您的外设做一个记录。
        - 在“绑定设备”页面绑定成功后，会自动跳转到设备管理列表页面
        - 体脂秤和食物秤都在此绑定

#### 1.2.3 体脂秤称重

体脂秤称重 - 这个控制器在被实例化后也会开始扫描附近的外设，通过过滤去连接已绑定过的设备。所以只有被绑定过后才能去进行上秤称重，否则无法接收到数据。
        - 在“体脂秤称重”页面接收到锁定数据后，会自动跳转到数据详情页面
	
#### 1.2.3 食物秤称重	

食物秤称重 - 扫描和连接已经绑定的食物秤，接收和解析重量数据的示例
	
#### 1.2.4 设备管理

设备管理 - 这个控制器会用列表的方式展示你在“绑定设备”页面绑定的外设。你可以通过长按的方式去删除已绑定设备。
    -这里面同时包含了体脂秤和食物秤

#### 1.2.5 模拟体脂计算

模拟体脂计算 - 根据蓝牙协议解析出的体重、阻抗，加上用户数据的身高、年龄、性别，计算出体脂率等多项体脂参数信息。
            
#### 1.2.6 扫描附近设备列表

扫描附近设备列表 - 附近体脂秤的列表，可以设置扫描时间和过滤信号强度，可以选则某个设备进行连接和称重
    -该功能只针对体脂秤


### Ⅲ .PPScale在蓝牙设备的使用

#### 1.1 PPScale初始化，以及基础约定

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

注意：如果需要自动循环扫描，需要在lockedData()后重新调用 
    
    ppScale.startSearchBluetoothScaleWithMacAddressList()

#### 1.2 体脂秤

##### 1.2.1 过程数据和锁定数据

    //根据需求实现接口
    //监听过程数据setPPProcessDateInterface()
    //监听锁定数据setPPLockDataInterface()
    //监听历史数据setPPHistoryDataInterface()
   
     ProtocalFilterImpl protocalFilter = new ProtocalFilterImpl();
     protocalFilter.setPPProcessDateInterface(new PPProcessDateInterface() {
    
        // 过程数据
        @Override
        public void monitorProcessData(PPBodyBaseModel bodyBaseModel, PPDeviceModel deviceModel) {
            Logger.d("bodyBaseModel scaleName " + bodyBaseModel);
            String weightStr = PPUtil.getWeight(bodyBaseModel.getUnit(), bodyBaseModel.getPpWeightKg(), deviceModel.deviceAccuracyType.getType());
            weightTextView.setText(weightStr);
        }
    });
    protocalFilter.setPPLockDataInterface(new PPLockDataInterface() {

        //锁定数据
        @Override
        public void monitorLockData(PPBodyFatModel bodyFatModel, PPDeviceModel deviceModel) {
            onDataLock(bodyFatModel, deviceModel);
        }

        @Override
        public void monitorOverWeight() {
            Logger.e("over weight ");
        }
    });
    
##### 1.2.2  历史数据

需要先使用“绑定设备”功能绑定对应的设备，然后再读取“历史数据”，具体参考：ReadHistoryListActivity.java
入口是在设备管理，选则一个设备，然后会有“读取历史数据”功能，使用前请确保你的秤支持历史数据
    	
###### 1.2.2.1  读取历史数据

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
            tv_starts.setText("开始读取离线数据");
            ppScale.fetchHistoryData();//读取历史数据
        } else {
            tv_starts.setText("请先绑定设备");
        }
    }
    
###### 1.2.2.2  历史数据回调       
        
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
                 tv_starts.setText("没有离线数据");
             } else {
                 tv_starts.setText("读取历史数据结束");
             }
             //历史数据结束，删除历史数据
        // deleteHistoryData();
         }

         @Override
         public void monitorHistoryFail() {

         }
     });
     
###### 1.2.2.3  删除历史数据   
        
     //删除历史
     if (ppScale != null) {
         ppScale.deleteHistoryData();
     }

#### 1.3 食物秤

##### 1.3.1  接收和解析数据
    示例代码：FoodSclaeDeviceActivity.java
    final ProtocalFilterImpl protocalFilter = new ProtocalFilterImpl();
    protocalFilter.setFoodScaleDataProtocoInterface(new FoodScaleDataProtocoInterface() {
        @Override
        public void processData(LFFoodScaleGeneral foodScaleGeneral, PPDeviceModel deviceModel) {
            textView.setText("过程数据");
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
    
##### 1.3.2  单位转换示例：
    
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
                String[] values = unit.format().split(split);
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
    

##### 1.3.3 单位枚举类 PPUnitType

        Unit_KG(0),//KG 
        Unit_LB(1),//LB
        PPUnitST(2),//ST
        PPUnitJin(3),//斤
        PPUnitG(4),//g
        PPUnitLBOZ(5),//lb:oz
        PPUnitOZ(6),//oz
        PPUnitMLWater(7),//ml(water)
        PPUnitMLMilk(8);//milk

##### 1.3.4 正负值问题：

       PPBodyFatModel的字段里:
       int thanZero; //正负 0表示负值 1 正值

##### 1.3.5 切换单位调用：

        PPScale.changeKitchenScaleUnit(PPUnitType unitType)

##### 1.3.6 食物秤归零：

        PPScale.toZeroKitchenScale()

##### 1.3.7 使用食物秤在不需要连接时，需要手动断开连接

       PPScale.disConnect()
       
##### 1.3.8 单位精度
    enum class PPDeviceAccuracyType {
        PPDeviceAccuracyTypeUnknow(0),//未知精度
        //KG精度0.1
        PPDeviceAccuracyTypePoint01(1),
        //KG精度0.05
        PPDeviceAccuracyTypePoint005(2),
        // 1G精度
        PPDeviceAccuracyTypePointG(3),
        // 0.1G精度
        PPDeviceAccuracyTypePoint01G(4);
    }

##### 1.4  食物秤与体脂秤的区别

###### 1.4.1  接收数据的接口不一样

ProtocalFilterImpl中接收数据的监听器食物秤是FoodScaleDataProtocoInterface，体脂秤是由PPProcessDateInterface(过程数据)、PPLockDataInterface(锁定数据)。

###### 1.4.2  接收数据的重量单位不一样

体脂秤的重量值对应的单位是KG,食物秤的重量值对应的单位是g，重量值对应的单位是固定的。（这里的单位与PPScale给的单位不是同步的，PPScale给的单位是当前秤端的单位）

##### 1.4.3  食物秤与体脂秤的标识
     
     /**
      * 设备类型
      */
     enum class PPDeviceType {
         PPDeviceTypeUnknow(0),//未知
         PPDeviceTypeCF(1), //体脂秤
         PPDeviceTypeCE(2), //体重秤
         PPDeviceTypeCB(3),//婴儿秤
         PPDeviceTypeCA(4), //厨房秤
         PPDeviceTypeCC(5); //wifi秤

#### 1.5 扫描设备列表

	搜索附近支持的设备ScanDeviceListActivity.java

	 	//获取周围蓝牙秤设备
        1、ppScale.monitorSurroundDevice();
        //您可以以毫秒为单位动态设置扫描时间
        2、ppScale.monitorSurroundDevice(300000);  
		//连接选定设备
		if (ppScale != null) {
           ppScale.connectWithMacAddressList(models);
        }

#### 1.6 [闭目单脚](#闭目单脚模式)

### IV .WiFi秤

#### 1.1 注意事项

默认Server域名地址是：https://api.lefuenergy.com

1、确保Server正常，路由器能正常连接到Server
2、确保WiFi环境是2.4G或2.4/5G混合模式，不支持单5G模式
3、确保账号密码正确
4、确保秤端使用的Server地址与App使用的Server地址对应

#### 1.2 WiFi配网的基本流程
蓝牙配网 - 该功能用于蓝牙WiFi秤，在给秤配置网络时使用

1、首先确保已经绑定了蓝牙WiFi秤
2、用户输入Wifi账号和密码
3、发起连接设备，
4、连接成功后，在可写的回调（PPBleWorkStateWritable）里面，将账号和密码发送给秤

    ppScale.configWifi(ssid, password)

5、在PPConfigWifiInterface的监听器里面monitorConfigState方法返回sn码，此时秤上的WiFi图标会先闪烁（连接路由器中），再常量（连接路由器成功并获取到sn），
6、将sn传给Server验证秤是否已经完成注册
7、Server返回成功，则配网成功，否则配网失败

#### 1.3 数据列表

数据列表 -是从Server端获取的秤存在Server端的离线数据,并非秤端存储的历史数据

#### 1.4 设备配置

在设备管理页面，如果绑定了WiFi秤，则会显示设置入口，点击设置入口可进入到设备配置页面,
设备配置页面可以查看当前设备的SN,SSID,PASSWORD、修改秤的服务端DNS地址、清除当前秤的SSID,
对应的代码是在DeveloperActivity.class下。


### V .实体类对象及具体参数说明

#### 1.1 数据对象实例化方式 PPBodyFatModel

如果是自行解析蓝牙协议数据的，需要实例化该类，来获取对应的其他身体数据，

##### 1.1.1 数据对象PPBodyFatModel 初始化

    /**
     * 称重
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

##### 1.1.2 数据对象PPBodyFatModel 参数说明

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

##### 1.1.3 错误类型 PPBodyEnum.PPBodyfatErrorType

      PPBodyfatErrorTypeNone(0),         //!< 无错误(可读取所有参数)
      PPBodyfatErrorTypeImpedance(-1),    //!< 阻抗有误,阻抗有误时, 不计算除BMI/idealWeightKg以外参数(写0)
      PPBodyfatErrorTypeAge(-1),          //!< 年龄参数有误，需在 6   ~ 99岁(不计算除BMI/idealWeightKg以外参数)
      PPBodyfatErrorTypeWeight(-2),       //!< 体重参数有误，需在 10  ~ 200kg(有误不计算所有参数)
      PPBodyfatErrorTypeHeight(-3);       //!< 身高参数有误，需在 90 ~ 220cm(不计算所有参数)

##### 1.1.4 健康评估 PPBodyEnum.PPBodyfatErrorType

      PPBodyGradeThin(0),             //!< 偏瘦型
      PPBodyGradeLThinMuscle(1),      //!< 标准型
      PPBodyGradeMuscular(2),         //!< 超重型
      PPBodyGradeLackofexercise(3);   //!< 肥胖型

##### 1.1.5 肥胖等级 PPBodyEnum.PPBodyfatErrorType

      PPBodyGradeFatOne(0),             //!< 肥胖1级
      PPBodyGradeLFatTwo(1),            //!< 肥胖2级
      PPBodyGradeFatThree(2),           //!< 肥胖3级
      PPBodyGradeFatFour(-1);           //!< 参数错误

##### 1.1.6 健康等级 PPBodyEnum.PPBodyfatErrorType

      PPBodyAssessment1(0),          //!< 健康存在隐患
      PPBodyAssessment2(1),          //!< 亚健康
      PPBodyAssessment3(2),          //!< 一般
      PPBodyAssessment4(3),          //!< 良好
      PPBodyAssessment5(4),          //!< 非常好
      PPBodyAssessmentError(-1);          //!< 参数错误

##### 1.1.7 身体类型 PPBodyFatModel.ppBodyType

     0 偏瘦型
     1 偏瘦肌肉型
     2 肌肉发达型
     3 缺乏运动型
     4 标准型
     5 标准肌肉型
     6 浮肿肥胖型
     7 偏胖肌肉型
     8 肌肉型偏胖

#### 1.2 设备对象PPDeviceModel 参数说明

    String deviceMac;//设备mac
    String deviceName;//设备蓝牙名称
    /**
     * 设备类型
     *
     * @see com.peng.ppscale.business.device.PPDeviceType.ScaleType
     * @deprecated
     */
    String scaleType;
    /**
     * 电量
     */
    int devicePower = -1;
    /**
     * 硬件版本号
     * @deprecated 
     */
    String firmwareVersion;
    /**
     * 设备类型
     *
     * @see PPScaleDefine.PPDeviceType
     */
    public PPScaleDefine.PPDeviceType deviceType;
    /**
     * 协议模式
     *
     * @see PPScaleDefine.PPDeviceProtocolType
     */
    public PPScaleDefine.PPDeviceProtocolType deviceProtocolType;
    /**
     * 计算方式
     *
     * @see PPScaleDefine.PPDeviceCalcuteType
     */
    public PPScaleDefine.PPDeviceCalcuteType deviceCalcuteType;
    /**
     * 精度
     *
     * @see PPScaleDefine.PPDeviceAccuracyType
     */
    public PPScaleDefine.PPDeviceAccuracyType deviceAccuracyType;
    /**
     * 供电模式
     *
     * @see PPScaleDefine.PPDevicePowerType
     */
    public PPScaleDefine.PPDevicePowerType devicePowerType;
    /**
     * 设备连接类型，用于必须直连的状态
     *
     * @see PPScaleDefine.PPDeviceConnectType
     */
    public PPScaleDefine.PPDeviceConnectType deviceConnectType;
    /**
     * 功能类型，可多功能叠加
     *
     * @see PPScaleDefine.PPDeviceFuncType
     */
    public int deviceFuncType;
    /**
     * 支持的单位
     *
     * @see PPScaleDefine.PPDeviceUnitType
     */
    public int deviceUnitType;
    /**
     * 是否能连接
     */
    public boolean deviceConnectAbled;

##### 1.2.1 PPScaleDefine.PPDeviceProtocolType 协议类型，具体说明

    PPDeviceProtocolTypeUnknow(0), //未知协议
    PPDeviceProtocolTypeV2(1) //使用V2.0蓝牙协议
    PPDeviceProtocolTypeV3(2), //使用V3.0蓝牙协议
    PPDeviceProtocolTypeTorre(3) //四电极、八电极协议

##### 1.2.2 PPScaleDefine.PPDeviceType 设备类型具体说明

    PPDeviceTypeUnknow(0), //未知
    PPDeviceTypeCF(1), //体脂秤
    PPDeviceTypeCE(2), //体重秤
    PPDeviceTypeCB(3), //婴儿秤
    PPDeviceTypeCA(4), //厨房秤
    PPDeviceTypeCC(5); //蓝牙wifi秤

##### 1.2.3 PPScaleDefine.PPDeviceAccuracyType 重量的精度类型具体说明

    PPDeviceAccuracyTypeUnknow(0), //未知精度
    PPDeviceAccuracyTypePoint01(1), //KG精度0.1
    PPDeviceAccuracyTypePoint005(2), //KG精度0.05
    PPDeviceAccuracyTypePointG(3), // 1G精度
    PPDeviceAccuracyTypePoint01G(4); // 0.1G精度

##### 1.2.4 PPScaleDefine.DeviceCalcuteType 体脂计算类型具体说明

    PPDeviceCalcuteTypeUnknow(0), //未知
    PPDeviceCalcuteTypeInScale(1), //秤端计算
    PPDeviceCalcuteTypeDirect(2), //直流
    PPDeviceCalcuteTypeAlternate(3), //交流
    PPDeviceCalcuteTypeNeedNot(4) //不需要计算

##### 1.2.5 PPScaleDefine.PPDevicePowerType 供电模式具体说明

    PPDevicePowerTypeUnknow(0),//未知
    PPDevicePowerTypeBattery(1),//电池供电
    PPDevicePowerTypeSolar(2),//太阳能供电
    PPDevicePowerTypeCharge(3);  //充电款

##### 1.2.6 PPScaleDefine.PPDeviceFuncType 功能类型，可多功能叠加,具体说明

    PPDeviceFuncTypeWeight(0x01),//体重
    PPDeviceFuncTypeFat(0x02), //测体脂
    PPDeviceFuncTypeHeartRate(0x04), //心率
    PPDeviceFuncTypeHistory(0x08), //历史数据
    PPDeviceFuncTypeSafe(0x10), //安全模式，孕妇模式
    PPDeviceFuncTypeBMDJ(0x20);  //闭幕单脚

##### 1.2.7 PPScaleDefine.PPDeviceUnitType 支持的单位,具体说明（暂时未启用）

    PPDeviceUnitTypeKG(0x01),//kg
    PPDeviceUnitTypeLB(0x02),//lb
    PPDeviceUnitTypeST(0x04),//st
    PPDeviceUnitTypeJin(0x08), //斤
    PPDeviceUnitTypeSTLB(0x10);//st:lb


### VI .蓝牙状态监控回调和系统蓝牙状态回调

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

### VII .闭目单脚模式

###### 闭目单脚模式

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
// 停止扫描切断开闭目单脚的设备
-  ppScale.exitBMDJModel();
```

### 1.3 监听闭目单脚状态

```
(interface)PPBMDJStatesInterface
```

### 1.4 发送指令使设备进入闭目单脚模式

```
// 闭目单脚设备进入准备状态
- (void)enterBMDJModel()
```

### 1.5 在回调函数中返回闭目单脚站立的时间

```
/// 设备退出闭目单脚状态
- (interface)PPBMDJDataInterface;
```

### VIII .蓝牙操作相关

#### 1.1 预留蓝牙操作对象

    BluetoothClient client = ppScale.getBleClient();

#### 1.2 停止扫描

    ppScale.stopSearch();

#### 1.3 断开设备连接

     ppScale.disConnect();

最后你需要在离开页面的之前调用stopSearch方法。 具体的实现请参考Demo中BindingDeviceActivity和ScaleWeightActivity中的代码。

### IX. [版本更新说明](doc/version_update.md)

### X. 使用的第三方库

#### 1、芯片方案商提供的体脂计算库

#### 2、[bluetoothkit1.4.0 蓝牙库](https://github.com/dingjikerbo/Android-BluetoothKit)

### XI. [常见问题](doc/common_problem.md)

Contact Developer： Email: yanfabu-5@lefu.cc

   
   
   
