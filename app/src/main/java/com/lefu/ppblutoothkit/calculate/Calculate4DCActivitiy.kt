package com.lefu.ppblutoothkit.calculate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import com.lefu.ppbasiccalculatekit.HTBodyBaseModel
import com.lefu.ppbasiccalculatekit.HTCalculateManager
import com.lefu.ppbase.PPDeviceModel
import com.lefu.ppbase.PPScaleDefine
import com.lefu.ppbase.PPScaleDefine.PPDeviceCalcuteType
import com.lefu.ppbase.vo.PPUserGender
import com.lefu.ppbase.vo.PPUserModel
import com.lefu.ppbasiccalculatekit.SecretManager
import com.lefu.ppblutoothkit.BaseImmersivePermissionActivity
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.util.UnitUtil
import com.lefu.ppblutoothkit.util.DataUtil
import com.lefu.ppcalculate.vo.PPBodyDetailModel
import com.peng.ppscale.util.DeviceUtil
// 添加 View Binding 导入
import com.lefu.ppblutoothkit.databinding.ActivityCalculate4dcBinding
import com.lefu.ppcalculate.calculate.PPCalculateManager

/**
 * 直流秤计算库
 */
class Calculate4DCActivitiy : BaseImmersivePermissionActivity() {

    var deviceName: String = ""
    private lateinit var binding: ActivityCalculate4dcBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalculate4dcBinding.inflate(layoutInflater)
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
            title = "直流秤计算",
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
            binding.etHeight.setText(bodyBaseModel?.userModel?.userHeight.toString())
            binding.etAge.setText(bodyBaseModel?.userModel?.age.toString())
            binding.etWeight.setText(bodyBaseModel?.getPpWeightKg().toString())
            binding.etImpedance.setText(bodyBaseModel?.impedance.toString())
        }
    }

    private fun startCalculate() {
        val sex = if (binding.etSex.text?.toString()?.toInt() == 0) {
            PPUserGender.PPUserGenderFemale
        } else {
            PPUserGender.PPUserGenderMale
        }
        val height = binding.etHeight.text?.toString()?.toInt() ?: 180
        val age = binding.etAge.text?.toString()?.toInt() ?: 28
        val weight = binding.etWeight.text?.toString()?.toDouble() ?: 70.00
        val impedance = binding.etImpedance.text?.toString()?.toLong() ?: 4195332L
        
        val userModel = PPUserModel.Builder()
            .setSex(sex) //gender
            .setHeight(height)//height 90-220
            .setAge(age)//age 6-99
            .build()
            
        val deviceModel = PPDeviceModel("", deviceName)//Select the corresponding Bluetooth name according to your own device
        deviceModel.deviceCalcuteType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeDirect
        deviceModel.deviceAccuracyType = if (DeviceUtil.Point2_Scale_List.contains(deviceModel.deviceName)) {
            PPScaleDefine.PPDeviceAccuracyType.PPDeviceAccuracyTypePoint005
        } else {
            PPScaleDefine.PPDeviceAccuracyType.PPDeviceAccuracyTypePoint01
        }

        val bodyBaseModel = HTBodyBaseModel()
        bodyBaseModel.weight = UnitUtil.getWeight(weight)
        bodyBaseModel.impedance = impedance
        bodyBaseModel.calculateType =  PPDeviceCalcuteType.PPDeviceCalcuteTypeDirect.getType()

        bodyBaseModel.height = userModel.userHeight
        bodyBaseModel.age = userModel.age
        bodyBaseModel.sex = if (userModel.sex == PPUserGender.PPUserGenderFemale) 0 else 1
        bodyBaseModel.secret = SecretManager.getSecret(bodyBaseModel.calculateType)

        val calculateDataJson = HTCalculateManager.calculateDataJson(bodyBaseModel)

        val ppBodyFatModel = PPCalculateManager.calculateData(calculateDataJson)

        val ppBodyDetailModel = ppBodyFatModel?.let { PPBodyDetailModel(it) }
        
        Log.d("liyp_", ppBodyDetailModel.toString())
        
        DataUtil.bodyDataModel = ppBodyFatModel
        Log.d("liyp_", ppBodyFatModel.toString())
        
        val intent = Intent(this, BodyDataDetailActivity::class.java)
        startActivity(intent)
    }
}