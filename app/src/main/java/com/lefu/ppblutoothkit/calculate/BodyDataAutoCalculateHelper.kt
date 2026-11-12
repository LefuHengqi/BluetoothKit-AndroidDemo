package com.lefu.ppblutoothkit.calculate

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.lefu.ppbase.PPBodyBaseModel
import com.lefu.ppbase.PPDeviceModel
import com.lefu.ppbase.util.Logger
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.SecretManager
import com.lefu.ppblutoothkit.util.DataUtil
import com.lefu.ppcalculate.PPBodyFatModel
import com.peng.ppscale.business.ble.PPScaleHelper

/**
 * 测量完成后的自动计算。
 *
 * 用设备回调的 [PPBodyBaseModel] + 当前用户信息直接构造 [PPBodyFatModel]，
 * 然后进入「计算结果」页（[BodyDataDetailActivity]），由它再叠加「测量结果」页。
 */
object BodyDataAutoCalculateHelper {

    fun calculateAndOpen(activity: Activity, bodyBaseModel: PPBodyBaseModel, deviceModel: PPDeviceModel?) {
        if (activity.isFinishing || activity.isDestroyed) return

        val targetDeviceModel = bodyBaseModel.deviceModel ?: deviceModel ?: return
        bodyBaseModel.deviceModel = targetDeviceModel
        if (bodyBaseModel.userModel == null) {
            bodyBaseModel.userModel = DataUtil.getUserModel()
        }

        val calcuteType = targetDeviceModel.deviceCalcuteType.getType()
        //调用计算库前必须补齐 secret 和身体年龄计算方式
        bodyBaseModel.secret = SecretManager.getSecret(calcuteType)
        bodyBaseModel.bodyAgeType = PPScaleHelper.getConfigDevice(targetDeviceModel.deviceSettingId)
            ?.advancedConfig?.bodyAgeType ?: 0

        DataUtil.bodyBaseModel = bodyBaseModel

        val fatModel = try {
            PPBodyFatModel(bodyBaseModel)
        } catch (t: Throwable) {
            Logger.e("BodyDataAutoCalculateHelper calculate fail: ${t.message}")
            Toast.makeText(activity, R.string.calculate_fail, Toast.LENGTH_SHORT).show()
            return
        }

        Logger.d(
            "BodyDataAutoCalculateHelper calcuteType=$calcuteType " +
                    "impedance=${bodyBaseModel.impedance} weight=${bodyBaseModel.getPpWeightKg()} " +
                    "errorType=${fatModel.errorType}"
        )

        DataUtil.bodyDataModel = fatModel
        activity.startActivity(Intent(activity, BodyDataDetailActivity::class.java))
    }
}