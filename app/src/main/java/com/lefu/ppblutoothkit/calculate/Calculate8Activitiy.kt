package com.lefu.ppblutoothkit.calculate

import android.app.Activity
import android.content.Intent
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
import com.lefu.ppbase.vo.PPUserGender
import com.lefu.ppbase.vo.PPUserModel
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.util.DataUtil
import com.lefu.ppblutoothkit.util.UnitUtil
import com.lefu.ppcalculate.PPBodyFatModel
import kotlinx.android.synthetic.main.activity_calculate_8ac.*

/**
 * 8电极计算库
 */
class Calculate8Activitiy : Activity() {

    var calcuteType: PPScaleDefine.PPDeviceCalcuteType? = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8//CF577系列
    var spinner: Spinner? = null

    var deviceName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculate_8ac)
        findViewById<Button>(R.id.calculateBtn).setOnClickListener {
            startCalculate()
        }
        spinner = findViewById<Spinner>(R.id.spinner)
        val adapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter.add("Product1-CF577")
        adapter.add("Product3-CF586")
        adapter.add("Product4-CF597")
        adapter.add("Product0-CF610")
        adapter.add("Product5-CF577_N1")
        adapter.add("Product6-CF597_N")
        spinner?.setAdapter(adapter)
        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    calcuteType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8//8电极算法, bhProduct =1--CF577
                } else if (position == 1) {
                    calcuteType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_1//8电极算法，bhProduct =3--CF586
                } else if (position == 2) {
                    calcuteType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_0//8电极算法，bhProduct =4 --CF597
                } else if (position == 3) {
                    calcuteType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_2//8电极算法，bhProduct =0 --CF610
                } else if (position == 4) {
                    calcuteType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_3//8电极算法，bhProduct =5 --CF577_N1
                } else if (position == 5) {
                    calcuteType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_4//8电极算法，bhProduct =6 --CF597_N
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
        initData()
    }

    private fun initData() {
        val tag = intent.getStringExtra("bodyDataModel")
        if (tag != null) {
            //显示称重完成后的数据
            val bodyBaseModel = DataUtil.util().bodyBaseModel
            deviceName = bodyBaseModel.deviceModel?.deviceName ?: ""
            etSex.setText(if (bodyBaseModel?.userModel?.sex == PPUserGender.PPUserGenderFemale) "0" else "1")
            etHeight.setText(bodyBaseModel?.userModel?.userHeight.toString())
            etAge.setText(bodyBaseModel?.userModel?.age.toString())
            etWeight.setText(bodyBaseModel?.getPpWeightKg().toString())
            z100KhzLeftArmEnCode.setText(bodyBaseModel?.z100KhzLeftArmEnCode.toString())
            z100KhzLeftLegEnCode.setText(bodyBaseModel?.z100KhzLeftLegEnCode.toString())
            z100KhzRightArmEnCode.setText(bodyBaseModel?.z100KhzRightArmEnCode.toString())
            z100KhzRightLegEnCode.setText(bodyBaseModel?.z100KhzRightLegEnCode.toString())
            z100KhzTrunkEnCode.setText(bodyBaseModel?.z100KhzTrunkEnCode.toString())
            z20KhzLeftArmEnCode.setText(bodyBaseModel?.z20KhzLeftArmEnCode.toString())
            z20KhzLeftLegEnCode.setText(bodyBaseModel?.z20KhzLeftLegEnCode.toString())
            z20KhzRightArmEnCode.setText(bodyBaseModel?.z20KhzRightArmEnCode.toString())
            z20KhzRightLegEnCode.setText(bodyBaseModel?.z20KhzRightLegEnCode.toString())
            z20KhzTrunkEnCode.setText(bodyBaseModel?.z20KhzTrunkEnCode.toString())

            calcuteType = bodyBaseModel?.deviceModel?.deviceCalcuteType
            if (calcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8) {
                // 8电极交流算法, bhProduct=1--CF577
                spinner?.setSelection(0)
            } else if (calcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_1) {
                // 8电极算法，bhProduct =3 --CF586
                spinner?.setSelection(1)
            } else if (calcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_0) {
                // 8电极算法，bhProduct =4 --CF597
                spinner?.setSelection(2)
            } else if (calcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_2) {
                // 8电极算法，bhProduct =0 --CF610
                spinner?.setSelection(3)
            } else if (calcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_3) {
                // 8电极算法，bhProduct =5 --CF577_N1
                spinner?.setSelection(4)
            } else if (calcuteType == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_4) {
                // 8电极算法，bhProduct =6 --CF597_N
                spinner?.setSelection(5)
            }
        }
    }

    private fun startCalculate() {
        val sex = if (etSex.text?.toString()?.toInt() == 0) {
            PPUserGender.PPUserGenderFemale
        } else {
            PPUserGender.PPUserGenderMale
        }
        val height = etHeight.text?.toString()?.toInt() ?: 168
        val age = etAge.text?.toString()?.toInt() ?: 35
        val weight = etWeight.text?.toString()?.toDouble() ?: 83.00

        val z100KhzLeftArmEnCode = z100KhzLeftArmEnCode.text?.toString()?.toLong() ?: 294794323L
        val z100KhzLeftLegEnCode = z100KhzLeftLegEnCode.text?.toString()?.toLong() ?: 806102147L
        val z100KhzRightArmEnCode = z100KhzRightArmEnCode.text?.toString()?.toLong() ?: 26360525L
        val z100KhzRightLegEnCode = z100KhzRightLegEnCode.text?.toString()?.toLong() ?: 816581534L
        val z100KhzTrunkEnCode = z100KhzTrunkEnCode.text?.toString()?.toLong() ?: 1080247226L
        val z20KhzLeftArmEnCode = z20KhzLeftArmEnCode.text?.toString()?.toLong() ?: 27983001L
        val z20KhzLeftLegEnCode = z20KhzLeftLegEnCode.text?.toString()?.toLong() ?: 837194050L
        val z20KhzRightArmEnCode = z20KhzRightArmEnCode.text?.toString()?.toLong() ?: 1634195706L
        val z20KhzRightLegEnCode = z20KhzRightLegEnCode.text?.toString()?.toLong() ?: 29868463L
        val z20KhzTrunkEnCode = z20KhzTrunkEnCode.text?.toString()?.toLong() ?: 1881406429L

//        val age 	=	27
//        val height 	=	180
//        val weight 	=	77.65
//        val sex 	=	PPUserGender.PPUserGenderMale
//        val z100KhzLeftArmEnCode 	=	1365273936L
//        val z100KhzLeftLegEnCode 	=	537581477L
//        val z100KhzRightArmEnCode 	=	1362153929L
//        val z100KhzRightLegEnCode 	=	1088101585L
//        val z100KhzTrunkEnCode 	=	1075910384L
//        val z20KhzLeftArmEnCode 	=	545197126L
//        val z20KhzLeftLegEnCode 	=	569465508L
//        val z20KhzRightArmEnCode 	=	921094L
//        val z20KhzRightLegEnCode 	=	837068764L
//        val z20KhzTrunkEnCode 	=	14330286L

//        val z100KhzLeftArmEnCode=569602315L
//        val z100KhzLeftLegEnCode=809255489L
//        val z100KhzRightArmEnCode=2895252L
//        val z100KhzRightLegEnCode=1886303910L
//        val z100KhzTrunkEnCode=4345946L
//        val z20KhzLeftArmEnCode=538898029L
//        val z20KhzLeftLegEnCode=1100272835L
//        val z20KhzRightArmEnCode=1374763886L
//        val z20KhzRightLegEnCode=1615689850L
//        val z20KhzTrunkEnCode=1620566014L

        val userModel = PPUserModel.Builder()
            .setSex(sex) //gender
            .setHeight(height)//height 100-220
            .setAge(age)//age 10-99
            .build()

        val deviceModel = PPDeviceModel("", deviceName)//Select the corresponding Bluetooth name according to your own device
        deviceModel.deviceCalcuteType = calcuteType ?: PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8
        val bodyBaseModel = PPBodyBaseModel()
        bodyBaseModel.weight = UnitUtil.getWeight(weight)
        bodyBaseModel.deviceModel = deviceModel
        bodyBaseModel.userModel = userModel

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

        val fatModel = PPBodyFatModel(bodyBaseModel)

        DataUtil.util().bodyDataModel = fatModel
        Log.d("liyp_", fatModel.toString())

        val intent = Intent(this@Calculate8Activitiy, BodyDataDetailActivity::class.java)
        startActivity(intent)
    }

}