//
//  PPBluetoothPeripheralApple.h
//  PPBluetoothKit
//
//  Created by 彭思远 on 2023/3/31.
//

#import <Foundation/Foundation.h>
#import <CoreBluetooth/CoreBluetooth.h>
#import "PPBluetoothAdvDeviceModel.h"
#import "PPBluetooth180ADeviceModel.h"
#import "PPBluetoothInterface.h"
//#import "PPBluetoothDeviceSettingModel.h"
#import "PPWifiInfoModel.h"
#import <PPBaseKit/PPBaseKit.h>
#import "PPCAInfoModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface PPBluetoothPeripheralApple : NSObject

@property (nonatomic, weak) id<PPBluetoothServiceDelegate> serviceDelegate;

@property (nonatomic, weak) id<PPBluetoothCMDDataDelegate> cmdDelegate;

@property (nonatomic, weak) id<PPBluetoothScaleDataDelegate> scaleDataDelegate;

@property (nonatomic, strong) CBPeripheral *peripheral;

@property (nonatomic, strong) PPBatteryInfoModel *batteryInfo;

@property (nonatomic, strong) PPBluetoothAdvDeviceModel *deviceAdv;

- (instancetype)initWithPeripheral:(CBPeripheral *)peripheral  andDevice:(PPBluetoothAdvDeviceModel *)device;

- (void)discoverDeviceInfoService:(void(^)(PPBluetooth180ADeviceModel *deviceModel))deviceInfoResponseHandler;

- (void)discoverFFF0Service;

- (void)fetchDeviceHistoryData;

/// 删除历史数据
///  - status 0 : 成功，1 : 失败
- (void)deleteDeviceHistoryDataWithHandler:(void(^)(NSInteger status))handler;

/// 同步时间
///  - status 0 : 成功，1 : 失败
- (void)syncDeviceTimeWithHandler:(void(^)(NSInteger status))handler;

/// 同步时间-指定时区
/// 使用该方法前，请确认设备的时区
///  - status 0 : 成功，1 : 失败
- (void)syncDeviceTimeWithZone:(PPZoneType)zoneType handler:(void(^)(NSInteger status))handler;

- (void)fetchDeviceBatteryInfo;

- (void)syncDeviceSetting:(PPBluetoothDeviceSettingModel *)settingModel;


/// 恢复出厂状态
/// - Parameter handler:
- (void)restoreFactoryWithHandler:(void(^)(void))handler;

/// 配网
/// - Parameters:
///   - model: name 和 pwd
///   - handler:
- (void)configNetWorkWithSSID:(NSString *)ssid password:(NSString *)password handler:(void(^)(NSString *sn, PPBluetoothAppleWifiConfigState configState))handler;

/// 配网
/// 此方法即将过期，使用 - (void)configNetWorkWithSSID:(NSString *)ssid password:(NSString *)password handler:(void(^)(NSString *sn, PPBluetoothAppleWifiConfigState configState))handler 代替
/// - Parameters:
///   - model: name 和 pwd
///   - handler:
- (void)configNetWorkWithSSID:(NSString *)ssid password:(NSString *)password withHandler:(void(^)(NSString *sn))handler API_DEPRECATED("Use - (void)configNetWorkWithSSID:(NSString *)ssid password:(NSString *)password handler:(void(^)(NSString *sn, PPBluetoothAppleWifiConfigState configState))handler instead", ios(1.0, API_TO_BE_DEPRECATED), visionos(1.0, API_TO_BE_DEPRECATED));

/// 设置DNS，即服务器域名
/// - Parameters:
///   - dns: 域名
///   - handler:
- (void)changeDNS:(NSString *)dns withHandler:(void(^)(NSInteger statu))handler;

/// 查询wifi参数
/// - Parameter handler:
- (void)queryWifiConfigWithHandler:(void (^)(PPWifiInfoModel * _Nullable))handler;

/// 断开设备蓝牙连接
- (void)disconnectDevice;

/// 查询设备时间
- (void)queryDeviceTime:(void(^)(NSString* deviceTime))handler;

/// 删除Wi-Fi参数
///  status 0：成功，1：失败
- (void)deleteWIFI:(void(^)(NSInteger status))handler;


/// 查询配置的DNS，即服务器域名
/// 部分机型不支持该功能
- (void)queryDNSWithHandler:(void(^)(NSString* DNS))handler;

/// 通过WIFI进行OTA（开发专用，用于工厂模式OTA）
/// 注：调用此方法前请确保设备已经配网，并且部分机型不支持该功能
- (void)startTestOTA;

/// 通过WIFI进行OTA
/// 注：调用此方法前请确保设备已经配网，并且部分机型不支持该功能
/// code:  0 接收成功，启动OTA，1 接收失败 没有配ssid 退出OTA，2 电量不足 退出OTA，3 充电中 退出OTA
- (void)startUserOTAWithHandler:(void(^)(NSInteger code))handler;

/// DFU升级，部分设备支持
- (void)startDfu:(NSString *)packagePath handler:(void(^)(CGFloat progress, PPDFUState state))handler;
/// 进入内码模式，部分设备支持
- (void)enterInternalCodeModeWithComplete:(void(^)(void))completion;
/// 退出内码模式，部分设备支持
- (void)exitInternalCodeModeWithComplete:(void(^)(void))completion;

/// 开启心率，部分设备支持
- (void)openHeartRateSwitchWithComplete:(void(^)(void))completion;

/// 关闭心率，部分设备支持
- (void)closeHeartRateSwitchWithComplete:(void(^)(void))completion;

/// 查询心率开关状态，部分设备支持
/// - Parameter handler: 0-心率测量打开， 1-心率测量关闭
- (void)fetchHeartRateSwitch:(void(^)(NSInteger status))handler;


/// 更新CA证书，部分设备支持
/// - Parameter caContent: ca证书内容
/// - Parameter handler:
///       - errorCode: 0设置成功  1异或校验失败，2包序错误，3长度错误，4证书校验失败，5证书有效期异常
///       - caInfo: ca信息
- (void)updateCAContent:(NSString *)caContent handler:(void(^)(NSInteger errorCode, PPCAInfoModel *caInfo))handler;


/// 获取CA证书信息，部分设备支持
/// - Parameter handler:
///       - errorCode: 0设置成功  1异或校验失败，2无证书
///       - caInfo: ca信息
- (void)fetchCAInfoWithHandler:(void(^)(NSInteger errorCode, PPCAInfoModel *caInfo))handler;



@end

NS_ASSUME_NONNULL_END
