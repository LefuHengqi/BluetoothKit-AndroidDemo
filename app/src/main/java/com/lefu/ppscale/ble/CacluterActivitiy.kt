package com.lefu.ppscale.ble

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.lefu.ppscale.ble.model.DataUtil
import com.peng.ppscale.business.device.DeviceManager
import com.peng.ppscale.business.device.PPUnitType
import com.peng.ppscale.vo.*

class CacluterActivitiy : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ppWeightKg = 70.0       //weight
        val impedance = 2420004L     //impedance

        val userModel = PPUserModel.Builder()
            .setSex(PPUserGender.PPUserGenderMale) //gender
            .setHeight(180)//height 100-220
            .setAge(28)//age 10-99
            .build()

        val deviceModel = PPDeviceModel("", DeviceManager.CF568_TM_315)//Select the corresponding Bluetooth name according to your own device
        deviceModel.setDeviceCalcuteType(PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeNormal)

        val bodyBaseModel = PPBodyBaseModel()
        bodyBaseModel.impedance = impedance
        bodyBaseModel.deviceModel = deviceModel
        bodyBaseModel.userModel = userModel
        bodyBaseModel.unit = PPUnitType.Unit_KG
        bodyBaseModel.weight = (ppWeightKg * 100).toInt()

        val ppBodyFatModel = PPBodyFatModel(bodyBaseModel)

        DataUtil.util().bodyDataModel = ppBodyFatModel
        Log.d("liyp_", ppBodyFatModel.toString())

    }

}