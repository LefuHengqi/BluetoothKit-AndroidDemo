//
//  PPBluetoothPeripheralApple.m
//  PPBluetoothKit
//
//  Created by 彭思远 on 2023/3/31.
//

#import "PPBluetoothPeripheralApple.h"
//#import "PPLog.h"
#import "PPScaleFormatTool.h"
#import "PPBluetoothCMDApple.h"
#import "PPBluetoothAppleDataAnalysis.h"
#import <PPBaseKit/PPBaseKit.h>

#define kBLE_SEND_SLEEP_TIME_INTERVAL 100000


// 服务UUID
static NSString *const kBK3432ServiceUUID = @"F000FFC0-0451-4000-B000-000000000000";
// 特征UUID
static NSString *const kCharacteristicFFC1 = @"F000FFC1-0451-4000-B000-000000000000";
static NSString *const kCharacteristicFFC2 = @"F000FFC2-0451-4000-B000-000000000000";


@interface PPBluetoothPeripheralApple()<CBPeripheralDelegate>




@property (nonatomic, strong) CBCharacteristic *fff1Characteristic;

@property (nonatomic, strong) CBUUID *cb180ALastUUID;

@property (nonatomic, copy) void(^deviceInfoResponseHandler)(PPBluetooth180ADeviceModel *deviceModel);

@property (nonatomic, strong) PPBluetooth180ADeviceModel *device180A;

@property (nonatomic, strong) NSData *lastData;

@property (nonatomic, strong) NSString *filterServiceUUIDString;

@property (nonatomic, copy) void(^receiveHandler)(NSData* receiveData);

@property (nonatomic, copy) void(^syncTimeHandler)(NSInteger status);

@property (nonatomic, copy) void(^deleteHistoryHandler)(NSInteger status);

@property (nonatomic, copy) void(^deleteWIFIHandler)(NSInteger status);

@property (nonatomic, copy) void(^mtuHandler)(NSInteger status);
@property (nonatomic, copy) void(^updateCAHandler)(NSInteger errorCode, PPCAInfoModel *caInfo);
@property (nonatomic, copy) void(^fetchCAHandler)(NSInteger errorCode, PPCAInfoModel *caInfo);

@property (nonatomic, copy) dispatch_block_t delayedTask;

// dfu
@property (nonatomic, strong) CBCharacteristic *characteristicFFC1;
@property (nonatomic, strong) CBCharacteristic *characteristicFFC2;
@property (nonatomic, strong) NSData *firmwareData;
@property (nonatomic, assign) NSInteger currentBlock;
@property (nonatomic, assign) NSInteger totalBlocks;
@property (nonatomic, assign) float dfuProgress;
@property (nonatomic, strong) void (^dfuUpdateHandler)(CGFloat progress, PPDFUState state);
@property (nonatomic, assign) BOOL isDfuUpdating;
@property (nonatomic, assign) NSInteger mtu;

@property (nonatomic, assign) NSInteger totalPackages;      // 总包数
@property (nonatomic, assign) NSInteger currentPackage;      // 当前包数
@property (nonatomic, strong) NSMutableData *packageDatas; // 最终数据
@property (nonatomic, assign) Byte currentCmd;         // 当前命令字


@end

@implementation PPBluetoothPeripheralApple

#pragma mark - Public

- (instancetype)initWithPeripheral:(CBPeripheral *)peripheral  andDevice:(PPBluetoothAdvDeviceModel *)device{
    
    self = [super init];
    if (self){
        _deviceAdv = device;
        _peripheral = peripheral;
        _peripheral.delegate = self;
        _mtu = 20;
    }
    return self;
}


- (void)discoverDeviceInfoService:(void(^)(PPBluetooth180ADeviceModel *deviceModel))deviceInfoResponseHandler{
    
    self.deviceInfoResponseHandler = deviceInfoResponseHandler;
    
    self.device180A = [[PPBluetooth180ADeviceModel alloc] init];
    CBUUID *filterCB = [CBUUID UUIDWithString:@"180A"];
    self.filterServiceUUIDString = filterCB.UUIDString;
    [self.peripheral discoverServices:@[filterCB]];
}

- (void)discoverFFF0Service{
    
    self.lastData = nil;
    
    CBUUID *filterCB = [CBUUID UUIDWithString:@"FFF0"];
    CBUUID *filterBK3432 = [CBUUID UUIDWithString:kBK3432ServiceUUID];
    self.filterServiceUUIDString = filterCB.UUIDString;
    [self.peripheral discoverServices:@[filterBK3432, filterCB]];
}

- (void)fetchDeviceBatteryInfo{
    
    CBUUID *filterCB = [CBUUID UUIDWithString:@"180F"];
    self.filterServiceUUIDString = filterCB.UUIDString;
    [self.peripheral discoverServices:@[filterCB]];
}


- (void)fetchDeviceHistoryData{
    
    [self writeData:@"F200" reciveHandler:nil];
}

- (void)deleteDeviceHistoryDataWithHandler:(void (^)(NSInteger))handler {
    self.deleteHistoryHandler = handler;
    [self writeData:@"F201" reciveHandler:nil];
}

- (void)syncDeviceTimeWithHandler:(void (^)(NSInteger))handler {
    self.syncTimeHandler = handler;
    
    [self cancelDelayTaskIfNeed];
    
    __weak typeof(self) weakSelf = self;
    self.delayedTask = dispatch_block_create(0, ^{
        
        PP_Log(@"同步时间 超时了");
        if (weakSelf.syncTimeHandler) { // 失败
            weakSelf.syncTimeHandler(1);
            weakSelf.syncTimeHandler = nil;
        }
    });
    
    // 1s没有回复，表示 同步 失败
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), self.delayedTask);

    
    NSString *cmd = [PPBluetoothCMDApple syncTimeCMDWithDevice180AModel:self.device180A];
    [self writeData:cmd reciveHandler:nil];

}

/// 同步时间-指定时区
- (void)syncDeviceTimeWithZone:(PPZoneType)zoneType handler:(void(^)(NSInteger status))handler {
    self.syncTimeHandler = handler;
    
    [self cancelDelayTaskIfNeed];
    
    __weak typeof(self) weakSelf = self;
    self.delayedTask = dispatch_block_create(0, ^{
        
        PP_Log(@"同步时间 超时了");
        if (weakSelf.syncTimeHandler) { // 失败
            weakSelf.syncTimeHandler(1);
            weakSelf.syncTimeHandler = nil;
        }
    });
    
    // 1s没有回复，表示 同步 失败
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1.0 * NSEC_PER_SEC)), dispatch_get_main_queue(), self.delayedTask);

    
    NSString *cmd = [PPBluetoothCMDApple syncTimeCMDWithZone:zoneType];
    [self writeData:cmd reciveHandler:nil];
}

- (void)cancelDelayTaskIfNeed {
    if (self.delayedTask) {
        
        dispatch_block_cancel(self.delayedTask);
        self.delayedTask = nil;
    }
}

- (void)syncDeviceSetting:(PPBluetoothDeviceSettingModel *)settingModel;
{
    
    NSString *cmd = [PPBluetoothCMDApple syncInfoCmdWithSettingModel:settingModel device180AModel:self.device180A andAdvDeviceModel:self.deviceAdv];
    [self writeData:cmd reciveHandler:nil];
}


- (void)changeDNS:(NSString *)dns withHandler:(void(^)(NSInteger statu))handler {
    
    
    if (handler == nil){
        return;
    }
    PP_Log(@"域名传参:%@", dns);
    NSString *domain = dns;

    if ([domain hasPrefix:@"http://"]) {
        domain = [domain stringByReplacingOccurrencesOfString:@"http://" withString:@""];
    }

    NSArray *codes = [PPBluetoothCMDApple changeDNS:domain];
    
    for (NSData *code in codes) {
        
        NSString *codeStr = [PPScaleFormatTool data2String:code];

        [self writeData:codeStr reciveHandler:^(NSData *reciveData) {
            
            NSString *reciveStr = [PPScaleFormatTool data2String:reciveData];

            if ([reciveStr isEqualToString:@"f800"]){
                
                handler(0);
            }
            
            if ([reciveStr isEqualToString:@"f801"]){
                
                handler(1);
            }
        }];
     
    }
}

- (void)configNetWorkWithSSID:(NSString *)ssid password:(NSString *)password handler:(void(^)(NSString *sn, PPBluetoothAppleWifiConfigState configState))handler {
    if (handler == nil){
        return;
    }
    
    NSArray *codes = [PPBluetoothCMDApple configWifiCMDBySSID:ssid andPassword:password];
    for (NSData *code in codes) {
        
        NSString *codeStr = [PPScaleFormatTool data2String:code];

        [self writeData:codeStr reciveHandler:^(NSData *reciveData) {
            
            NSString *reciveStr = [PPScaleFormatTool data2String:reciveData];

            if ([reciveStr hasPrefix:@"06"]){
            
                NSData *d = [reciveData subdataWithRange:NSMakeRange(1, reciveData.length - 1)];
                NSString *sn  =[[NSString alloc] initWithData:d encoding:NSUTF8StringEncoding];
                
                handler(sn, PPBluetoothAppleWifiConfigStateSuccess);
                
            } else if ([reciveStr hasPrefix:@"0c"] && reciveData.length > 1) {

                NSData *err = [reciveData subdataWithRange:NSMakeRange(reciveData.length - 1, 1)];
                Byte *errorByte = (Byte *)[err bytes];
                
                NSInteger errorCode = errorByte[0];
                if (errorCode > 10) {
                    errorCode = errorCode - 10;
                }
                
                PPBluetoothAppleWifiConfigState wifiState = PPBluetoothAppleWifiConfigStateOtherFail;
                switch (errorCode) {
                    case 1:
                        wifiState = PPBluetoothAppleWifiConfigStateLowBatteryLevel;
                        break;
                        
                    case 3:
                        wifiState = PPBluetoothAppleWifiConfigStateRegistFail;
                        break;
                        
                    case 5:
                        wifiState = PPBluetoothAppleWifiConfigStateUnableToFindRouter;
                        break;
                    case 6:
                        wifiState = PPBluetoothAppleWifiConfigStatePasswordError;
                        break;
                        
                    default:
                        wifiState = PPBluetoothAppleWifiConfigStateOtherFail;
                        break;
                }
                
                

                handler(nil, wifiState);
            }
        }];
     
    }
}

- (void)configNetWorkWithSSID:(NSString *)ssid password:(NSString *)password withHandler:(void(^)(NSString *sn))handler{
    
    if (handler == nil){
        return;
    }
    
    __weak typeof(self) weakSelf = self;
    
    NSArray *codes = [PPBluetoothCMDApple configWifiCMDBySSID:ssid andPassword:password];
    for (NSData *code in codes) {
        
        NSString *codeStr = [PPScaleFormatTool data2String:code];

        [self writeData:codeStr reciveHandler:^(NSData *reciveData) {
            
            NSString *reciveStr = [PPScaleFormatTool data2String:reciveData];

            if ([reciveStr hasPrefix:@"06"]){
                weakSelf.receiveHandler = nil;
            
                NSData *d = [reciveData subdataWithRange:NSMakeRange(1, reciveData.length - 1)];
                NSString *sn  =[[NSString alloc] initWithData:d encoding:NSUTF8StringEncoding];
                handler(sn);
            }
        }];
     
    }
}

/// 查询wifi参数
/// - Parameter handler:
//    Health Scale7 已配网
//    PP:🙂2023.10.25 14:20:51:writeData:--- {length = 2, bytes = 0xf500}
//    PP:🙂2023.10.25 14:20:51:handleReciveData - {length = 9, bytes = 0x0a0100044954333213}
//    PP:🙂2023.10.25 14:20:51:handleReciveData - {length = 14, bytes = 0x0b01000977687331323334353668}
//    PP:🙂2023.10.25 14:20:51:handleReciveData - {length = 17, bytes = 0x06425730314c4631303333303030303639}
        
//    Health Scale C24 已配网
//    PP:🙂2023.10.25 13:53:58:writeData:--- {length = 2, bytes = 0xf500}
//    PP:🙂2023.10.25 13:53:58:handleReciveData - {length = 9, bytes = 0x0a0100044954333213}
//    PP:🙂2023.10.25 13:53:58:handleReciveData - {length = 14, bytes = 0x0b01000977687331323334353668}
//    PP:🙂2023.10.25 13:53:58:handleReciveData - {length = 1, bytes = 0x0e}
//    PP:🙂2023.10.25 13:53:58:handleReciveData - {length = 2, bytes = 0xf500}
- (void)queryWifiConfigWithHandler:(void (^)(PPWifiInfoModel * _Nullable))handler{
    
    
    if (handler == nil) {
        
        return;
    }
    
    NSMutableArray* ssidArray = @[].mutableCopy;
    NSMutableArray* pwdArray = @[].mutableCopy;
    
    __weak typeof(self) weakSelf = self;
    
    [self writeData:@"F500" reciveHandler:^(NSData *reciveData) {
        
        NSString *code = [PPScaleFormatTool data2String:reciveData];
        
        if ([code isEqualToString:@"f501"]) {
            weakSelf.receiveHandler = nil;
            
            PPWifiInfoModel* model = [[PPWifiInfoModel alloc] init];
            handler(model);
        }
        if ([code isEqualToString:@"0e"] || [code hasPrefix:@"06"]) {
            weakSelf.receiveHandler = nil;
            
            PPWifiInfoModel* model = [[PPWifiInfoModel alloc] init];
            NSString* ssid = [self getStringFromArray:ssidArray];
            NSString* pwd = [self getStringFromArray:pwdArray];
            model.ssid = ssid;
            model.password = pwd;
            handler(model);
            
        }else{
            
            if ([code hasPrefix:@"0a"]) {
                
                NSString *ss = [code substringWithRange:NSMakeRange(4, code.length - 4)];
                [ssidArray addObject:ss];
            }
            
            if ([code hasPrefix:@"0b"]) {
                
                NSString *ss = [code substringWithRange:NSMakeRange(4, code.length - 4)];
                [pwdArray addObject:ss];
            }
        }
        
    }];

}

- (void)restoreFactoryWithHandler:(void(^)(void))handler{
    
    NSString *code = @"F901";
    
    [self writeData:code reciveHandler:^(NSData * _Nonnull reciveData) {
        
        NSString* resultString = [PPScaleFormatTool data2String:reciveData];
        
        if ([resultString isEqualToString:@"f901"]) {
            
            handler();
        }
    }];
}

/// 开启心率，部分设备支持
- (void)openHeartRateSwitchWithComplete:(void(^)(void))completion {
    PP_Log(@"开启心率");
    NSString *code = [PPBluetoothCMDApple heartRateCodeWithOpen:YES];
    [self writeData:code reciveHandler:^(NSData *reciveData) {
    }];
    
    if (!completion) {
        return;
    }
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.3 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        completion();
    });
}

/// 关闭心率，部分设备支持
- (void)closeHeartRateSwitchWithComplete:(void(^)(void))completion {
    PP_Log(@"关闭心率心率");
    NSString *code = [PPBluetoothCMDApple heartRateCodeWithOpen:NO];
    [self writeData:code reciveHandler:^(NSData *reciveData) {
    }];
    
    if (!completion) {
        return;
    }
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.3 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        completion();
    });
}

/// 查询心率开关状态，部分设备支持
/// - Parameter handler: 0-心率测量打开， 1-心率测量关闭
- (void)fetchHeartRateSwitch:(void(^)(NSInteger status))handler {
    PP_Log(@"查询心率开关状态");
    NSString *code = @"FDA000000000000000005D";
    [self writeData:code reciveHandler:^(NSData *reciveData) {
        if (reciveData.length < 11) {
            return;
        }
        Byte *bytes = (Byte *)[reciveData bytes];
        
        if (bytes[0] == 0xFD) {
            NSInteger retInt = bytes[3];
            if (handler) {
                handler(retInt);
            }
        }
    }];
}

#pragma mark - Dfu
#pragma mark 开始升级

- (void)startDfu:(NSString *)packagePath handler:(void(^)(CGFloat progress, PPDFUState state))handler {
    
    if (!handler) {
        PP_Log(@"回调为空");
        return;
    }
    
    if (!self.characteristicFFC1 || !self.characteristicFFC2) {
        
        PP_Log(@"FFC1或FFC2特征为空");
        handler(0, PPDFUStateError);
    }

    NSData *data = [NSData dataWithContentsOfFile:packagePath];
    self.firmwareData = data;

    // 发送固件包的前16字节
    if (self.firmwareData.length < 16) {
        PP_Log(@"固件包长度小于16");
        
        handler(0, PPDFUStateError);
        
        return;
    }
    
    self.dfuUpdateHandler = handler;
    
    self.isDfuUpdating = NO;
    
    NSData *headerData = [self.firmwareData subdataWithRange:NSMakeRange(0, 16)];
    PP_Log(@"开始发送前16byte");
    [self writeFFC1:headerData];
    
}

// 开始发送固件数据
- (void)startSendingFirmware {
    
    
    // 计算总块数 (每块16字节数据)
    self.totalBlocks = ceil(self.firmwareData.length / 16.0);
    self.currentBlock = 0;
    PP_Log(@"开始发送数据，总块:%ld", self.totalBlocks);
    
    [self sendNextBlock];
    
//    // 创建GCD定时器
//    dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0);
//    self.sendTimer = dispatch_source_create(DISPATCH_SOURCE_TYPE_TIMER, 0, 0, queue);
//
//    if (self.sendTimer) {
//        // 设置定时器：15ms间隔，允许3ms误差
//        uint64_t interval = 15 * NSEC_PER_MSEC;
//        uint64_t leeway = 3 * NSEC_PER_MSEC;
//        dispatch_source_set_timer(self.sendTimer,
//                                 dispatch_time(DISPATCH_TIME_NOW, 0),
//                                 interval,
//                                 leeway);
//
//        __weak typeof(self) weakSelf = self;
//        dispatch_source_set_event_handler(self.sendTimer, ^{
//            // 确保在主线程执行蓝牙操作
//            dispatch_async(dispatch_get_main_queue(), ^{
//                [weakSelf sendNextBlock];
//            });
//        });
//
//        // 启动定时器
//        dispatch_resume(self.sendTimer);
//    }
}

// 发送下一个数据块
- (void)sendNextBlock {
    // 检查是否所有块都已发送
    if (self.currentBlock >= self.totalBlocks) {
        
        PP_Log(@"所有块数据发送完毕，currentBlock:%ld totalBlocks:%ld", self.currentBlock, self.totalBlocks);

        if (self.dfuUpdateHandler) {
            self.dfuUpdateHandler(self.dfuProgress, PPDFUStateCompleted);
        }
        
        return;
    }
    

    
    [self sendBlock:self.currentBlock];
    self.currentBlock++;
    
    // 更新进度
    self.dfuProgress = 1.0 * self.currentBlock / self.totalBlocks;
    
    PP_Log(@"进度:%f", self.dfuProgress);
    
    if (self.dfuUpdateHandler) {
        self.dfuUpdateHandler(self.dfuProgress, PPDFUStateSendingData);
    }
    
}

// 发送指定块
- (void)sendBlock:(NSInteger)blockIndex {
    // 计算数据偏移量
    NSInteger dataOffset = blockIndex * 16;
    if (dataOffset >= self.firmwareData.length) {
        
        PP_Log(@"发送完毕，dataOffset:%ld 总长度:%ld", dataOffset, self.firmwareData.length);
        
        if (self.dfuUpdateHandler) {
            self.dfuUpdateHandler(self.dfuProgress, PPDFUStateCompleted);
        }

        return;
    }
    
    // 计算实际数据长度
    NSInteger dataLength = MIN(16, self.firmwareData.length - dataOffset);
    
    // 创建数据包 (2字节块索引 + 16字节数据)
    NSMutableData *packet = [NSMutableData dataWithCapacity:18];
    
    // 添加块索引 (小端序)
    uint16_t blockIndexLittleEndian = CFSwapInt16HostToLittle((uint16_t)blockIndex);
    [packet appendBytes:&blockIndexLittleEndian length:2];
    
    // 添加数据
    NSData *blockData = [self.firmwareData subdataWithRange:NSMakeRange(dataOffset, dataLength)];
    [packet appendData:blockData];
    

    

    // 发送数据
    [self writeWithoutResponseFFC2:packet];
    
}



- (void)writeFFC1:(NSData *)packet {
    if (self.characteristicFFC1) {
        
        NSString *code = [PPScaleFormatTool data2String:packet];
        PP_Log(@"发送-FFC1:%@", code);
        [self.peripheral writeValue:packet forCharacteristic:self.characteristicFFC1 type:CBCharacteristicWriteWithResponse];
    } else {
        
        PP_Log(@"FFC1为空");
    }
    
}

- (void)writeWithoutResponseFFC2:(NSData *)packet {
    if (self.characteristicFFC2) {
        
        NSString *code = [PPScaleFormatTool data2String:packet];
        PP_Log(@"发送-FFC2:%@", code);
        [self.peripheral writeValue:packet forCharacteristic:self.characteristicFFC2 type:CBCharacteristicWriteWithoutResponse];
    } else {
        
        PP_Log(@"FFC2为空");
    }
    
}

// 处理ImgIdentify特征的通知
- (void)handleFFC1:(NSData *)data {
    if (data && data.length && !self.isDfuUpdating) {
        
        PP_Log(@"升级失败");
        
        if (self.dfuUpdateHandler) {
            self.dfuUpdateHandler(0, PPDFUStateError);
        }
    }
}

// 处理ImgBlock特征的通知
- (void)handleFFC2:(NSData *)data {
    if (data.length >= 2) {
        const uint8_t *bytes = data.bytes;
        uint16_t blockNumber = (bytes[0] << 8) | bytes[1];
        
        if (blockNumber == 0x0000) {
            if (self.isDfuUpdating) {
                
                PP_Log(@"升级中，收到0000，忽略");
                return;
            }
            
            self.isDfuUpdating = YES;
            
            // 开始发送固件包
            [self startSendingFirmware];
        } else if (blockNumber == 0xFFFF) {
            
            // 升级完成
            PP_Log(@"收到 0xFFFF");
//            [self cleanup];
        } else {
            
            // 请求重发特定块
            PP_Log(@"发送丢包块: %d", blockNumber);
            [self sendBlock:blockNumber];
        }
    }
}

- (void)cleanup {

    self.firmwareData = nil;
    self.currentBlock = 0;
    self.totalBlocks = 0;

}

- (void)enterInternalCodeModeWithComplete:(void(^)(void))completion {
    PP_Log(@"进入内码模式");
    [self writeData:@"FD340000000000000000C9" reciveHandler:^(NSData *reciveData) {
        
    }];
    
    if (!completion) {
        return;
    }
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        completion();
    });
}

- (void)exitInternalCodeModeWithComplete:(void(^)(void))completion {
    PP_Log(@"退出内码模式");
    [self writeData:@"FD350000000000000000C8" reciveHandler:^(NSData *reciveData) {
        
    }];
    
    if (!completion) {
        return;
    }
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        completion();
    });
}


#pragma mark - CBPeripheralDelegate

- (void)peripheral:(CBPeripheral *)peripheral didDiscoverServices:(NSError *)error
{
    for (CBService *service in peripheral.services) {
        
        PP_Log(@"已经发现服务%@",service);
        
        if ([service.UUID.UUIDString containsString:self.filterServiceUUIDString] || [service.UUID.UUIDString containsString:kBK3432ServiceUUID]){
            
            [peripheral discoverCharacteristics:nil forService:service];
        }
        
    }
}

- (void)peripheral:(CBPeripheral *)peripheral didDiscoverCharacteristicsForService:(CBService *)service error:(NSError *)error
{
    PP_Log(@"服务遍历-%@", service);
    
    if ([service.UUID isEqual:[CBUUID UUIDWithString:kBK3432ServiceUUID]]) {
        
        for (CBCharacteristic *characteristic in service.characteristics) {
            
            if ([characteristic.UUID isEqual:[CBUUID UUIDWithString:kCharacteristicFFC1]]) {
                
                self.characteristicFFC1 = characteristic;
                [peripheral setNotifyValue:YES forCharacteristic:characteristic];
            }
            if ([characteristic.UUID isEqual:[CBUUID UUIDWithString:kCharacteristicFFC2]]) {
                
                self.characteristicFFC2 = characteristic;
                [peripheral setNotifyValue:YES forCharacteristic:characteristic];
            }
        }
    }
    
    if ([service.UUID.UUIDString containsString:@"FFF0"]) {
        
        for (CBCharacteristic *characteristic in service.characteristics) {
            
            if ([characteristic.UUID.UUIDString containsString:@"FFF4"]) {
                
                [peripheral setNotifyValue:YES forCharacteristic:characteristic];
            }
            if ([characteristic.UUID.UUIDString containsString:@"FFF1"]) {

                self.fff1Characteristic = characteristic;
            }
        }
    }
    
    if ([service.UUID.UUIDString hasPrefix:@"180A"]) {
        
        self.cb180ALastUUID = service.characteristics.lastObject.UUID;
        
        for (CBCharacteristic *characteristic in service.characteristics) {
            [peripheral readValueForCharacteristic:characteristic];
        }
    }
    
    if ([service.UUID.UUIDString hasPrefix:@"180F"]) {
        
        for (CBCharacteristic *characteristic in service.characteristics) {
        
            if ([characteristic.UUID.UUIDString containsString:@"2A19"]) {
                
                [peripheral setNotifyValue:YES forCharacteristic:characteristic];
                [peripheral readValueForCharacteristic:characteristic];
            }
        }
    }
    
}

- (void)peripheral:(CBPeripheral *)peripheral didUpdateNotificationStateForCharacteristic:(CBCharacteristic *)characteristic error:(NSError *)error {
    
    PP_Log(@"didUpdateNotificationStateForCharacteristic");
    
    if ([characteristic.UUID isEqual:[CBUUID UUIDWithString:@"FFF4"]]) {
        
        if (self.serviceDelegate && [self.serviceDelegate respondsToSelector:@selector(discoverFFF0ServiceSuccess)]){
            
            [self.serviceDelegate discoverFFF0ServiceSuccess];
        }
    }

    
}

- (void)peripheral:(CBPeripheral *)peripheral didUpdateValueForCharacteristic:(CBCharacteristic *)characteristic error:(nullable NSError *)error{
    
    NSData *receiveData = characteristic.value;
    NSString *receiveStr = [PPScaleFormatTool data2String:receiveData];
    PP_Log(@"handleReciveData - %@", receiveStr);
    
    
    if (!receiveData) {
        
        return;
    }
    if ([characteristic.UUID isEqual:[CBUUID UUIDWithString:@"FFF4"]]){
        
        [self analysisFFF4DataForCharacteristic:characteristic];
        
    }else if ([characteristic.service.UUID isEqual:[CBUUID UUIDWithString:@"180F"]]){
        
        if ([characteristic.UUID.UUIDString containsString:@"2A19"]) {
            
            PPBatteryInfoModel * model = [PPBluetoothAppleDataAnalysis analysisStrengthWithData:receiveData];

            self.batteryInfo = model;
            
            self.deviceAdv.devicePower = model.power;
            
            PP_Log(@"电量:%ld", model.power);
            
            if(self.scaleDataDelegate && [self.scaleDataDelegate respondsToSelector:@selector(monitorBatteryInfoChange:advModel:)]){
                
                [self.scaleDataDelegate monitorBatteryInfoChange:model advModel:self.deviceAdv];
            }
        }else{
            
            PP_Log(@"未处理此特征--%@", characteristic);
        }
        
    }  else if ([characteristic.UUID.UUIDString isEqualToString:kCharacteristicFFC1]) {
        
        [self handleFFC1:characteristic.value];
    } else if ([characteristic.UUID.UUIDString isEqualToString:kCharacteristicFFC2]) {
        
        [self handleFFC2:characteristic.value];
    } else{
        
        [self analysis180ADataForCharacteristic:characteristic];
        
        if ([characteristic.UUID isEqual:self.cb180ALastUUID]){
            
            if (self.deviceInfoResponseHandler){
                
                PP_Log(@"获取到了180A服务");

                self.deviceInfoResponseHandler(self.device180A);
                self.deviceInfoResponseHandler = nil;
            }
        }
    }

}

- (void)peripheral:(CBPeripheral *)peripheral didWriteValueForCharacteristic:(CBCharacteristic *)characteristic error:(NSError *)error {
    if (error) {
        PP_Log(@"写入失败-%@", characteristic.UUID.UUIDString);
    } else {
        PP_Log(@"写入成功-%@", characteristic.UUID.UUIDString);
    }
}

- (void)peripheralIsReadyToSendWriteWithoutResponse:(CBPeripheral *)peripheral {
    PP_Log(@"peripheralIsReadyToSendWriteWithoutResponse");
    [self sendNextBlock];
}


#pragma mark - Private

- (void)analysisFFF4DataForCharacteristic:(CBCharacteristic *)characteristic{
    
    NSData *receiveData = characteristic.value;
    
    NSString *reciveStr = [PPScaleFormatTool data2String:receiveData];
    
//    PP_Log(@"handleReciveDataStr - %@", reciveStr);

    if ([reciveStr hasPrefix:@"cf"] ||
        [reciveStr hasPrefix:@"ce"]){
        
        [self handleFFF4ReciveData:receiveData];
        
    }else  if ([reciveStr hasPrefix:@"ef"]){
        
        [self handleFFF4ReciveData:receiveData];

        
    }else  if ([reciveStr hasPrefix:@"ff"]){

        if (self.deviceAdv.deviceCalcuteType == PPDeviceCalcuteTypeAlternate4_1) {
            
            
            
            PPBluetoothScaleBaseModel *scaleBaseModel = [PPBluetoothAppleDataAnalysis analysis20LengthDataWithCalcute4_1:receiveData deviceAdvModel:self.deviceAdv andDevice180AModel:self.device180A];
            
            if (self.scaleDataDelegate && [self.scaleDataDelegate respondsToSelector:@selector(monitorHistoryData:advModel:)] && scaleBaseModel.dateTimeInterval > ABNORMAL_HISTORY_INTERVAL_TIME) {
                
                [self.scaleDataDelegate monitorHistoryData:scaleBaseModel advModel:self.deviceAdv];
            }
            
            return;
        }
     
        
    }else if ([reciveStr isEqualToString:@"f100"]){
        
        [self cancelDelayTaskIfNeed];
        
        if (self.syncTimeHandler) { //成功
            self.syncTimeHandler(0);
            self.syncTimeHandler = nil;
        }
        
        if (self.cmdDelegate && [self.cmdDelegate respondsToSelector:@selector(syncDeviceTimeSuccess)]){
            
            [self.cmdDelegate syncDeviceTimeSuccess];
        }
    }else if ([reciveStr isEqualToString:@"f200"]){
        
        if (self.cmdDelegate && [self.cmdDelegate respondsToSelector:@selector(syncDeviceHistorySuccess)]){
            
            [self.cmdDelegate syncDeviceHistorySuccess];
        }
    }else if ([reciveStr isEqualToString:@"f201"]){
        
        if (self.deleteHistoryHandler) { // 成功
            self.deleteHistoryHandler(0);
            self.deleteHistoryHandler = nil;
        }
        
        if (self.cmdDelegate && [self.cmdDelegate respondsToSelector:@selector(deleteDeviceHistoryDataSuccess)]){
            
            [self.cmdDelegate deleteDeviceHistoryDataSuccess];
        }
    }else if ([reciveStr isEqualToString:@"f300"]){
        
        if (self.cmdDelegate && [self.cmdDelegate respondsToSelector:@selector(deviceWillDisconnect)]){
            
            [self.cmdDelegate deviceWillDisconnect];
        }
    }else if ([reciveStr isEqualToString:@"f400"]){
        
        if (self.deleteWIFIHandler) { // 成功
            self.deleteWIFIHandler(0);
            self.deleteWIFIHandler = nil;
        }
    }
    
    if ([reciveStr hasPrefix:@"0202"] && receiveData.length == 4 && self.mtuHandler) {
        
        Byte *testByte = (Byte *)[receiveData bytes];
        NSString *mtuStr = [NSString stringWithFormat:@"%02x%02x",testByte[3],testByte[2]];
        NSInteger mtu = [PPScaleFormatTool numberHexString:mtuStr];
        self.mtu = mtu;
        
        PP_Log(@"mtu:%ld", mtu);
        
        self.mtuHandler(0);
        self.mtuHandler = nil;
    }
    
//    F6+总包(1byte)+00首包(1byte)+命令(1byte)+数据总长度(2byte小端)+数据(xbyte)+异或校验(1byte,从F6开始)
//    F6+总包(1byte)+XX包号(1byte)+命令(1byte)+数据(xbyte)+异或校验(1byte,从F6开始)
    if ([reciveStr hasPrefix:@"f6"] && receiveData.length > 5) {
        Byte *testByte = (Byte *)[receiveData bytes];
        
        self.totalPackages = testByte[1];
        self.currentPackage = testByte[2];
        self.currentCmd = testByte[3];
        
        if (self.currentPackage == 0 && receiveData.length > 7) { // 首包
            self.packageDatas = [[NSMutableData alloc] init];
            NSData *data = [receiveData subdataWithRange:NSMakeRange(6, receiveData.length - 7)];
            [self.packageDatas appendData:data];
        } else {
            NSData *data = [receiveData subdataWithRange:NSMakeRange(4, receiveData.length - 5)];
            [self.packageDatas appendData:data];
        }
        
        if (self.totalPackages == self.currentPackage + 1) {
            NSData *contentData = self.packageDatas;
            self.packageDatas = nil;

            Byte *bytes = (Byte *)[contentData bytes];
            if (self.currentCmd == 0x01 && self.updateCAHandler) { // 更新CA
                
                NSInteger errorCode = bytes[0];
                if (errorCode == 0) {
                    PPCAInfoModel *info = [PPBluetoothAppleDataAnalysis analysisCAInfoWithData:contentData];
                    self.updateCAHandler(errorCode, info);
                } else {
                    self.updateCAHandler(errorCode, [[PPCAInfoModel alloc] init]);
                }
                
                self.updateCAHandler = nil;
                
            } else if (self.currentCmd == 0x02 && self.fetchCAHandler) { // 获取CA
                
                NSInteger errorCode = bytes[0];
                if (errorCode == 0) {
                    PPCAInfoModel *info = [PPBluetoothAppleDataAnalysis analysisCAInfoWithData:contentData];
                    self.fetchCAHandler(errorCode, info);
                } else {
                    self.fetchCAHandler(errorCode, [[PPCAInfoModel alloc] init]);
                }
                
                self.fetchCAHandler = nil;
            }
            
        }
        
    }

    
    if (self.receiveHandler) {
        
        self.receiveHandler(receiveData);
    }
    
    
}

- (void)analysis180ADataForCharacteristic:(CBCharacteristic *)characteristic{
    
    NSData *data = characteristic.value;
    
    if ([characteristic.UUID isEqual:[CBUUID UUIDWithString:@"2A29"]]){
        
        NSString *ss = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        self.device180A.manufacturerName = ss;
    }
    
    if ([characteristic.UUID isEqual:[CBUUID UUIDWithString:@"2A28"]]){
        
        NSString *ss = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        self.device180A.softwareRevision = ss;
    }
    
    if ([characteristic.UUID isEqual:[CBUUID UUIDWithString:@"2A27"]]){
        
        NSString *ss = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        self.device180A.hardwareRevision = ss;
    }
    
    if ([characteristic.UUID isEqual:[CBUUID UUIDWithString:@"2A26"]]){
        
        NSString *ss = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        self.device180A.firmwareRevision = ss;
    }
    
    if ([characteristic.UUID isEqual:[CBUUID UUIDWithString:@"2A25"]]){
        
        NSString *ss = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        self.device180A.serialNumber = ss;
    }
    
    if ([characteristic.UUID isEqual:[CBUUID UUIDWithString:@"2A24"]]){
        
        NSString *ss = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        self.device180A.modelNumber = ss;
    }
}


- (void)writeData:(NSString *)code reciveHandler:(void(^)(NSData *reciveData)) reciveHandler{
    
    if (self.fff1Characteristic) {
        
        if (reciveHandler != nil){
            
            self.receiveHandler = reciveHandler;
        }

        NSData *data = [PPScaleFormatTool convertHexStrToData:code];
        PP_Log(@"writeData:--- %@", code);
        [self.peripheral writeValue:data forCharacteristic:self.fff1Characteristic type:CBCharacteristicWriteWithResponse];
        usleep(kBLE_SEND_SLEEP_TIME_INTERVAL);
    }
}





- (void)handleFFF4ReciveData:(NSData *)reciveData{
    
    if ([self.lastData isEqual:reciveData]){
        return;
    }else{
        self.lastData = reciveData;
    }
    
    
    
    if (reciveData.length == 11) {
        
        PPBluetoothScaleBaseModel *scaleBaseModel = [PPBluetoothAppleDataAnalysis analysis11LengthData:reciveData deviceModel:self.deviceAdv];
        
        if (scaleBaseModel.isEnd) {
            
            if (self.scaleDataDelegate && [self.scaleDataDelegate respondsToSelector:@selector(monitorLockData:advModel:)]) {
                
                [self.scaleDataDelegate monitorLockData:scaleBaseModel advModel:self.deviceAdv];
            }
        }else{
            
            if (self.scaleDataDelegate && [self.scaleDataDelegate respondsToSelector:@selector(monitorProcessData:advModel:)]) {
                
                [self.scaleDataDelegate monitorProcessData:scaleBaseModel advModel:self.deviceAdv];
            }
        }
        
    }
    
    if (reciveData.length == 14) {
        
        
        PPBluetoothScaleBaseModel *scaleBaseModel = [PPBluetoothAppleDataAnalysis analysisEF14LengthData:reciveData deviceModel:self.deviceAdv];
    
        PP_Log(@"四电极双频阻抗:%ld 100阻抗:%ld",scaleBaseModel.impedance,scaleBaseModel.impedance100EnCode);
    
            
    }
    
    if (reciveData.length == 18 ||
        reciveData.length == 20){
        
        
        PPBluetoothScaleBaseModel *scaleBaseModel = [PPBluetoothAppleDataAnalysis analysis18LengthData:reciveData deviceAdvModel:self.deviceAdv andDevice180AModel:self.device180A];
        
        if (self.scaleDataDelegate && [self.scaleDataDelegate respondsToSelector:@selector(monitorHistoryData:advModel:)] && scaleBaseModel.dateTimeInterval > ABNORMAL_HISTORY_INTERVAL_TIME) {
            
            [self.scaleDataDelegate monitorHistoryData:scaleBaseModel advModel:self.deviceAdv];
        }
    }
}


- (NSString*)getStringFromArray:(NSArray*)dataArray{
    NSArray *sortedStringsArray = [dataArray sortedArrayUsingComparator:^NSComparisonResult(NSString *obj1, NSString *obj2) {
        // 获取前两个字符
        NSString *substr1 = [obj1 substringToIndex:2];
        NSString *substr2 = [obj2 substringToIndex:2];
        
        // 比较字符串
        return [substr1 compare:substr2];
    }];
    NSMutableString* logStr = [NSMutableString new];
    for (NSString* subString in sortedStringsArray) {
        if (subString.length > 4) {
            NSString* str = [subString substringFromIndex:4]; //去掉索引 + 长度
            str = [str substringToIndex:str.length - 2]; //去掉校验位
            [logStr appendString:str];
        }
    }
    NSData* data = [PPScaleFormatTool convertHexStrToData:logStr];
    return [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    
}

- (void)disconnectDevice {
    [self writeData:@"F300" reciveHandler:nil];
}

- (void)queryDeviceTime:(void(^)(NSString* deviceTime))handler{
    
    __weak typeof(self) weakSelf = self;
    
    NSString *cmd = @"F100";
    
    [self writeData:cmd reciveHandler:^(NSData * _Nonnull reciveData) {
        
        NSString* result = [PPScaleFormatTool data2String:reciveData];
        if ([result hasPrefix:@"f1"] && reciveData.length == 8) {
            weakSelf.receiveHandler = nil;
            
            Byte* reciveBytes = (Byte*)reciveData.bytes;
            int year = reciveBytes[1] << 8 | reciveBytes[2];
            int month = reciveBytes[3];
            int day = reciveBytes[4];
            int hour = reciveBytes[5];
            int mm = reciveBytes[6];
            int ss = reciveBytes[7];
            NSString* localString = [NSString stringWithFormat:@"%04d-%02d-%02d %02d:%02d:%02d", year, month, day, hour, mm, ss];
            
            NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
            dateFormatter.locale = [NSLocale systemLocale];
            dateFormatter.calendar = [[NSCalendar alloc] initWithCalendarIdentifier:NSCalendarIdentifierGregorian];
            [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
            NSDate *date = [dateFormatter dateFromString:localString];
            
            
            NSTimeZone* sourceTimeZone = [NSTimeZone timeZoneWithAbbreviation:@"UTC"];//或GMT
            
            //设置转换后的目标日期时区
            NSTimeZone* destinationTimeZone = [NSTimeZone localTimeZone];
            //得到源日期与世界标准时间的偏移量
            NSInteger sourceGMTOffset = [sourceTimeZone secondsFromGMTForDate:date];
            //目标日期与本地时区的偏移量
            NSInteger destinationGMTOffset = [destinationTimeZone secondsFromGMTForDate:date];
            //得到时间偏移量的差值
            NSTimeInterval interval = destinationGMTOffset - sourceGMTOffset;
            NSDate* destinationDateNow = [[NSDate alloc] initWithTimeInterval:interval sinceDate:date];
            NSString *str = [dateFormatter stringFromDate:destinationDateNow];
            
            NSString *deviceTimeStr = @"";
            if ([self.device180A.modelNumber isEqualToString:@"UTC-0"]) {
                deviceTimeStr = str;
            }else{
                deviceTimeStr = localString;
            }
            
            
            handler(deviceTimeStr);
        }
    }];
}

- (void)deleteWIFI:(void(^)(NSInteger status))handler {
    self.deleteWIFIHandler = handler;
    [self writeData:@"F400" reciveHandler:nil];
}

/// 查询配置的DNS，即服务器域名
- (void)queryDNSWithHandler:(void(^)(NSString* DNS))handler {
    
    __weak typeof(self) weakSelf = self;
    
    [self writeData:@"F501" reciveHandler:^(NSData *reciveData) {
        
        NSString *code = [PPScaleFormatTool data2String:reciveData];
        
        if ([code hasPrefix:@"f501"] && code.length > 8) {
            weakSelf.receiveHandler = nil;
            
            NSString *dataHex = [code substringFromIndex:8];
            NSData *hexData = [PPScaleFormatTool convertHexStrToData:dataHex];
            NSString *dnsStr = [[NSString alloc] initWithData:hexData encoding:NSUTF8StringEncoding];
            
            handler(dnsStr);
            
        }
        
    }];
}


- (void)startTestOTA {

    if (self.fff1Characteristic) {
        
        NSData *data =[@"AT+OTA" dataUsingEncoding:NSUTF8StringEncoding];
        PP_Log(@"OTA:%@", data);
        [self.peripheral writeValue:data forCharacteristic:self.fff1Characteristic type:CBCharacteristicWriteWithResponse];
    } else {
        PP_Log(@"写特征为空");
    }
    
}

- (void)startUserOTAWithHandler:(void(^)(NSInteger code))handler {

    __weak typeof(self) weakSelf = self;
    [self writeData:@"ef00" reciveHandler:^(NSData *reciveData) {

        NSString *code = [PPScaleFormatTool data2String:reciveData];
        
        if ([code hasPrefix:@"ef"] && code.length == 4) {
            weakSelf.receiveHandler = nil;
            
            NSString *dataHex = [code substringFromIndex:2];
            NSInteger hexNum = [PPScaleFormatTool numberHexString:dataHex];

            handler(hexNum);
            
        }
        
    }];
}

/// 更新MTU，部分设备支持
/// - Parameter handler: 0设置成功 1设置失败
- (void)codeUpdateMTU:(void(^)(NSInteger status))handler {
    if (handler == nil){
        return;
    }
    
    PP_Log(@"更新MTU");
    
    self.mtuHandler = handler;
    [self writeData:@"0200" reciveHandler:nil];
    
}

/// 更新CA证书，部分设备支持
/// - Parameter caContent: ca证书
/// - Parameter handler: 0设置成功 1设置失败
- (void)updateCAContent:(NSString *)caContent handler:(void(^)(NSInteger errorCode, PPCAInfoModel *caInfo))handler {
    if (self.mtu <= 20) {
        
        __weak typeof(self) weakSelf = self;
        [self codeUpdateMTU:^(NSInteger status) {
            [weakSelf updateCA:caContent handler:handler];
        }];
    } else {
        [self updateCA:caContent handler:handler];
    }
}

- (void)updateCA:(NSString *)caContent handler:(void(^)(NSInteger errorCode, PPCAInfoModel *caInfo))handler {
    if (!handler) {
        return;
    }
    
    self.updateCAHandler = handler;
    
    NSString *hexStr = [PPScaleFormatTool hexStringFromString:caContent];
    
    NSString *suf = @"0d0a00";
    if (![hexStr hasSuffix:suf]) {
        hexStr = [NSString stringWithFormat:@"%@%@", hexStr, suf];
    }
    
    PP_Log(@"CA-hex:%@", hexStr);
    
    NSArray *array = [PPBluetoothCMDApple splitBluetoothData:hexStr mtu:self.mtu cmd:0x01];
    for (NSData *c in array) {
        NSString *codeStr = [PPScaleFormatTool data2String:c];
        [self writeData:codeStr reciveHandler:^(NSData *reciveData) {
        }];
    }
}

- (void)fetchCAInfoWithHandler:(void(^)(NSInteger errorCode, PPCAInfoModel *caInfo))handler {
    if (!handler) {
        return;
    }
    
    if (self.mtu <= 20) {
        
        __weak typeof(self) weakSelf = self;
        [self codeUpdateMTU:^(NSInteger status) {
            [weakSelf getCAInfoWithHandler:handler];
        }];
    } else {
        [self getCAInfoWithHandler:handler];
    }
    
    
}

- (void)getCAInfoWithHandler:(void(^)(NSInteger errorCode, PPCAInfoModel *caInfo))handler {
    self.fetchCAHandler = handler;
    [self writeData:@"f60100020000f5" reciveHandler:^(NSData *reciveData) {
    }];
}

@end
