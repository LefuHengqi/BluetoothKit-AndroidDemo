

Android的示例程序：
https://gitee.com/ppscale/ppscale-android-demo
iOS的示例程序：
https://gitee.com/pengsiyuan777/ppscale-demo-ios


直接使用计算库的方法：
以下是Android的使用示例：


                val ppWeightKg = 59.9       //weight
                val impedance = 503     //impedance
                val userModel = PPUserModel.Builder()
                        .setSex(PPUserGender.PPUserGenderFemale) //gender
                        .setHeight(158)//height 100-220
                        .setAge(28)//age 10-99
                        .build()

                //Select the corresponding Bluetooth name according to your own device
                val deviceModel = PPDeviceModel("", DeviceManager.LF_SMART_SCALE_CF539)

                val ppBodyFatModel = PPBodyFatModel(ppWeightKg, impedance, userModel, deviceModel, PPUnitType.Unit_KG)

                Log.d("liyp_", ppBodyFatModel.toString())


将原有的HTBodyfat_ SDK计算库删除，然后引入ppscale-2.1.10-20221206.083556-3.aar文件，
并在项目的build.gradle文件中引入它，然后通过创建PPBodyFatModel的实例来计算体脂数据。

以下是iOS的使用示例：

    #import <PPScaleSDK/PPScaleManager.h>
    
    CGFloat weight =  59.9 ;
    NSInteger impedance = 503;
    //sex PPUserGenderFemale | PPUserGenderMale
    //height 100-220
    //age 10-99

    PPUserModel *user = [[PPUserModel alloc] initWithUserHeight:158 gender:PPUserGenderFemale age:28 pregnantMode:NO athleteMode:NO andUnit:PPUnitKG];

    PPDeviceModel *deviceModel = [[PPDeviceModel alloc] init];

    //PPDeviceCalcuteTypeDirect, // DC algorithm ----Smart scale CF539
    //PPDeviceCalcuteTypeAlternate, // AC algorithm----CF398,CF616
    deviceModel.deviceCalcuteType = PPDeviceCalcuteTypeAlternate;
   
    PPBodyFatModel *fatModel = [[PPBodyFatModel alloc] initWithUserModel:user deviceModel:deviceModel weight:weight heartRate:0 isHeartRateEnd:YES unit:PPUnitKG andImpedance:impedance];

    NSLog(@"%@",fatModel);





