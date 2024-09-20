package com.lefu.ppblutoothkit.calculate

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.lefu.ppbase.PPBodyBaseModel
import com.lefu.ppbase.PPDeviceModel
import com.lefu.ppbase.PPScaleDefine
import com.lefu.ppbase.vo.PPUserGender
import com.lefu.ppbase.vo.PPUserModel
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.util.UnitUtil
import com.lefu.ppblutoothkit.util.DataUtil
import com.lefu.ppcalculate.PPBodyFatModel
import com.lefu.ppbase.vo.PPUnitType
import com.lefu.ppcalculate.vo.PPBodyDetailModel
import com.peng.ppscale.util.DeviceUtil
import kotlinx.android.synthetic.main.activity_calculate_4ac.*
import kotlinx.android.synthetic.main.activity_calculate_4ac2channel.etImpedance1
import kotlinx.android.synthetic.main.activity_calculate_4ac2channel.etImpedance2
import kotlinx.android.synthetic.main.activity_calculate_4ac2channel.sportModeEt
import kotlinx.android.synthetic.main.activity_calculate_8ac.etAge
import kotlinx.android.synthetic.main.activity_calculate_8ac.etHeight
import kotlinx.android.synthetic.main.activity_calculate_8ac.etSex
import kotlinx.android.synthetic.main.activity_calculate_8ac.etWeight

/**
 * 4电极交流双频算法
 */
class Calculate4AC2ChannelActivitiy : Activity() {

    var deviceName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculate_4ac2channel)

        findViewById<Button>(R.id.calculateBtn).setOnClickListener {
            startCalculate()
        }

        initData()
    }

    private fun initData() {

        val tag = intent.getStringExtra("bodyDataModel")
        if (tag != null) {
            //显示称重完成后的数据
            val bodyBaseModel = DataUtil.bodyBaseModel
            deviceName = bodyBaseModel?.deviceModel?.deviceName ?: ""
            etSex.setText(if (bodyBaseModel?.userModel?.sex == PPUserGender.PPUserGenderFemale) "0" else "1")
            sportModeEt.setText(if (bodyBaseModel?.userModel?.isAthleteMode == false) "0" else "1")
            etHeight.setText(bodyBaseModel?.userModel?.userHeight.toString())
            etAge.setText(bodyBaseModel?.userModel?.age.toString())
            etWeight.setText(bodyBaseModel?.getPpWeightKg().toString())
            etImpedance1.setText(bodyBaseModel?.impedance.toString())
            etImpedance2.setText(bodyBaseModel?.ppImpedance100EnCode.toString())
        }
    }

    private fun startCalculate() {
        val sex = if (etSex.text?.toString()?.toInt() == 0) {
            PPUserGender.PPUserGenderFemale
        } else {
            PPUserGender.PPUserGenderMale
        }

        val isAthleteMode = sportModeEt.text?.toString()?.toInt() == 1

        val height = etHeight.text?.toString()?.toInt() ?: 180
        val age = etAge.text?.toString()?.toInt() ?: 28
        val weight = etWeight.text?.toString()?.toDouble() ?: 70.00
        val impedance1 = etImpedance1.text?.toString()?.toLong() ?: 4195332L
        val impedance2 = etImpedance2.text?.toString()?.toLong() ?: 4195332L

        val userModel = PPUserModel.Builder()
            .setSex(sex) //gender
            .setHeight(height)//height 90-220
            .setAge(age)//age 6-99
            .setAthleteMode(isAthleteMode)
            .build()

        val deviceModel = PPDeviceModel("", deviceName)//Select the corresponding Bluetooth name according to your own device
        deviceModel.deviceCalcuteType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate4_1
        deviceModel.deviceAccuracyType = if (DeviceUtil.Point2_Scale_List.contains(deviceModel.deviceName)) {
            PPScaleDefine.PPDeviceAccuracyType.PPDeviceAccuracyTypePoint005
        } else {
            PPScaleDefine.PPDeviceAccuracyType.PPDeviceAccuracyTypePoint01
        }
        val bodyBaseModel = PPBodyBaseModel()
        bodyBaseModel.weight = UnitUtil.getWeight(weight)
        bodyBaseModel.impedance = impedance1
        bodyBaseModel.ppImpedance100EnCode = impedance2
        bodyBaseModel.deviceModel = deviceModel
        bodyBaseModel.userModel = userModel
        bodyBaseModel.unit = PPUnitType.Unit_KG

        val ppBodyFatModel = PPBodyFatModel(bodyBaseModel)

        val ppBodyDetailModel = PPBodyDetailModel(ppBodyFatModel)
        Log.d("liyp_", ppBodyDetailModel.toString())

        DataUtil.bodyDataModel = ppBodyFatModel
        Log.d("liyp_", ppBodyFatModel.toString())

        val intent = Intent(this, BodyDataDetailActivity::class.java)
        startActivity(intent)
    }

}