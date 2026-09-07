//
//  PPBluetoothAppleDataAnalysis.m
//  PPBluetoothKit
//
//  Created by 彭思远 on 2023/4/3.
//

#import "PPBluetoothAppleDataAnalysis.h"
#import "PPScaleFormatTool.h"
#import "PPBluetoothInsideDefine.h"
//#import "PPLog.h"
#import <PPBaseKit/PPBaseKit.h>
#import "PPBatteryInfoModel.h"
static NSInteger staticImpedance = 0;
static NSInteger staticWeightKg = 0;
static NSInteger static100Impedance = 0;

@implementation PPBluetoothAppleDataAnalysis

+ (PPBluetoothScaleBaseModel *)analysisEF14LengthData:(NSData *)receiveData deviceModel:(PPBluetoothAdvDeviceModel *)device {
    PPBluetoothScaleBaseModel *model = [[PPBluetoothScaleBaseModel alloc] init];

    
    if (receiveData.length == 14) {
        
        
    
        
        UInt8 bytes[14];
        memcpy(bytes, receiveData.bytes, 14);
        
        if (bytes[0] == 0xEF) {
            
            
            NSString *impedanceStr1 = [NSString stringWithFormat:@"%02x%02x%02x%02x", bytes[8], bytes[7], bytes[6], bytes[5]];
            NSInteger impedance1 = [PPScaleFormatTool numberHexString:impedanceStr1];
            
            NSString *impedanceStr2 = [NSString stringWithFormat:@"%02x%02x%02x%02x", bytes[12], bytes[11], bytes[10], bytes[9]];
            NSInteger impedance2 = [PPScaleFormatTool numberHexString:impedanceStr2];

                staticImpedance = impedance1;
                model.impedance  = impedance1;
                static100Impedance = impedance2;
                model.impedance100EnCode = impedance2;
            
        }
    }
    
    return model;
}

+ (PPBluetoothScaleBaseModel *)analysis11LengthData:(NSData *)receiveData deviceModel:(PPBluetoothAdvDeviceModel *)device {
    
    UInt8 bytes[11];
    memcpy(bytes, receiveData.bytes, 11);
    
    PPBluetoothScaleBaseModel *model = [[PPBluetoothScaleBaseModel alloc] init];
    model.isPlus = YES;
    
    int weightInt = (int)(bytes[4]*256+bytes[3]);
    if (weightInt == 65535) {
        model.isOverload = YES;
    }
    model.weight = weightInt;
    
  
    if (device.deviceCalcuteType == PPDeviceCalcuteTypeAlternate4_1) {
        
        
        if (bytes[9]==0x08) {
         
            PP_Log(@"重量稳定开始测量阻抗");
            
            staticWeightKg = weightInt;

            model.impedance = 0;
            model.isEnd = NO;
            model.isHeartRating = NO;
            model.heartRate = 0;

        }
        
        if (bytes[9]==0x03) {
            
            PP_Log(@"开始测量心率");
            
            model.isHeartRating = YES;


        }
    }

    if (bytes[9]==0x01) {
        
        staticImpedance = 0;
        staticWeightKg = 0;
        
        model.impedance = 0;
        model.isEnd = NO;
        model.isHeartRating = NO;
        model.heartRate = 0;
    }
    
    if(bytes[9]==0x00){
        
        NSInteger impedance = (bytes[7]*256*256+bytes[6]*256+bytes[5]);

        Byte b2 = bytes[2] & 0xC0;
        
        
        if (b2 == 0x80){
            
            PP_Log(@"正在测量心率");
            staticImpedance = impedance;
            staticWeightKg = weightInt;
            model.impedance = 0;
            model.isEnd = NO;
            model.isHeartRating = YES;
            model.heartRate = 0;
            
        }
        
        if (b2 == 0xC0){
            
            int heartRate = (int)(bytes[1]);
            model.weight = staticWeightKg;
            model.impedance = staticImpedance;
            model.impedance100EnCode = static100Impedance;
            model.isEnd = YES;
            model.isHeartRating = NO;
            model.heartRate = heartRate;
            
            PP_Log(@"心率测量完成");
        }

        if (b2 == 0x00){
            
            model.weight = weightInt;
            model.impedance = impedance;
            model.isEnd = YES;
            model.isHeartRating = NO;
            model.heartRate = 0;
            
            if (staticImpedance > 0) {
                model.impedance = staticImpedance;

            }
            
            if (static100Impedance > 0) {
                model.impedance100EnCode = static100Impedance;

            }
            
            PP_Log(@"称重完成");
            
        }

        NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
        dateFormatter.locale = [NSLocale systemLocale];
        dateFormatter.calendar = [[NSCalendar alloc] initWithCalendarIdentifier:NSCalendarIdentifierGregorian];
        [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
        model.dateStr = [dateFormatter stringFromDate:[NSDate date]];
    }
    
    if (bytes[9] == 0xa0){

        NSInteger impedance = (bytes[7]*256*256+bytes[6]*256+bytes[5]);

        if (impedance > 1200) {
            impedance = impedance / 10;
        }

        model.weight = weightInt;
        model.impedance = impedance;
        model.isEnd = YES;
        model.isHeartRating = NO;
        model.heartRate = 0;
        NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
        dateFormatter.locale = [NSLocale systemLocale];
        dateFormatter.calendar = [[NSCalendar alloc] initWithCalendarIdentifier:NSCalendarIdentifierGregorian];
        [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
        model.dateStr = [dateFormatter stringFromDate:[NSDate date]];
    }

    if (bytes[8]==0x00) {
        model.unit = PPUnitKG;
    }
    if (bytes[8]==0x01) {
        model.unit = PPUnitLB;
    }
    if (bytes[8]==0x02) {
        model.unit = PPUnitSTLB;
    }
    if (bytes[8]==0x0b) {
        model.unit = PPUnitST;
    }
    if (bytes[8]==0x03) {
        model.unit = PPUnitJin;
    }

    
    return model;
}

+ (PPBluetoothScaleBaseModel *)analysis20LengthDataWithCalcute4_1:(NSData *)receiveDate deviceAdvModel:(PPBluetoothAdvDeviceModel *)device andDevice180AModel:(PPBluetooth180ADeviceModel *)device180A  {
    
    
    PPBluetoothScaleBaseModel *model = [[PPBluetoothScaleBaseModel alloc] init];
    UInt8 bytes[20];
    memcpy(bytes, receiveDate.bytes, 20);
    
    
    int year = (int)(bytes[4] *256 + bytes[5]);
    int mounth = (int)(bytes[6]);
    int day = (int)(bytes[7]);
    int hour = (int)(bytes[8]);
    int minite = (int)(bytes[9]);
    int secound = (int)(bytes[10]);
    NSString *localString = [NSString stringWithFormat:@"%02d-%02d-%02d %02d:%02d:%02d",year,mounth,day,hour,minite,secound];
    
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
    if ([device180A.modelNumber isEqualToString:@"UTC-0"]) {
        model.dateStr = str;
    }else{
        model.dateStr = localString;
    }
    int weightInt = (int)(bytes[3]*256+bytes[2]);
    model.weight = weightInt;
    
    NSInteger heartRate = (int)(bytes[11]);
    model.heartRate = heartRate;
    
    
    NSString *impedanceStr1 = [NSString stringWithFormat:@"%02x%02x%02x%02x", bytes[15], bytes[14], bytes[13], bytes[12]];
    NSInteger impedance1 = [PPScaleFormatTool numberHexString:impedanceStr1];
    
    NSString *impedanceStr2 = [NSString stringWithFormat:@"%02x%02x%02x%02x", bytes[19], bytes[18], bytes[17], bytes[16]];
    NSInteger impedance2 = [PPScaleFormatTool numberHexString:impedanceStr2];

        
    model.impedance  = impedance1;
    
    model.impedance100EnCode = impedance2;
   
    model.isEnd = NO;
    
    return model;
}

+ (PPBluetoothScaleBaseModel *)analysis18LengthData:(NSData *)receiveDate deviceAdvModel:(PPBluetoothAdvDeviceModel *)device andDevice180AModel:(PPBluetooth180ADeviceModel *)device180A  {
    
    PPBluetoothScaleBaseModel *model = [[PPBluetoothScaleBaseModel alloc] init];
    UInt8 bytes[18];
    memcpy(bytes, receiveDate.bytes, 18);
    
    
    int year = (int)(bytes[11] *256 + bytes[12]);
    int mounth = (int)(bytes[13]);
    int day = (int)(bytes[14]);
    int hour = (int)(bytes[15]);
    int minite = (int)(bytes[16]);
    int secound = (int)(bytes[17]);
    NSString *localString = [NSString stringWithFormat:@"%02d-%02d-%02d %02d:%02d:%02d",year,mounth,day,hour,minite,secound];
    
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
    if ([device180A.modelNumber isEqualToString:@"UTC-0"]) {
        model.dateStr = str;
    }else{
        model.dateStr = localString;
    }
    if (bytes[9]==0x01) {
        int weightInt = (int)(bytes[4]*256+bytes[3]);
        model.weight = weightInt;
    }
    else if(bytes[9]==0x00){
        //体脂类声明
        int weightInt = (int)(bytes[4]*256+bytes[3]);
        NSInteger impedance = (bytes[7]*256*256+bytes[6]*256+bytes[5]);
        model.weight = weightInt;
        model.impedance = impedance;
        if (bytes[2] == 0xc0) {
            NSInteger heartRate = (int)(bytes[1]);
            model.heartRate = heartRate;
        }
    }
    model.isEnd = NO;
    
    return model;
}


+ (PPBatteryInfoModel *)analysisStrengthWithData:(NSData *)receiveData{
    
    
   
    
    PPBatteryInfoModel *m = [[PPBatteryInfoModel alloc] init];
    Byte *testByte = (Byte *)[receiveData bytes];

    NSInteger power = [PPScaleFormatTool numberHexString:[PPScaleFormatTool reversStrWith2Step:[PPScaleFormatTool data2String:receiveData]]];
    
    
    if (receiveData.length > 1) {
        power = [PPScaleFormatTool numberHexString:[PPScaleFormatTool reversStrWith2Step:[PPScaleFormatTool data2String:[receiveData subdataWithRange:NSMakeRange(0, 1)]]]];
    }
    
    m.power = power;

    m.lumen = -1;
    
    
    
    if (receiveData.length != 6) {
        return m;
    }
    

    
    NSString *strengthStr = [NSString stringWithFormat:@"%02x",testByte[1]];
    NSInteger strength = [PPScaleFormatTool numberHexString:strengthStr];
    
    if(strength > 0){
        m.lumen = strength;
    }
    
    
    NSString *powerStr = [NSString stringWithFormat:@"%02x",testByte[0]];
    
    m.power = power;
    
    
    return m;
}

+ (PPCAInfoModel *)analysisCAInfoWithData:(NSData *)receiveData {
    if (receiveData.length < 28) {
        PP_Log(@"解析CA回复数据-长度不正确");
        return nil;
    }
    
    Byte *bytes = (Byte *)[receiveData bytes];
    int year = (int)(bytes[1] *256 + bytes[2]);
    int mounth = (int)(bytes[3]);
    int day = (int)(bytes[4]);
    int hour = (int)(bytes[5]);
    int minite = (int)(bytes[6]);
    int secound = (int)(bytes[7]);
    NSString *localString = [NSString stringWithFormat:@"%02d-%02d-%02d %02d:%02d:%02d",year,mounth,day,hour,minite,secound];
    NSData *fiData = [receiveData subdataWithRange:NSMakeRange(8, 20)];
    NSString *fiStr = [PPScaleFormatTool data2String:fiData];
    
    PPCAInfoModel *info = [[PPCAInfoModel alloc] init];
    info.expirationDate = localString;
    info.caFingerprint = fiStr;
    
    return info;
}

@end
