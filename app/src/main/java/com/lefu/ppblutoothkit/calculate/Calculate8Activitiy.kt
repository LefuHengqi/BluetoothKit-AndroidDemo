package com.lefu.ppblutoothkit.calculate

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.util.UnitUtil
import com.lefu.ppblutoothkit.util.DataUtil
import com.peng.ppscale.business.device.DeviceManager
import com.peng.ppscale.util.DeviceUtil
import com.peng.ppscale.vo.*
import kotlinx.android.synthetic.main.activity_calculate_8ac.*

/**
 * 8电极计算库
 */
class Calculate8Activitiy : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculate_8ac)

        findViewById<Button>(R.id.calculateBtn).setOnClickListener {
            startCalculate()
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
//"weight":"83.00",
// "z100KhzLeftArmEnCode":"545156739",
// "z100KhzLeftLegEnCode":"541890251",
// "z100KhzRightArmEnCode":"828433987",
// "z100KhzRightLegEnCode":"1352567256",
// "z100KhzTrunkEnCode":"12179942",
// "z20KhzLeftArmEnCode":"15600033",
// "z20KhzLeftLegEnCode":"7027077",
// "z20KhzRightArmEnCode":"24374673",
// "z20KhzRightLegEnCode":"270243386",
// "z20KhzTrunkEnCode":"12247378"}
        val z100KhzLeftArmEnCode = z100KhzLeftArmEnCode.text?.toString()?.toLong() ?: 294794323
        val z100KhzLeftLegEnCode = z100KhzLeftLegEnCode.text?.toString()?.toLong() ?: 806102147
        val z100KhzRightArmEnCode = z100KhzRightArmEnCode.text?.toString()?.toLong() ?: 26360525
        val z100KhzRightLegEnCode = z100KhzRightLegEnCode.text?.toString()?.toLong() ?: 816581534
        val z100KhzTrunkEnCode = z100KhzTrunkEnCode.text?.toString()?.toLong() ?: 1080247226
        val z20KhzLeftArmEnCode = z20KhzLeftArmEnCode.text?.toString()?.toLong() ?: 27983001
        val z20KhzLeftLegEnCode = z20KhzLeftLegEnCode.text?.toString()?.toLong() ?: 837194050
        val z20KhzRightArmEnCode = z20KhzRightArmEnCode.text?.toString()?.toLong() ?: 1634195706
        val z20KhzRightLegEnCode = z20KhzRightLegEnCode.text?.toString()?.toLong() ?: 29868463
        val z20KhzTrunkEnCode = z20KhzTrunkEnCode.text?.toString()?.toLong() ?: 1881406429


//        val age 	=	41
//        val height 	=	168
//        val weight 	=	56.55
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

        val userModel = PPUserModel.Builder()
            .setSex(sex) //gender
            .setHeight(height)//height 100-220
            .setAge(age)//age 10-99
            .build()

        val deviceModel = PPDeviceModel("", DeviceManager.CF568_CF577)//Select the corresponding Bluetooth name according to your own device
        deviceModel.deviceCalcuteType = PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeAlternate8_1
        deviceModel.deviceAccuracyType = if (DeviceUtil.Point2_Scale_List.contains(deviceModel.deviceName)) {
            PPScaleDefine.PPDeviceAccuracyType.PPDeviceAccuracyTypePoint005
        } else {
            PPScaleDefine.PPDeviceAccuracyType.PPDeviceAccuracyTypePoint01
        }
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