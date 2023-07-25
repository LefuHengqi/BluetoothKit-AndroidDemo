package com.lefu.ppscale.ble

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.lefu.base.SettingManager
import com.lefu.ppscale.ble.activity.BodyDataDetailActivity
import com.lefu.ppscale.ble.model.DataUtil
import com.peng.ppscale.business.device.DeviceManager
import com.peng.ppscale.business.device.PPUnitType
import com.peng.ppscale.vo.*


/**
 * 2.x 连接 apple
 * 2.x 广播 banana
 * 3.x 连接 coconat
 * 秤端计算 durain
 * Torre torre
 */
class Calculate8Activitiy : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculate)


//        val ppWeightKg = DataUtil.util().weightKg       //weight
//        val ppWeightKg = DataUtil.util().weightKg       //weight
//        val impedance = DataUtil.util().impedance       //3609627

        val userModel1 = SettingManager.get()
            .getDataObj(SettingManager.USER_MODEL, PPUserModel::class.java)

        //體重(Kg)=111.2
        //2023-07-25 14:14:19.325 10395-10395 System.out              com.lefu.xin.debug                   I  身高(cm)=168.0
        //2023-07-25 14:14:19.325 10395-10395 System.out              com.lefu.xin.debug                   I  年齡(歲)=35
        //2023-07-25 14:14:19.325 10395-10395 System.out              com.lefu.xin.debug                   I  性別=MALE
        //2023-07-25 14:14:19.325 10395-10395 System.out              com.lefu.xin.debug                   I  加密阻抗-20Khz右手=1899017045
        //2023-07-25 14:14:19.325 10395-10395 System.out              com.lefu.xin.debug                   I  加密阻抗-20Khz左手=1638237328
        //2023-07-25 14:14:19.325 10395-10395 System.out              com.lefu.xin.debug                   I  加密阻抗-20Khz躯干=2104597
        //2023-07-25 14:14:19.325 10395-10395 System.out              com.lefu.xin.debug                   I  加密阻抗-20Khz右脚=15335777
        //2023-07-25 14:14:19.325 10395-10395 System.out              com.lefu.xin.debug                   I  加密阻抗-20Khz左手=553608489
        //2023-07-25 14:14:19.325 10395-10395 System.out              com.lefu.xin.debug                   I  加密阻抗-100Khz右手=808371149
        //2023-07-25 14:14:19.325 10395-10395 System.out              com.lefu.xin.debug                   I  加密阻抗-100Khz左手=1106257050
        //2023-07-25 14:14:19.325 10395-10395 System.out              com.lefu.xin.debug                   I  加密阻抗-100Khz躯干=553646843
        //2023-07-25 14:14:19.325 10395-10395 System.out              com.lefu.xin.debug                   I  加密阻抗-100Khz右脚=1081914980
        //2023-07-25 14:14:19.325 10395-10395 System.out              com.lefu.xin.debug                   I  加密阻抗-100Khz左脚=272117228
        //impedance
        val userModel = PPUserModel.Builder()
            .setSex(PPUserGender.PPUserGenderMale) //gender
//                    .setHeight(userModel1.userHeight)//height 100-220
            .setHeight(168)//height 100-220
            .setAthleteMode(false)
            .setAge(35)//age 10-99
            .build()
        val deviceModel = PPDeviceModel("", "CF577")//Select the corresponding Bluetooth name according to your own device
        deviceModel.deviceCalcuteType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8
        val bodyBaseModel = PPBodyBaseModel()
//                bodyBaseModel.impedance = impedance
        bodyBaseModel.weight = UnitUtil.getWeight(111.2)
//                bodyBaseModel.weight = 8195
        bodyBaseModel.deviceModel = deviceModel
        bodyBaseModel.userModel = userModel

        bodyBaseModel.z100KhzLeftArmEnCode = 1106257050
        bodyBaseModel.z100KhzLeftLegEnCode = 272117228
        bodyBaseModel.z100KhzRightArmEnCode = 808371149
        bodyBaseModel.z100KhzRightLegEnCode = 1081914980
        bodyBaseModel.z100KhzTrunkEnCode = 553646843
        bodyBaseModel.z20KhzLeftArmEnCode = 1638237328
        bodyBaseModel.z20KhzLeftLegEnCode = 553608489
        bodyBaseModel.z20KhzRightArmEnCode = 1899017045
        bodyBaseModel.z20KhzRightLegEnCode = 15335777
        bodyBaseModel.z20KhzTrunkEnCode = 2104597

        val fatModel = PPBodyFatModel(bodyBaseModel)

        DataUtil.util().bodyDataModel = fatModel
        Log.d("liyp_", fatModel.toString())

        val intent = Intent(this@Calculate8Activitiy, BodyDataDetailActivity::class.java)
        startActivity(intent)

    }

}