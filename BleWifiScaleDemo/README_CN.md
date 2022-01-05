
### 1. 简介
#### 1.1 运行环境
- iOS版本大于9.0，且需要真机运行。
- Android版本大于5.0，且需要真机运行。
    
[Android源码示例请点击](https://github.com/PPScale/ppscale-android-demo.git)

--- 
#### 1.2 一些名称的约定
- 蓝牙WiFi秤：蓝牙WiFi双模，可以通过蓝牙传输数据，在配网完成后可以通过路由器连接云服务器进行数据交换。
- 体重秤和体脂称：体重秤只有测量重量的能力，体脂秤除了测量体重还可以通过测量双脚之间的电阻计算出身体其他参数。
- 锁定数据：平稳的站立在秤面上后，秤面显示的数字会在变化后趋于一个固定值，在闪烁3次后显示的读数即为锁定数据。
- 测脂过程：秤面显示屏会有一字型的图案从上到下进行变化。
- 测脂数据：如果您使用的是体脂称，在锁定数据后秤会主动进行测脂，在完成这个过程后会得到测脂数据。
- 测脂失败：如果您使用的是体脂称，但是测量过程中没有看到测脂过程，请确认您是光脚站立并且脚底正常接触电极片。
- 设备配网：通过蓝牙下发路由器账号和密码信息的过程。

--- 
#### 1.3 开发流程
1、需要您或者您的团队完成服务端的代码运行，并生成一个域名地址, 给称和App使用，示例请参阅服务端的示例和协议文档;
2、蓝牙WiFi秤需要配置您的服务端的域名地址;
3、您需要修改NetUtil.java中url为您的服务器地址，与秤端地址对应

--- 
#### 1.4 demo使用方法

1、确保你完成了上面1.3里面的修改
2、编译运行BleWifiScaleDemo App
3、然后点击“绑定设备”，然后上秤完成称重，完成后，App会弹窗选择，您可以直接去配网，也可以稍后到设备列表页，去配网
4、“数据详情”可以查看您最后一组数据
5、“数据列表”可以查，秤端传给服务端的离线数据，需要服务端开发好相应的接口
6、“上秤称重”可以在您绑定完设备后，再次称重使用
7、“设备管理”可以查看已经绑定的设备列表
8、当你绑定了设备后，在“设备管理”页面可以看到该设备，并可以在“设备管理”页面配置网络


--- 
#### 1.5 开发这模式使用方法
1、在“设备管理”页, 也可以进入开发者模式，对秤端进行设置，进入开发者模式时，会发起蓝牙连接，连接状态在顶部标题栏会显示。
2、你可以获取当前秤端的SSID和Password、SN
3、你可以配置秤的服务器域名，分为Ip和域名两种，在你需要修改秤的服务器地址时，你可以使用这两个方法，配置一个即可
4、你可以清除秤上的已经配置的SSID和Password
5、重连是重新连接蓝牙。

---
#### 1.6 需要权限
在Android 6.0及以上系统版本，启动扫描前，需确保开启 
    1、定位权限  
    2、定位开关  
    3、蓝牙开关 

在清单文件加入以下权限

```
    <!-- 访问网络，网络定位需要上网-->
    <uses-permission android:name="android.permission.INTERNET" />
    <!-- wifi状态，在wifi配网时用到-->
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />

```

--- 
### 2. BleWifiScaleDemo中实现的场景

#### 2.1 如果您采购的是蓝牙版本的秤那么您的业务流程可能需要包含以下几个部分

##### 2.1.1 用户信息（对应DEMO中的UserModel.java）
- 需要提供用户的身高、年龄、性别。这三项信息在计算人体参数时的必要参数，如果您订购的设备是体重秤而非体脂秤，那这三项信息为非必须。
- 如果您的设备是蓝牙WiFi秤需要设置Base Url，这个值和秤端固件烧录的是相同的。目的是确保秤和App访问的是同一台服务器。

##### 2.1.2 设备绑定（对应DEMO中的BindingDeviceActivity.java）
- 如果您希望对设备进行管理可以加入此逻辑。此流程中当前会扫描附近所有LF衡器生产的蓝牙秤设备，一旦发现会尝试进行连接去获取秤端发出的数据，在接收到锁定数据之后会将设备信息储存在本地。

##### 2.1.2 蓝牙称重（对应DEMO中的BindingDeviceActivity.java）
- 在这个流程中，只会尝试去连接已储存在本地的设备（过滤条件使用的是蓝牙秤的MAC地址）

--- 

#### 2.2 如果您采购的是蓝牙Wifi版本的秤那么您的业务流程在蓝牙秤的基础上可能还需要包含以下几个部分

##### 2.2.1 设备配网（对应DEMO中的BleConfigWifiActivity.java）
- 在收到锁定数据后如果您的设备为蓝牙WiFi秤（逻辑上通过设备名称判断）会进入设备配网流程。此流程中尝试连接特定的MAC地址的设备，在连接成功后通过蓝牙向设备发送路由器的ssid和password，password可以为空，需要注意的是匹配的路由器不能是5G频段，这会导致配网失败。

##### 2.2.2 Wifi数据列表 （对应DEMO中的WifiDataListActivity.java）
- 已配网的蓝牙WiFi秤未进行蓝牙称重的状态下进行称重，在数据锁定后会将数据上传到指定的云服务器。在此流程中通过设备的SN（WiFi设备的唯一标识符）获取这台秤上传到服务器的所有重量数据。

--- 

#### 2.3 公共页面

##### 2.3.1 身体详情 （对应DEMO中的BodyDataDetailActivity.java）
- 蓝牙称重后会跳转到该页面，进行身体详情的展示。
- 点击“WiFi数据列表”页的数据，会跳转到该页面，使用当前的用户信息，带入计算方法中得出身体详情。

##### 2.3.2 设备列表（对应DEMO中的DeviceListActivity.java）
- 列表中展示的是在“设备绑定”中记录的设备。可以进行解绑和配网（如果是蓝牙WiFi秤）的操作；
- 解绑后的设备需要再次绑定，否则无法在蓝牙秤重页面使用。

--- 
### 3. 引入PPScale到工程中, gradle自动导入方式
1、在project项目目录下的build.gradle中加入
```
        allprojects {
            repositories {
                、、、
                 maven { url 'http://nexus.lefuenergy.com/repository/maven-public' }
            }
        }
```
2、在需要引入sdk的module下的build.gradle中加入
根据不同的分支请采用不同的artifactId，格式是：ppscale-分支名
下面是master分支的集成方式，已集成相应的so文件   
```
        dependencies {
            、、、
                implementation 'com.peng.ppscale:ppscale-new-master:0.0.4.10'     
        }
```
--- 
### 4. PPScale提供的方法

#### 4.1  体重、体脂秤相关方法

详情请参阅[蓝牙体重、体脂秤的文档](../README.md)
[Bluetooth weight and body fat scale documentation](../doc/README-en.md)

--- 
#### 4.2 WiFi配网的相关方法
蓝牙WiFi秤配网

- 传入待连接路由器的ssid和password,如果路由器没有设置密码password可以为空。
```
    /**
     * 参数配置 绑定时请确保WIFI是2.4G，并且账号密码正确
     *
     * @param password     WIFI密码
     * @parm ssid          WIFI账号  不可为空
     */
    private BleOptions getBleOptions() {
        ssid = etWifiName.getText().toString();
        return new BleOptions.Builder()
                .setFeaturesFlag(BleOptions.ScaleFeatures.FEATURES_CONFIG_WIFI)
                .setPassword(etWifiKey.getText().toString())
                .setSsid(ssid)
                .build();
    }
```

配网完成监听
```
      ProtocalFilterImpl protocalFilter = new ProtocalFilterImpl();
      protocalFilter.setConfigWifiInterface(new PPConfigWifiInterface())
```
启动配网
```
    List<String> devices = new ArrayList<>();
            devices.add(address);
            ppScale = new PPScale.Builder(this)
                    .setProtocalFilterImpl(protocalFilter)
                    .setBleOptions(getBleOptions())
                    .setDeviceList(devices)
                    .setBleStateInterface(bleStateInterface)
                    .build();
            ppScale.startSearchBluetoothScaleWithMacAddressList();
```

--- 
#### 4.3 状态监听代理方法
蓝牙状态的监听
```
PPBleStateInterface bleStateInterface = new PPBleStateInterface() {
    @Override
    public void monitorBluetoothWorkState(PPBleWorkState ppBleWorkState) {
        if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnected) {
            Logger.d("设备已连接");
        } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnecting) {
            Logger.d("设备连接中");
        } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateDisconnected) {
            Logger.d("设备已断开");
        } else if (ppBleWorkState == PPBleWorkState.PPBleStateSearchCanceled) {
            Logger.d("停止扫描");
        } else if (ppBleWorkState == PPBleWorkState.PPBleWorkSearchTimeOut) {
            Logger.d("扫描超时");
        } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateSearching) {
            Logger.d("扫描中");
        } else {
            Logger.e("蓝牙状态异常");
        }
    }

    @Override
    public void monitorBluetoothSwitchState(PPBleSwitchState ppBleSwitchState) {
        if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOff) {
            Logger.e("系统蓝牙断开");
            Toast.makeText(BleConfigWifiActivity.this, "系统蓝牙断开", Toast.LENGTH_SHORT).show();
        } else if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOn) {
            Logger.d("系统蓝牙打开");
            Toast.makeText(BleConfigWifiActivity.this, "系统蓝牙打开", Toast.LENGTH_SHORT).show();
        } else {
            Logger.e("系统蓝牙异常");
        }
    }
};

```

--- 
### 5.0 开发者模式
在设备列表页面，点击设置，可以进入开发者模式页面。
开发者模式提供了, 需要确保秤常亮  
```
    1、获取秤端的sn、ssid以及password功能
    2、修改秤端的网络Ip地址 （IP+Host）例如：
    3、修改秤端的网络DNS地址（域名），与修改IP是冲突的，只设置其中一个就可以，建议设置DNS
    4、清除SSID，秤会恢复到出厂设置
    5、重连，是在秤与手机蓝牙断开时使用
```

--- 
### 6. 蓝牙WiFi秤的常见问题

#### 6.1 配网为什么只能使用2.4G频段？
- 是硬件规格导致的，我们使用的WiFi芯片只有2.4G频段。


#### 6.2 混合模式的路由器可以进行配网么？
- 答：可以，2.4G和5G的混合模式其实是路由器信号不停的在2.4G和5G之间切换，由于切换的频率非常快，所以可以同时使用两种频段。秤可以在搜索到2.4G频段的情况下进行配网。（提供了配网兼容性报告，所有的路由器是在默认模式下进行测试）


#### 6.3 秤通过WiFi上传数据的时机是什么时候？
- 答：每次测量完锁定数据后。

#### 6.4 秤端的历史数据是怎样产生的？
- 答：秤重锁定后在蓝牙未连接且未能成功上传服务器（又分上传失败和未配网两种状态）的情况下会，当前这组称重的锁定数据会存为历史存储在秤体内（最多存100组，超出后采用先进先出的原则）。

#### 6.5 秤端的历史数据是怎么样传输的？
- 答：数据的上传分为两种情况：第一种通过蓝牙，当前秤没能连接到路由器的情况下 若App通过蓝牙连接上秤并下发了获取历史数据指令，那秤端将会将存储的历史数据通过蓝牙告知App且不会在传输完成后主动删除，需等待App下发删除指令，会将所有历史数据抹除。第二种通过WiFi，当前秤之前已经配过网，但是由于某种原因数据没有能够及时上传，那么会在下次测量完锁定数据后尝试连接匹配的路由器并上传本次称重和所有历史数据。

#### 6.6 App和蓝牙WiFi秤配网和数据上传的业务流程是怎样的？

```
sequenceDiagram
App->>Scale: 扫描附近设备
Scale->>App: 对外发送蓝牙广播
App->>Scale: 发现目标设备，开始连接
Scale->>App: 连接成功
App->>Scale: 发送 SSID,Password
Scale->>App: 回复SN
Scale->>Server:发送SN
Server->>Scale:SN已存储
App->>Server: 发送SN，UID
Server->>App:将SN和UID进行绑定
Server->>App:配网成功
Scale->>Server:上报称重数据（SN,Weight,impedance）
Server->>App:通过SN找到匹配的UID，将称重数据发送出去（Weight, impedance）
```
#### 5.7 OTA的过程是怎么样的？

 ```
sequenceDiagram
Scale->>Server: 查询最新的固件版本（将本地固件的版本号作为参数上传）
Server->>Scale: 判断是否需要升级
Server->>Scale: 如果需要升级（返回固件的下载路径）
Scale->>Server: 从固件下载路径开始下载
Scale->>Server: 升级成功后显示PASS 
```

Contact Developer：
Email: yanfabu-5@lefu.cc

