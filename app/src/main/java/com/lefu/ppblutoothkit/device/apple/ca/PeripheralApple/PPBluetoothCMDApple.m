//
//  PPBluetoothCMDApple.m
//  PPBluetoothKit
//
//  Created by 彭思远 on 2023/4/3.
//

#import "PPBluetoothCMDApple.h"
#import "PPScaleFormatTool.h"
#import "PPBluetoothInsideDefine.h"
//#import "PPLog.h"
#import <PPBaseKit/PPBaseKit.h>

#define kBLE_SEND_MAX_LEN 16

@implementation PPBluetoothCMDApple

+ (NSString *)syncTimeCMDWithDevice180AModel:(PPBluetooth180ADeviceModel *)device180A{
    
    NSMutableString *timeHexStr = [[NSMutableString alloc] init];
    [timeHexStr appendString:@"F1"];
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    formatter.locale = [NSLocale systemLocale];
    formatter.calendar = [[NSCalendar alloc] initWithCalendarIdentifier:NSCalendarIdentifierGregorian];
    [formatter setDateFormat:@"yyyy MM dd HH mm ss"];
    NSInteger zone = 8;
    if (device180A.modelNumber) {
        NSArray *arr = [device180A.modelNumber componentsSeparatedByString:@"-"];
        if (arr.count == 2) {
            NSString *str = arr.lastObject;
            if ([str isEqualToString:@"0"]) {
                zone = 0;
            }else{
                zone = 8;
            }
        }else{
            zone = 8;
        }
    }else{
        zone = 8;
    }
    
    if (zone == 0) {
        
        [formatter setTimeZone:[NSTimeZone timeZoneWithAbbreviation:@"UTC"]];
    }else{

        [formatter setTimeZone:[NSTimeZone systemTimeZone]];
    }
    NSDate *datenow = [NSDate date];
    NSString *currentTimeString = [formatter stringFromDate:datenow];
    PP_Log(@"currentTimeString =  %@",currentTimeString);
    NSArray *array = [currentTimeString componentsSeparatedByString:@" "];
    for (NSString *timeComponent in array) {
        [timeHexStr appendString:[PPScaleFormatTool getHexByDecimal:[timeComponent integerValue]]];
    }
    PP_Log(@"timeHexStr = %@",timeHexStr);
    return timeHexStr;
}

+ (NSString *)syncTimeCMDWithZone:(PPZoneType)zoneType {
    
    NSMutableString *timeHexStr = [[NSMutableString alloc] init];
    [timeHexStr appendString:@"F1"];
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    formatter.locale = [NSLocale systemLocale];
    formatter.calendar = [[NSCalendar alloc] initWithCalendarIdentifier:NSCalendarIdentifierGregorian];
    [formatter setDateFormat:@"yyyy MM dd HH mm ss"];
    
    if (zoneType == PPZoneTypeUTC) {
        
        [formatter setTimeZone:[NSTimeZone timeZoneWithAbbreviation:@"UTC"]];
    }else{

        [formatter setTimeZone:[NSTimeZone systemTimeZone]];
    }
    NSDate *datenow = [NSDate date];
    NSString *currentTimeString = [formatter stringFromDate:datenow];
    PP_Log(@"currentTimeString =  %@",currentTimeString);
    NSArray *array = [currentTimeString componentsSeparatedByString:@" "];
    for (NSString *timeComponent in array) {
        [timeHexStr appendString:[PPScaleFormatTool getHexByDecimal:[timeComponent integerValue]]];
    }
    PP_Log(@"timeHexStr = %@",timeHexStr);
    return timeHexStr;
}

+ (NSString *)syncInfoCmdWithSettingModel:(PPBluetoothDeviceSettingModel *)settingModel device180AModel:(PPBluetooth180ADeviceModel *)device180A andAdvDeviceModel:(PPBluetoothAdvDeviceModel *)deviceModel{
    
    if (deviceModel.deviceCalcuteType == PPDeviceCalcuteTypeInScale) {
        
        NSData *dataClose = nil;
        Byte bytes[8];
        bytes[0] = 0xFE;
        bytes[1] = 00;
        
        if (settingModel.gender == PPDeviceGenderTypeMale){
            bytes[2] = 0x01;

        }
        if (settingModel.gender == PPDeviceGenderTypeFemale){
            bytes[2] = 0x00;

        }
        bytes[3] = 0x00;
        bytes[4] = [PPScaleFormatTool convertDecimalTo0x:settingModel.height];
        bytes[5] = [PPScaleFormatTool convertDecimalTo0x:settingModel.age];
        
        if (settingModel.unit == PPUnitKG){
            
            bytes[6] = 0x01;
        }
        
        if (settingModel.unit == PPUnitLB){
            
            bytes[6] = 0x02;
        }
        
        
        if (settingModel.unit == PPUnitJin){
            
            bytes[6] = 0x01;
        }
   
        Byte crc=bytes[1];
        for (int i=2; i<7; i++) {
            crc = crc^bytes[i];
        }
        bytes[7] = crc;
        dataClose = [[NSData alloc] initWithBytes:bytes length:8];
        return [PPScaleFormatTool data2String:dataClose] ;
    }
    
    if (deviceModel.deviceProtocolType == PPDeviceProtocolTypeV2) {
        NSData *dataClose = nil;
        
        Byte bytes[11];
        bytes[0] = 0xFD;
        if (settingModel.isPregnantMode) {
            bytes[1] = 0x38;
        }else{
            if (deviceModel.deviceFuncType & PPDeviceFuncTypeWifi) {
                if ([device180A.modelNumber isEqualToString:@"UTC-0"]) {
                    
                    bytes[1] = 0x37;

                }else{
                    
                    bytes[1] = 0x00;
                }

            }else{
                bytes[1] = 0x37;
            }
        }
        
        if (settingModel.unit == PPUnitKG){
            
            bytes[2] = 0x00;
        }
        
        if (settingModel.unit == PPUnitLB){
            
            bytes[2] = 0x01;
        }
        
        if (settingModel.unit == PPUnitST){
            
            if ([deviceModel.deviceName isEqualToString:kBLEDeviceHealthScale3]) {
                bytes[2] = 0x02;
            }else{
                bytes[2] = 0x0b;
            }
        }
        
        if (settingModel.unit == PPUnitJin){
            
            bytes[2] = 0x03;
        }
        
        if (settingModel.unit == PPUnitSTLB){
            
            bytes[2] = 0x02;
        }
        
        if(bytes[1] != 0x37){
            bytes[3] = settingModel.userIndex;

        }else{
            bytes[3] = 0x00;
        }
        
        if (settingModel.isAthleteMode) {
            bytes[4] = 0x02;
        }else{
            bytes[4] = 0x00;
        }
        if (settingModel.gender == PPDeviceGenderTypeMale) {
            bytes[5] = 0x01;
        }else{
            bytes[5] = 0x00;
        }
        bytes[6] = [PPScaleFormatTool convertDecimalTo0x:settingModel.age];
        bytes[7] = [PPScaleFormatTool convertDecimalTo0x:settingModel.height];
        
        if(bytes[1] != 0x37){
            // 方法1：转换为16位整数（适合0-655.35范围，精度0.01）
            uint16_t weightValue = (uint16_t)(settingModel.currentWeight * 100); // 乘以100以保留两位小数
            bytes[8] = (Byte)(weightValue >> 8);    // 高字节
            bytes[9] = (Byte)(weightValue & 0xFF);  // 低字节
        }
        
        Byte crc=bytes[0];

        for (int i=1; i<10; i++) {
            crc = crc^bytes[i];
        }
        bytes[10] = crc;
        dataClose = [[NSData alloc] initWithBytes:bytes length:11];
        return [PPScaleFormatTool data2String:dataClose] ;
    }
    
    if (deviceModel.deviceProtocolType == PPDeviceProtocolTypeV3) {
        NSData *dataClose = nil;
        
        Byte bytes[11];
        bytes[0] = 0xFD;
        if (settingModel.isPregnantMode) {
            bytes[1] = 0x38;
        }else{
            if (deviceModel.deviceFuncType & PPDeviceFuncTypeWifi) {
                bytes[1] = 0x00;

            }else{
                bytes[1] = 0x37;
            }
        }
        
        if (settingModel.unit == PPUnitKG){
            
            bytes[2] = 0x00;
        }
        
        if (settingModel.unit == PPUnitLB){
            
            bytes[2] = 0x01;
        }
        
        if (settingModel.unit == PPUnitST){
            
            bytes[2] = 0x0b;
        }
        
        if (settingModel.unit == PPUnitJin){
            
            bytes[2] = 0x03;
        }
        
        if (settingModel.unit == PPUnitSTLB){
            
            bytes[2] = 0x02;
        }

        bytes[3] = 0x0;
        if (settingModel.isAthleteMode) {
            bytes[4] = 0x02;
        }else{
            bytes[4] = 0x00;
        }
        if (settingModel.gender == PPDeviceGenderTypeMale) {
            bytes[5] = 0x01;
        }else{
            bytes[5] = 0x00;
        }
        bytes[6] = [PPScaleFormatTool convertDecimalTo0x:settingModel.age];
        bytes[7] = [PPScaleFormatTool convertDecimalTo0x:settingModel.height];
        bytes[8] = 0x0;
        bytes[9] = 0x0;
        Byte crc=bytes[0];
        for (int i=1; i<10; i++) {
            crc = crc^bytes[i];
        }
        bytes[10] = crc;
        dataClose = [[NSData alloc] initWithBytes:bytes length:11];
        return [PPScaleFormatTool data2String:dataClose] ;
    }
    
    return @"";
}

+ (NSArray *)changeDNS:(NSString *)dns{
    
    Byte bytes1[2];
    bytes1[0] = 0xF8;
    bytes1[1] = 0x00;
    // 组装包头
    NSData *preData = [[NSData alloc] initWithBytes:bytes1 length:2];
    // 把用户输入的ip转换成16进制的字符串
    NSString *ipHEX =  [PPScaleFormatTool hexStringFromString:dns];
    // 把16进制的ip字符串转换为二进制的字节流
    NSData *ipData= [PPScaleFormatTool convertHexStrToData:ipHEX];
    // 把二进制字节流的ip长度转换成16进制，再转换而二进制字节流
    NSInteger len = ipData.length;
    NSData *ipLen = [PPScaleFormatTool convertHexStrToData:[PPScaleFormatTool getHexByDecimal:len]];
    // 把二进制字节流的ip进行crc校验，得到二进制字节流格式的校验码
    UInt8 bytes[len];
    memcpy(bytes, ipData.bytes, len);
    Byte crc=bytes[0];
    for (int i=1; i<len; i++) {
        crc = crc^bytes[i];
    }
    
    Byte crcbytes[1];
    crcbytes[0] = crc;
    NSData *crcData = [[NSData alloc] initWithBytes:crcbytes length:1];

    // 拼接
    NSMutableData *msgData = [[NSMutableData alloc] init];
    // 拼接包头
    [msgData appendData:preData];
    // 拼接ip长度 `
    [msgData appendData:ipLen];
    // 拼接校验码
    [msgData appendData:crcData];
    // 拼接ip
    [msgData appendData:ipData];
    
    // 分包发送 最大的包长为20
    NSMutableArray *commands = @[].mutableCopy;
    for (int i = 0; i < [msgData length]; i += kBLE_SEND_MAX_LEN) {
        // 预加 最大包长度，如果依然小于总数据长度，可以取最大包数据大小
        if ((i + kBLE_SEND_MAX_LEN) < [msgData length]) {
            NSString *rangeStr = [NSString stringWithFormat:@"%i,%i", i, kBLE_SEND_MAX_LEN];
            NSData *subData = [msgData subdataWithRange:NSRangeFromString(rangeStr)];
            [commands addObject:subData];
 
        }
        else {
            NSString *rangeStr = [NSString stringWithFormat:@"%i,%i", i, (int)([msgData length] - i)];
            NSData *subData = [msgData subdataWithRange:NSRangeFromString(rangeStr)];
            [commands addObject:subData];
        }
    }
    return  commands;
}

+ (NSArray *)configWifiCMDBySSID:(NSString *)ssid andPassword:(NSString *)password{
        
    NSData *dataSSID = [PPScaleFormatTool convertHexStrToData:[PPScaleFormatTool hexStringFromString:ssid]];
    NSInteger ssidLen = dataSSID.length;
    NSMutableArray *ssidArr = @[].mutableCopy;
    for (int i = 0; i < ssidLen; i += kBLE_SEND_MAX_LEN) {
        if ((i + kBLE_SEND_MAX_LEN) < ssidLen) {
            NSString *rangeStr = [NSString stringWithFormat:@"%i,%i", i, kBLE_SEND_MAX_LEN];
            NSData *subData = [dataSSID subdataWithRange:NSRangeFromString(rangeStr)];
            PP_Log(@"-----------\n %@  \n----------",subData);
            [ssidArr addObject:subData];
            
        }else {
            NSString *rangeStr = [NSString stringWithFormat:@"%i,%i", i, (int)(ssidLen - i)];
            NSData *subData = [dataSSID subdataWithRange:NSRangeFromString(rangeStr)];
            PP_Log(@"-----------\n %@  \n----------",subData);
            [ssidArr addObject:subData];
        }
    }
    
    
    NSMutableArray *passwordArr = @[].mutableCopy;
    NSData *dataPassword = [PPScaleFormatTool convertHexStrToData:[PPScaleFormatTool hexStringFromString:password]];
    NSInteger passwordLen = dataPassword.length;

    for (int i = 0; i < passwordLen; i += kBLE_SEND_MAX_LEN) {
        if ((i + kBLE_SEND_MAX_LEN) < passwordLen) {
            NSString *rangeStr = [NSString stringWithFormat:@"%i,%i", i, kBLE_SEND_MAX_LEN];
            NSData *subData = [dataPassword subdataWithRange:NSRangeFromString(rangeStr)];
            PP_Log(@"-----------\n %@  \n----------",subData);
            [passwordArr addObject:subData];
            
        }else {
            NSString *rangeStr = [NSString stringWithFormat:@"%i,%i", i, (int)(passwordLen - i)];
            NSData *subData = [dataPassword subdataWithRange:NSRangeFromString(rangeStr)];
            PP_Log(@"-----------\n %@  \n----------",subData);
            [passwordArr addObject:subData];
        }
    }
    
    NSMutableArray *codeArr = @[].mutableCopy;
    [ssidArr enumerateObjectsUsingBlock:^(NSData*  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        NSString *totalHex = [PPScaleFormatTool getHexByDecimal:ssidArr.count];
        NSString *numHex = [PPScaleFormatTool getHexByDecimal:idx];
        NSString *lenHex = [PPScaleFormatTool getHexByDecimal:ssidLen];

        NSString *c = [NSString stringWithFormat:@"0a%@%@%@", totalHex,numHex, lenHex];
        NSData *data = [PPScaleFormatTool convertHexStrToData:c];
        NSMutableData *mData = [data mutableCopy];
        [mData appendData:obj];
        
        uint16_t res = [PPBluetoothCMDApple crc16:mData];
        [mData appendBytes:&res length:1];
        [codeArr addObject:mData];
        
    }];
    
    [passwordArr enumerateObjectsUsingBlock:^(NSData*  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        NSString *totalHex = [PPScaleFormatTool getHexByDecimal:passwordArr.count];
        NSString *numHex = [PPScaleFormatTool getHexByDecimal:idx];
        NSString *lenHex = [PPScaleFormatTool getHexByDecimal:passwordLen];

        NSString *c = [NSString stringWithFormat:@"0b%@%@%@", totalHex,numHex, lenHex];
        NSData *data = [PPScaleFormatTool convertHexStrToData:c];
        NSMutableData *mData = [data mutableCopy];
        [mData appendData:obj];
        
        uint16_t res = [PPBluetoothCMDApple crc16:mData];
        [mData appendBytes:&res length:1];
        [codeArr addObject:mData];
    }];
    
    
    [codeArr addObject:[PPScaleFormatTool convertHexStrToData:@"0E"]];
    return codeArr;
}

+ (uint16_t)crc16:(NSData*) data
{
    unsigned int crc;
    crc = 0xFFFF;
    uint8_t byteArray[[data length]];
    [data getBytes:&byteArray length:data.length];
    crc = byteArray[0];
    for (int i = 1; i<[data length]; i++) {
        Byte byte = byteArray[i];
        crc = crc^byte;
    }
    return crc;
}

+ (NSString *)heartRateCodeWithOpen:(BOOL)open {
    NSData *data = nil;
    
    Byte bytes[11];
    bytes[0] = 0xFD;
    if (open) {
        bytes[1] = 0x44;
    } else {
        bytes[1] = 0x43;
    }
    bytes[2] = 0x00;
    bytes[3] = 0x00;
    bytes[4] = 0x00;
    bytes[5] = 0x00;
    bytes[6] = 0x00;
    bytes[7] = 0x00;
    bytes[8] = 0x00;
    bytes[9] = 0x00;
    
    
    Byte crc=bytes[0];

    for (int i=1; i<10; i++) {
        crc = crc^bytes[i];
    }
    bytes[10] = crc;
    data = [[NSData alloc] initWithBytes:bytes length:11];
    return [PPScaleFormatTool data2String:data] ;
}

+ (NSArray<NSData *> *)splitBluetoothData:(NSString *)contentStr mtu:(NSInteger)mtuLen cmd:(Byte)cmd {
    
    // ---- 1. 十六进制字符串转 NSData ----
    NSData *payloadData = [PPScaleFormatTool convertHexStrToData:contentStr];
    if (payloadData.length == 0) {
        PP_Log(@"[BLE] 数据为空");
        return @[];
    }
    
    // ---- 2. 计算每包能承载的最大数据字节数 ----
    // 首包固定开销: F6(1) + 总包(1) + 00(1) + 命令(1) + 总长度(2) + 校验(1) = 7 字节
    NSInteger firstPackageMaxData = mtuLen - 7;
    // 续包固定开销: F6(1) + 总包(1) + 包号(1) + 命令(1) + 校验(1) = 5 字节
    NSInteger otherPackageMaxData = mtuLen - 5;
    
    if (firstPackageMaxData <= 0 || otherPackageMaxData <= 0) {
        PP_Log(@"[BLE] MTU 太小，无法容纳协议头");
        return @[];
    }
    
    NSUInteger totalDataLen = payloadData.length;
    
    // ---- 3. 计算总包数 ----
    NSInteger remain = totalDataLen - firstPackageMaxData;
    NSInteger totalPackages = 1;
    if (remain > 0) {
        totalPackages += (remain + otherPackageMaxData - 1) / otherPackageMaxData; // 向上取整
    }
    
    if (totalPackages > 255) {
        PP_Log(@"[BLE] 数据过长，总包数超过 255");
        return @[];
    }
    
    // ---- 4. 逐包组装 ----
    NSMutableArray<NSData *> *packages = [NSMutableArray array];
    NSUInteger offset = 0;
    
    for (int pkgIndex = 0; pkgIndex < totalPackages; pkgIndex++) {
        NSMutableData *package = [NSMutableData data];
        
        // ---- 协议头 ----
        Byte header[4];
        header[0] = 0xF6;                    // 帧头
        header[1] = (Byte)totalPackages;     // 总包数
        header[2] = (Byte)pkgIndex;          // 包号（首包 0x00）
        header[3] = cmd;                     // 命令字
        [package appendBytes:header length:4];
        
        // ---- 首包追加数据总长度（2字节小端）----
        if (pkgIndex == 0) {
            Byte lenBytes[2];
            lenBytes[0] = (Byte)(totalDataLen & 0xFF);         // 低字节
            lenBytes[1] = (Byte)((totalDataLen >> 8) & 0xFF);  // 高字节
            [package appendBytes:lenBytes length:2];
        }
        
        // ---- 追加本包数据 ----
        NSUInteger dataLenForThisPkg = (pkgIndex == 0) ?
            MIN(firstPackageMaxData, totalDataLen - offset) :
            MIN(otherPackageMaxData, totalDataLen - offset);
        
        NSData *subData = [payloadData subdataWithRange:NSMakeRange(offset, dataLenForThisPkg)];
        [package appendData:subData];
        offset += dataLenForThisPkg;
        
        // ---- 计算异或校验（从 F6 开始，到数据末尾）----
        Byte xor = 0;
        const Byte *bytes = (const Byte *)package.bytes;
        for (NSUInteger i = 0; i < package.length; i++) {
            xor ^= bytes[i];
        }
        [package appendBytes:&xor length:1];
        
        // ---- 加入数组 ----
        [packages addObject:package];
        
        PP_Log(@"[BLE] 第 %d 包，长度 %lu 字节", pkgIndex, (unsigned long)package.length);
    }
    
    PP_Log(@"[BLE] 拆包完成，共 %lu 包", (unsigned long)packages.count);
    return packages;
}

@end
