package com.lefu.ppblutoothkit.util

import com.lefu.ppbase.PPBodyBaseModel
import com.lefu.ppbase.PPDeviceModel
import com.lefu.ppbase.vo.PPUnitType
import com.lefu.ppbase.vo.PPUserGender
import com.lefu.ppbase.vo.PPUserModel
import com.lefu.ppcalculate.PPBodyFatModel

object DataUtil {
    var bodyDataModel: PPBodyFatModel? = null
    var bodyBaseModel: PPBodyBaseModel? = null
    var deviceModel: PPDeviceModel? = null
    var unit = PPUnitType.Unit_KG

    fun getUserModel(): PPUserModel {
        var userModel: PPUserModel? = SettingManager.get().getDataObj(SettingManager.USER_MODEL, PPUserModel::class.java)
        if (userModel == null) {
            userModel = PPUserModel.Builder()
                .setAge(18)
                .setHeight(180)
                .setSex(PPUserGender.PPUserGenderMale)
                .setGroupNum(0)
                .setUserID("0000")
                .setMemberId("00001")
                .build()
        }
        return userModel!!
    }

    fun setUserModel(userModel: PPUserModel) {
        SettingManager.get().setDataObj(SettingManager.USER_MODEL, userModel)
    }
}