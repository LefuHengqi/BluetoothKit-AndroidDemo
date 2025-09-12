package com.lefu.ppblutoothkit.calculate

import android.app.Activity
import androidx.appcompat.widget.Toolbar
import com.lefu.ppblutoothkit.BaseImmersivePermissionActivity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner

import com.lefu.ppbase.PPBodyBaseModel
import com.lefu.ppbase.PPDeviceModel
import com.lefu.ppbase.PPScaleDefine
import com.lefu.ppbase.vo.PPUnitType
import com.lefu.ppbase.vo.PPUserGender
import com.lefu.ppbase.vo.PPUserModel
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.SecretManager
import com.lefu.ppblutoothkit.util.DataUtil
import com.lefu.ppblutoothkit.util.UnitUtil
import com.lefu.ppcalculate.PPBodyFatModel
import com.lefu.ppcalculate.vo.PPBodyDetailModel
// 添加 View Binding 导入
import com.lefu.ppblutoothkit.databinding.ActivityCalculate8acBinding

// 移除 kotlinx.android.synthetic 导入

/**
 * 8电极计算库
 */
class Calculate8Activitiy : BaseImmersivePermissionActivity() {

    var calcuteType: PPScaleDefine.PPDeviceCalcuteType? = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8
    var spinner: Spinner? = null
    var deviceName: String = ""
    private lateinit var binding: ActivityCalculate8acBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalculate8acBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 在 setContentView 之后调用沉浸式设置
        setupImmersiveMode()

        // 初始化Toolbar
        initToolbar()

        binding.calculateBtn.setOnClickListener {
            startCalculate()
        }
        spinner = binding.spinner
        val adapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter.add("Product1-CF577")
        adapter.add("Product3-CF586")
        adapter.add("Product4-CF597")
        adapter.add("Product7-CF610")
        adapter.add("Product5-CF577_N1")
        adapter.add("Product6-CF597_N")
        spinner?.setAdapter(adapter)
        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    calcuteType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8
                } else if (position == 1) {
                    calcuteType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_1
                } else if (position == 2) {
                    calcuteType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_0
                } else if (position == 3) {
                    calcuteType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_2
                } else if (position == 4) {
                    calcuteType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_3
                } else if (position == 5) {
                    calcuteType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_4
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
        initData()
    }

    private fun initToolbar() {
        val toolbar: Toolbar? = findViewById(R.id.toolbar)
        toolbar?.let {
            setupUnifiedToolbar(
                toolbar = it,
                title = "8电极计算",
                showBackButton = true
            )
        }
    }

    private fun initData() {
        val tag = intent.getStringExtra("bodyDataModel")
        if (tag != null) {
            val bodyBaseModel = DataUtil.bodyBaseModel
            deviceName = bodyBaseModel?.deviceModel?.deviceName ?: ""
            binding.etSex.setText(if (bodyBaseModel?.userModel?.sex == PPUserGender.PPUserGenderFemale) "0" else "1")
            binding.etHeight.setText(bodyBaseModel?.userModel?.userHeight.toString())
            binding.etAge.setText(bodyBaseModel?.userModel?.age.toString())
            binding.etWeight.setText(bodyBaseModel?.getPpWeightKg().toString())
            binding.z100KhzLeftArmEnCode.setText(bodyBaseModel?.z100KhzLeftArmEnCode.toString())
            binding.z100KhzLeftLegEnCode.setText(bodyBaseModel?.z100KhzLeftLegEnCode.toString())
            binding.z100KhzRightArmEnCode.setText(bodyBaseModel?.z100KhzRightArmEnCode.toString())
            binding.z100KhzRightLegEnCode.setText(bodyBaseModel?.z100KhzRightLegEnCode.toString())
            binding.z100KhzTrunkEnCode.setText(bodyBaseModel?.z100KhzTrunkEnCode.toString())
            binding.z20KhzLeftArmEnCode.setText(bodyBaseModel?.z20KhzLeftArmEnCode.toString())
            binding.z20KhzLeftLegEnCode.setText(bodyBaseModel?.z20KhzLeftLegEnCode.toString())
            binding.z20KhzRightArmEnCode.setText(bodyBaseModel?.z20KhzRightArmEnCode.toString())
            binding.z20KhzRightLegEnCode.setText(bodyBaseModel?.z20KhzRightLegEnCode.toString())
            binding.z20KhzTrunkEnCode.setText(bodyBaseModel?.z20KhzTrunkEnCode.toString())

            calcuteType = bodyBaseModel?.deviceModel?.deviceCalcuteType
            if (calcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8) {
                spinner?.setSelection(0)
            } else if (calcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_1) {
                spinner?.setSelection(1)
            } else if (calcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_0) {
                spinner?.setSelection(2)
            } else if (calcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_2) {
                spinner?.setSelection(3)
            } else if (calcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_3) {
                spinner?.setSelection(4)
            } else if (calcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_4) {
                spinner?.setSelection(5)
            }
        }
    }

    private fun startCalculate() {
        val sex = if (binding.etSex.text?.toString()?.toInt() == 0) {
            PPUserGender.PPUserGenderFemale
        } else {
            PPUserGender.PPUserGenderMale
        }
        val height = binding.etHeight.text?.toString()?.toInt() ?: 168
        val age = binding.etAge.text?.toString()?.toInt() ?: 35
        val weight = binding.etWeight.text?.toString()?.toDouble() ?: 83.00

        val z100KhzLeftArmEnCode = binding.z100KhzLeftArmEnCode.text?.toString()?.toLong() ?: 294794323L
        val z100KhzLeftLegEnCode = binding.z100KhzLeftLegEnCode.text?.toString()?.toLong() ?: 806102147L
        val z100KhzRightArmEnCode = binding.z100KhzRightArmEnCode.text?.toString()?.toLong() ?: 26360525L
        val z100KhzRightLegEnCode = binding.z100KhzRightLegEnCode.text?.toString()?.toLong() ?: 816581534L
        val z100KhzTrunkEnCode = binding.z100KhzTrunkEnCode.text?.toString()?.toLong() ?: 1080247226L
        val z20KhzLeftArmEnCode = binding.z20KhzLeftArmEnCode.text?.toString()?.toLong() ?: 27983001L
        val z20KhzLeftLegEnCode = binding.z20KhzLeftLegEnCode.text?.toString()?.toLong() ?: 837194050L
        val z20KhzRightArmEnCode = binding.z20KhzRightArmEnCode.text?.toString()?.toLong() ?: 1634195706L
        val z20KhzRightLegEnCode = binding.z20KhzRightLegEnCode.text?.toString()?.toLong() ?: 29868463L
        val z20KhzTrunkEnCode = binding.z20KhzTrunkEnCode.text?.toString()?.toLong() ?: 1881406429L

        val userModel = PPUserModel.Builder()
            .setSex(sex)
            .setHeight(height)
            .setAge(age)
            .build()

        val deviceModel = PPDeviceModel("", deviceName)
        deviceModel.setDeviceCalcuteType(calcuteType ?: PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8)
        val bodyBaseModel = PPBodyBaseModel()

        bodyBaseModel.weight = UnitUtil.getWeight(weight)
        bodyBaseModel.deviceModel = deviceModel
        bodyBaseModel.userModel = userModel
        bodyBaseModel.unit = PPUnitType.Unit_KG
        bodyBaseModel.z100KhzLeftArmEnCode = z100KhzLeftArmEnCode
        bodyBaseModel.z100KhzLeftLegEnCode = z100KhzLeftLegEnCode
        bodyBaseModel.z100KhzRightArmEnCode = z100KhzRightArmEnCode
        bodyBaseModel.z100KhzRightLegEnCode = z100KhzRightLegEnCode
        bodyBaseModel.z100KhzTrunkEnCode = z100KhzTrunkEnCode
        bodyBaseModel.z20KhzLeftArmEnCode = z20KhzLeftArmEnCode
        bodyBaseModel.z20KhzLeftLegEnCode = z20KhzLeftLegEnCode
        bodyBaseModel.z20KhzRightArmEnCode = z20KhzRightArmEnCode
        bodyBaseModel.z20KhzRightLegEnCode = z20KhzRightLegEnCode
        bodyBaseModel.z20KhzTrunkEnCode = z20KhzTrunkEnCode

        SecretManager.bodyBaseModel = bodyBaseModel
        bodyBaseModel.secret = SecretManager.getSecret(deviceModel.deviceCalcuteType.getType())

        val fatModel = PPBodyFatModel(bodyBaseModel)
        val ppDetailModel = PPBodyDetailModel(fatModel)
        Log.d("liyp_", ppDetailModel.toString())

        DataUtil.bodyDataModel = fatModel
        Log.d("liyp_", fatModel.toString())

        val intent = Intent(this@Calculate8Activitiy, BodyDataDetailActivity::class.java)
        startActivity(intent)
    }
}