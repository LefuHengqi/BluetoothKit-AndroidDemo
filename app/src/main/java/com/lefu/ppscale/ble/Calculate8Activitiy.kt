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


        val ppWeightKg = DataUtil.util().weightKg       //weight
        val impedance = DataUtil.util().impedance       //3609627

        val userModel1 = SettingManager.get()
            .getDataObj(SettingManager.USER_MODEL, PPUserModel::class.java)

        //impedance
        val userModel = PPUserModel.Builder()
            .setSex(PPUserGender.PPUserGenderFemale) //gender
//                    .setHeight(userModel1.userHeight)//height 100-220
            .setHeight(168)//height 100-220
            .setAthleteMode(false)//height 100-220
            .setAge(18)//age 10-99
            .build()
        val deviceModel = PPDeviceModel("", "CF577")//Select the corresponding Bluetooth name according to your own device
        deviceModel.deviceCalcuteType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8
        val bodyBaseModel = PPBodyBaseModel()
//                bodyBaseModel.impedance = impedance
        bodyBaseModel.weight = UnitUtil.getWeight(ppWeightKg)
//                bodyBaseModel.weight = 8195
        bodyBaseModel.deviceModel = deviceModel
        bodyBaseModel.userModel = userModel

        bodyBaseModel.z100KhzLeftArmEnCode = 1349413211;
        bodyBaseModel.z100KhzLeftLegEnCode = 537517742;
        bodyBaseModel.z100KhzRightArmEnCode = 1346231752;
        bodyBaseModel.z100KhzRightLegEnCode = 1088034268;
        bodyBaseModel.z100KhzTrunkEnCode = 1075909621;
        bodyBaseModel.z20KhzLeftArmEnCode = 545129035;
        bodyBaseModel.z20KhzLeftLegEnCode = 553605803;
        bodyBaseModel.z20KhzRightArmEnCode = 853251;
        bodyBaseModel.z20KhzRightLegEnCode = 837067472;
        bodyBaseModel.z20KhzTrunkEnCode = 14200485;

        val fatModel = PPBodyFatModel(bodyBaseModel)

        DataUtil.util().bodyDataModel = fatModel
        Log.d("liyp_", fatModel.toString())

        val intent = Intent(this@Calculate8Activitiy, BodyDataDetailActivity::class.java)
        startActivity(intent)

    }

}