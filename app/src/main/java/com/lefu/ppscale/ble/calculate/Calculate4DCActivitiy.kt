package com.lefu.ppscale.ble.calculate

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.lefu.ppscale.ble.R
import com.lefu.ppscale.ble.activity.BodyDataDetailActivity
import com.lefu.ppscale.ble.util.UnitUtil
import com.lefu.ppscale.ble.util.DataUtil
import com.peng.ppscale.business.device.DeviceManager
import com.peng.ppscale.business.device.PPUnitType
import com.peng.ppscale.util.DeviceUtil
import com.peng.ppscale.vo.*


/**
 * 直流秤计算库
 */
class Calculate4DCActivitiy : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculate_4dc)

        findViewById<Button>(R.id.calculateBtn).setOnClickListener {
            startCalculate()
        }
    }

    private fun startCalculate() {
        val ppWeightKg = 70.0       //weight
        val impedance = 419L     //impedance

        val userModel = PPUserModel.Builder()
            .setSex(PPUserGender.PPUserGenderMale) //gender
            .setHeight(180)//height 100-220
            .setAge(28)//age 10-99
            .build()

        val deviceModel = PPDeviceModel("", DeviceManager.FL_SCALE)//Select the corresponding Bluetooth name according to your own device
        deviceModel.setDeviceCalcuteType(PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeDirect)
        deviceModel.deviceAccuracyType = if (DeviceUtil.Point2_Scale_List.contains(deviceModel.deviceName)) {
            PPScaleDefine.PPDeviceAccuracyType.PPDeviceAccuracyTypePoint005
        } else {
            PPScaleDefine.PPDeviceAccuracyType.PPDeviceAccuracyTypePoint01
        }
        val bodyBaseModel = PPBodyBaseModel()
        bodyBaseModel.impedance = impedance
        bodyBaseModel.deviceModel = deviceModel
        bodyBaseModel.userModel = userModel
        bodyBaseModel.unit = PPUnitType.Unit_KG
        bodyBaseModel.weight = UnitUtil.getWeight(ppWeightKg)

        val ppBodyFatModel = PPBodyFatModel(bodyBaseModel)

        DataUtil.util().bodyDataModel = ppBodyFatModel
        Log.d("liyp_", ppBodyFatModel.toString())

        val intent = Intent(this, BodyDataDetailActivity::class.java)
        startActivity(intent)
    }

}