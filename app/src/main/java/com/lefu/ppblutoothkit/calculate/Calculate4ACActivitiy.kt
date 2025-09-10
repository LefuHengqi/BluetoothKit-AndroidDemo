package com.lefu.ppblutoothkit.calculate

// 添加 View Binding 导入
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.widget.Toolbar
import com.lefu.ppbase.PPBodyBaseModel
import com.lefu.ppbase.PPDeviceModel
import com.lefu.ppbase.PPScaleDefine
import com.lefu.ppbase.vo.PPUnitType
import com.lefu.ppbase.vo.PPUserGender
import com.lefu.ppbase.vo.PPUserModel
import com.lefu.ppblutoothkit.BaseImmersivePermissionActivity
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.SecretManager
import com.lefu.ppblutoothkit.databinding.ActivityCalculate4acBinding
import com.lefu.ppblutoothkit.util.DataUtil
import com.lefu.ppblutoothkit.util.UnitUtil
import com.lefu.ppcalculate.PPBodyFatModel
import com.lefu.ppcalculate.vo.PPBodyDetailModel

// 移除所有 kotlinx.android.synthetic 导入

/**
 * 4电极交流算法
 */
class Calculate4ACActivitiy : BaseImmersivePermissionActivity() {

    var deviceName: String = ""
    var calcuteType: PPScaleDefine.PPDeviceCalcuteType? = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate
    var spinner: Spinner? = null
    private lateinit var binding: ActivityCalculate4acBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalculate4acBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // 在 setContentView 之后调用沉浸式设置
        setupImmersiveMode()
        
        // 初始化Toolbar
        initToolbar()

        binding.calculateBtn.setOnClickListener {
            startCalculate()
        }
        spinner = binding.spinner
        val toolbar: Toolbar? = findViewById(R.id.toolbar)
        toolbar?.title = getString(R.string._4ac)
        toolbar?.setTitleTextColor(Color.WHITE)
        val adapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter.add("Version-V5.0.5")
        adapter.add("Version-V5.0.b")
        spinner?.setAdapter(adapter)
        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    calcuteType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate
                } else if (position == 1) {
                    calcuteType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate4_0
                } else if (position == 2) {
                    calcuteType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeNormal
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
                title = "4AC计算",
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
            binding.etImpedance.setText(bodyBaseModel?.impedance.toString())
            calcuteType = bodyBaseModel?.deviceModel?.deviceCalcuteType
            if (calcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate) {
                // 4电极交流-V5.0.5固定版本-做减法
                spinner?.setSelection(0)
            } else if (calcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate4_0) {
                // 4电极交流(新)-跟随方案商，最新版本
                spinner?.setSelection(1)
            } else if (calcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeNormal) {
                // 4电极交流4-V5.0.5固定版本-不做减法
                spinner?.setSelection(2)
            }
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
        deviceModel.setDeviceCalcuteType(calcuteType ?: PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate)

        val bodyBaseModel = PPBodyBaseModel()
        bodyBaseModel.weight = UnitUtil.getWeight(weight)
        bodyBaseModel.impedance = impedance
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