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
import com.peng.ppscale.vo.*

/**
 * 4电极交流算法
 */
class Calculate4ACActivitiy : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculate_4ac)

        findViewById<Button>(R.id.calculateBtn).setOnClickListener {
            startCalculate()
        }

    }

    private fun startCalculate() {
        val ppWeightKg = 70.0       //weight
        val impedance = 4195332L     //impedance

        val userModel = PPUserModel.Builder()
            .setSex(PPUserGender.PPUserGenderMale) //gender
            .setHeight(180)//height 100-220
            .setAge(28)//age 10-99
            .build()

        val deviceModel = PPDeviceModel("", DeviceManager.CF568_TM_315)//Select the corresponding Bluetooth name according to your own device
        deviceModel.setDeviceCalcuteType(PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate)

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