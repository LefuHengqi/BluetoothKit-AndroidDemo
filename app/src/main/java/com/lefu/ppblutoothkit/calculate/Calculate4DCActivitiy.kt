package com.lefu.ppblutoothkit.calculate

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
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
import com.lefu.ppblutoothkit.batch_calculate.BaseBatchCalculateActivity
import com.lefu.ppblutoothkit.batch_calculate.CSVFIleUtil
import com.lefu.ppcalculate.vo.PPBodyDetailModel
import com.peng.ppscale.util.DeviceUtil
// 添加 View Binding 导入
import com.lefu.ppblutoothkit.databinding.ActivityCalculate4dcBinding

/**
 * 直流秤计算库
 */
class Calculate4DCActivitiy : BaseBatchCalculateActivity() {

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

        initbatchCalculation()
    }
    
    private fun initToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setupUnifiedToolbar(
            toolbar = toolbar,
            title = "直流秤计算",
            showBackButton = true
        )
    }


    private fun initbatchCalculation() {
        logTxt = binding.logTxt
        binding.logTxt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                binding.nestedScrollViewLog.fullScroll(View.FOCUS_DOWN)
            }
        })
        findViewById<Button>(R.id.device_selectCSV).setOnClickListener {
            batchCalculateType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeDirect
            addPrint("check sdCard read and write permission batchCalculateType:${batchCalculateType}")
            requestPermission()
        }

        findViewById<Button>(R.id.device_startCalculate).setOnClickListener {
            startBatchCalculate()
        }
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
        deviceModel.setDeviceCalcuteType(PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeDirect)
        deviceModel.setDeviceAccuracyType(if (DeviceUtil.Point2_Scale_List.contains(deviceModel.deviceName)) {
            PPScaleDefine.PPDeviceAccuracyType.PPDeviceAccuracyTypePoint005
        } else {
            PPScaleDefine.PPDeviceAccuracyType.PPDeviceAccuracyTypePoint01
        })
        
        val bodyBaseModel = PPBodyBaseModel()
        bodyBaseModel.weight = UnitUtil.getWeight(weight)
        bodyBaseModel.impedance = impedance
        bodyBaseModel.deviceModel = deviceModel
        bodyBaseModel.userModel = userModel
        bodyBaseModel.unit = PPUnitType.Unit_KG
        bodyBaseModel.secret = SecretManager.getSecret(deviceModel.deviceCalcuteType.getType())
        val ppBodyFatModel = PPBodyFatModel(bodyBaseModel)
        val bodyDetailModel = PPBodyDetailModel(ppBodyFatModel)
        
        Log.d("liyp_", bodyDetailModel.toString())
        
        DataUtil.bodyDataModel = ppBodyFatModel
        Log.d("liyp_", ppBodyFatModel.toString())
        
        val intent = Intent(this, BodyDataDetailActivity::class.java)
        startActivity(intent)
    }

    override fun calculateCSVDataByProduct0(dataRow: CSVFIleUtil.CSVDataRow): PPBodyFatModel? {
        val sex = if (dataRow.gender.equals("男", false)) {
            PPUserGender.PPUserGenderMale
        } else {
            PPUserGender.PPUserGenderFemale
        }
        val height = dataRow.height.toInt()
        val age = dataRow.age
        val weight = dataRow.weight.toDouble()
        val impedance = dataRow.impedance50Khz

        val userModel = PPUserModel.Builder()
            .setSex(sex)
            .setHeight(height)
            .setAge(age)
            .build()

        val deviceModel = PPDeviceModel("", "")
        deviceModel.setDeviceCalcuteType(PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeDirect)

        val bodyBaseModel = PPBodyBaseModel()
        bodyBaseModel.weight = UnitUtil.getWeight(weight)
        bodyBaseModel.impedance = impedance
        bodyBaseModel.deviceModel = deviceModel
        bodyBaseModel.userModel = userModel
        bodyBaseModel.unit = PPUnitType.Unit_KG

        return PPBodyFatModel(bodyBaseModel)
    }
}