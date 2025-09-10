package com.lefu.ppblutoothkit.calculate

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import com.lefu.ppbase.PPBodyBaseModel
import com.lefu.ppbase.PPDeviceModel
import com.lefu.ppbase.PPScaleDefine
import com.lefu.ppbase.vo.PPUserGender
import com.lefu.ppbase.vo.PPUserModel
import com.lefu.ppblutoothkit.BaseImmersivePermissionActivity
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.util.UnitUtil
import com.lefu.ppblutoothkit.util.DataUtil
import com.lefu.ppcalculate.PPBodyFatModel
import com.lefu.ppbase.vo.PPUnitType
import com.lefu.ppblutoothkit.SecretManager
import com.lefu.ppcalculate.vo.PPBodyDetailModel
import com.peng.ppscale.util.DeviceUtil
import com.lefu.ppblutoothkit.databinding.ActivityCalculate4ac2channelBinding

/**
 * 4电极交流双频算法
 */
class Calculate4AC2ChannelActivitiy : BaseImmersivePermissionActivity() {

    var deviceName: String = ""
    private lateinit var binding: ActivityCalculate4ac2channelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalculate4ac2channelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // 在 setContentView 之后调用沉浸式设置
        setupImmersiveMode()
        
        // 初始化Toolbar
        initToolbar()

        binding.calculateBtn.setOnClickListener {
            startCalculate()
        }

        initData()
    }
    
    private fun initToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setupUnifiedToolbar(
            toolbar = toolbar,
            title = "4电极交流双频算法",
            showBackButton = true
        )
    }

    private fun initData() {
        val tag = intent.getStringExtra("bodyDataModel")
        if (tag != null) {
            //显示称重完成后的数据
            val bodyBaseModel = DataUtil.bodyBaseModel
            deviceName = bodyBaseModel?.deviceModel?.deviceName ?: ""
            binding.etSex.setText(if (bodyBaseModel?.userModel?.sex == PPUserGender.PPUserGenderFemale) "0" else "1")
            binding.sportModeEt.setText(if (bodyBaseModel?.userModel?.isAthleteMode == false) "0" else "1")
            binding.etHeight.setText(bodyBaseModel?.userModel?.userHeight.toString())
            binding.etAge.setText(bodyBaseModel?.userModel?.age.toString())
            binding.etWeight.setText(bodyBaseModel?.getPpWeightKg().toString())
            binding.etImpedance1.setText(bodyBaseModel?.impedance.toString())
            binding.etImpedance2.setText(bodyBaseModel?.ppImpedance100EnCode.toString())
        }
    }

    private fun startCalculate() {
        val sex = if (binding.etSex.text?.toString()?.toInt() == 0) {
            PPUserGender.PPUserGenderFemale
        } else {
            PPUserGender.PPUserGenderMale
        }

        val isAthleteMode = binding.sportModeEt.text?.toString()?.toInt() == 1

        val height = binding.etHeight.text?.toString()?.toInt() ?: 180
        val age = binding.etAge.text?.toString()?.toInt() ?: 28
        val weight = binding.etWeight.text?.toString()?.toDouble() ?: 70.00
        val impedance1 = binding.etImpedance1.text?.toString()?.toLong() ?: 4195332L
        val impedance2 = binding.etImpedance2.text?.toString()?.toLong() ?: 4195332L

        val userModel = PPUserModel.Builder()
            .setSex(sex) //gender
            .setHeight(height)//height 90-220
            .setAge(age)//age 6-99
            .setAthleteMode(isAthleteMode)
            .build()

        val deviceModel = PPDeviceModel("", deviceName)//Select the corresponding Bluetooth name according to your own device
        deviceModel.setDeviceCalcuteType(PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate4_1)
        deviceModel.setDeviceAccuracyType(if (DeviceUtil.Point2_Scale_List.contains(deviceModel.deviceName)) {
            PPScaleDefine.PPDeviceAccuracyType.PPDeviceAccuracyTypePoint005
        } else {
            PPScaleDefine.PPDeviceAccuracyType.PPDeviceAccuracyTypePoint01
        })
        val bodyBaseModel = PPBodyBaseModel()
        bodyBaseModel.weight = UnitUtil.getWeight(weight)
        bodyBaseModel.impedance = impedance1
        bodyBaseModel.ppImpedance100EnCode = impedance2
        bodyBaseModel.deviceModel = deviceModel
        bodyBaseModel.userModel = userModel
        bodyBaseModel.unit = PPUnitType.Unit_KG
        bodyBaseModel.secret = SecretManager.getSecret(deviceModel.deviceCalcuteType.getType())
        val ppBodyFatModel = PPBodyFatModel(bodyBaseModel)

        val ppBodyDetailModel = PPBodyDetailModel(ppBodyFatModel)
        Log.d("liyp_", ppBodyDetailModel.toString())

        DataUtil.bodyDataModel = ppBodyFatModel
        Log.d("liyp_", ppBodyFatModel.toString())

        val intent = Intent(this, BodyDataDetailActivity::class.java)
        startActivity(intent)
    }

}