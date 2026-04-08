package com.lefu.ppblutoothkit.calculate

// 添加 View Binding 导入
import android.content.Intent
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
import com.lefu.ppblutoothkit.databinding.ActivityCalculate8acBinding
import com.lefu.ppblutoothkit.util.DataUtil
import com.lefu.ppblutoothkit.util.UnitUtil
import com.lefu.ppcalculate.PPBodyFatModel
import com.lefu.ppcalculate.vo.PPBodyDetailModel
import com.peng.ppscale.business.ble.PPScaleHelper

// 移除 kotlinx.android.synthetic 导入

/**
 * 8电极计算库-597平滑算法
 */
class Calculate8_SmoothActivitiy : BaseImmersivePermissionActivity() {

    var calcuteType: PPScaleDefine.PPDeviceCalcuteType? = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8
    var spinner: Spinner? = null
    var deviceName: String = ""

    /**
     * 身体年龄计算方式
     * 0默认计算方式 1-使用乐福自定义公式，2-使用计算库原始值； 0不填则保持原有方式（双频：原始值，其他算法：自定义）
     */
    var bodyAgeType: Int = 0
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
        adapter.add("Product6-CF597_N_Smooth")
        spinner?.setAdapter(adapter)
        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    calcuteType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_5
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
            val configDevice = PPScaleHelper.getConfigDevice(bodyBaseModel?.deviceModel?.deviceSettingId)
            bodyAgeType = configDevice?.advancedConfig?.bodyAgeType ?: 0
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

            calcuteType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_5
            spinner?.setSelection(0)

        }
    }


    /**
     * 测量一组数据A（测出来的阻抗A1），第一次测量，无需平滑算法，直接计算出身体数据，阻抗保存A1；
     * 测量一组数据B（测出来的阻抗B1），上一次使用A1，走平滑算法，得到身体数据和平滑后的阻抗B2，阻抗保存B2；
     * 测量一组数据C（测出来的阻抗C1），上一次使用B2，走平滑算法，得到身体数据和平滑后的阻抗C2，阻抗保存C2；
     * 测量一组数据D（测出来的阻抗D1），上一次使用c2，走平滑算法，得到身体数据和平滑后的阻抗D2，阻抗保存D2；
     *
     */
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

        bodyBaseModel.secret = SecretManager.getSecret(deviceModel.deviceCalcuteType.getType())
        bodyBaseModel.bodyAgeType = bodyAgeType

        val baseModel2 = createBaseModel2(bodyBaseModel)

        val fatModel = PPBodyFatModel(bodyBaseModel, baseModel2)
        val ppDetailModel = PPBodyDetailModel(fatModel)
        Log.d("liyp_", ppDetailModel.toString())

        DataUtil.bodyDataModel = fatModel
        Log.d("liyp_", fatModel.toString())

        val intent = Intent(this@Calculate8_SmoothActivitiy, BodyDataDetailActivity::class.java)
        startActivity(intent)
    }

    /**
     * 获取上一组数据，必须是上一组带体脂的数据，体重/身高/性别/年龄/10阻抗(上一组计算后的加密阻抗，并非原始阻抗)
     */
    fun createBaseModel2(bodyBaseModel: PPBodyBaseModel): PPBodyBaseModel {
        val bodyBaseModel2= PPBodyBaseModel()
        bodyBaseModel2.secret = bodyBaseModel.secret//计算密钥，一个App一个。
        bodyBaseModel2.weight = bodyBaseModel.weight//必须是真实的上一组数据体重
        bodyBaseModel2.userModel = bodyBaseModel.userModel//必须是真实的上一组数据性别/年龄/身高

        val z100KhzLeftArmEnCode = 1638170641L
        val z100KhzLeftLegEnCode = 273583225L
        val z100KhzRightArmEnCode = 1375395659L
        val z100KhzRightLegEnCode = 1911090953L
        val z100KhzTrunkEnCode = 1351901590L
        val z20KhzLeftArmEnCode = 1371679960L
        val z20KhzLeftLegEnCode = 4123498L
        val z20KhzRightArmEnCode = 566797253L
        val z20KhzRightLegEnCode = 23178490L
        val z20KhzTrunkEnCode = 275997162L

        bodyBaseModel2.z100KhzLeftArmEnCode = z100KhzLeftArmEnCode
        bodyBaseModel2.z100KhzLeftLegEnCode = z100KhzLeftLegEnCode
        bodyBaseModel2.z100KhzRightArmEnCode = z100KhzRightArmEnCode
        bodyBaseModel2.z100KhzRightLegEnCode = z100KhzRightLegEnCode
        bodyBaseModel2.z100KhzTrunkEnCode = z100KhzTrunkEnCode
        bodyBaseModel2.z20KhzLeftArmEnCode = z20KhzLeftArmEnCode
        bodyBaseModel2.z20KhzLeftLegEnCode = z20KhzLeftLegEnCode
        bodyBaseModel2.z20KhzRightArmEnCode = z20KhzRightArmEnCode
        bodyBaseModel2.z20KhzRightLegEnCode = z20KhzRightLegEnCode
        bodyBaseModel2.z20KhzTrunkEnCode = z20KhzTrunkEnCode

     return bodyBaseModel2
    }


}