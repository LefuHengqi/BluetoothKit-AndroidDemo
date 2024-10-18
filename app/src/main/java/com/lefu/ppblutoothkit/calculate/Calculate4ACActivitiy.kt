package com.lefu.ppblutoothkit.calculate

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
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

/**
 * 4电极交流算法
 */
class Calculate4ACActivitiy : Activity() {

    var deviceName: String = ""
    var calcuteType: PPScaleDefine.PPDeviceCalcuteType? = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate//4电极新版计算库
    var etSex: EditText? = null
    var etHeight: EditText? = null
    var etAge: EditText? = null
    var etWeight: EditText? = null
    var etImpedance: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculate_4ac)

        findViewById<Button>(R.id.calculateBtn).setOnClickListener {
            startCalculate()
        }

        val toolbar: Toolbar? = findViewById(R.id.toolbar)
        toolbar?.title = getString(R.string._4ac)
        toolbar?.setTitleTextColor(Color.WHITE)

        initData()
    }

    private fun initData() {
        etSex = findViewById(R.id.etSex)
        etHeight = findViewById(R.id.etHeight)
        etAge = findViewById(R.id.etAge)
        etWeight = findViewById(R.id.etWeight)
        etImpedance = findViewById(R.id.etImpedance1)
        val tag = intent.getStringExtra("bodyDataModel")
        if (tag != null) {
            //显示称重完成后的数据
            val bodyBaseModel = DataUtil.bodyBaseModel
            deviceName = bodyBaseModel?.deviceModel?.deviceName ?: ""
            etSex?.setText(if (bodyBaseModel?.userModel?.sex == PPUserGender.PPUserGenderFemale) "0" else "1")
            etHeight?.setText(bodyBaseModel?.userModel?.userHeight.toString())
            etAge?.setText(bodyBaseModel?.userModel?.age.toString())
            etWeight?.setText(bodyBaseModel?.getPpWeightKg().toString())
            etImpedance?.setText(bodyBaseModel?.impedance.toString())
            calcuteType = bodyBaseModel?.deviceModel?.deviceCalcuteType
        }
    }

    private fun startCalculate() {
        val sex = if (etSex?.text?.toString()?.toInt() == 0) {
            PPUserGender.PPUserGenderFemale
        } else {
            PPUserGender.PPUserGenderMale
        }
        val height = etHeight?.text?.toString()?.toInt() ?: 180
        val age = etAge?.text?.toString()?.toInt() ?: 28
        val weight = etWeight?.text?.toString()?.toDouble() ?: 70.00
        val impedance = etImpedance?.text?.toString()?.toLong() ?: 4195332L

        val userModel = PPUserModel.Builder()
            .setSex(sex) //gender
            .setHeight(height)//height 90-220
            .setAge(age)//age 6-99
            .build()

        val deviceModel = PPDeviceModel("", deviceName)//Select the corresponding Bluetooth name according to your own device
        deviceModel.deviceCalcuteType = calcuteType ?: PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate4_0
        deviceModel.deviceAccuracyType = if (DeviceUtil.Point2_Scale_List.contains(deviceModel.deviceName)) {
            PPScaleDefine.PPDeviceAccuracyType.PPDeviceAccuracyTypePoint005
        } else {
            PPScaleDefine.PPDeviceAccuracyType.PPDeviceAccuracyTypePoint01
        }

        val bodyBaseModel = PPBodyBaseModel()
        bodyBaseModel.weight = UnitUtil.getWeight(weight)
        bodyBaseModel.impedance = impedance
        bodyBaseModel.deviceModel = deviceModel
        bodyBaseModel.userModel = userModel
        bodyBaseModel.unit = PPUnitType.Unit_KG
2
        val ppBodyFatModel = PPBodyFatModel(bodyBaseModel)

        val ppBodyDetailModel = PPBodyDetailModel(ppBodyFatModel)
        Log.d("liyp_", ppBodyDetailModel.toString())

        DataUtil.bodyDataModel = ppBodyFatModel
        Log.d("liyp_", ppBodyFatModel.toString())

        val intent = Intent(this, BodyDataDetailActivity::class.java)
        startActivity(intent)
    }

}