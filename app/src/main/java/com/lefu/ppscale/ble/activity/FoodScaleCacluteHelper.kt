package com.lefu.ppscale.ble.activity

import android.content.Context
import com.lefu.ppscale.ble.UnitUtil
import com.peng.ppscale.business.device.PPUnitType
import com.peng.ppscale.util.DeviceType
import com.peng.ppscale.util.DeviceUtil
import com.peng.ppscale.util.Energy
import com.peng.ppscale.util.EnergyUnitLbOz
import com.peng.ppscale.vo.PPDeviceModel
import com.peng.ppscale.vo.PPScaleDefine

object FoodScaleCacluteHelper {

    @JvmStatic
    fun getValue(context: Context, weightG: Float, unit: PPUnitType, deviceModel: PPDeviceModel): String {
        DeviceType.deviceType = DeviceUtil.getDeviceType(deviceModel.getDeviceName())
        var valueStr = ""
        var value = weightG
//        if (foodScaleGeneral.thanZero == 0) {
//            value *= -1f
//        }
        val type = unit
        valueStr = if (deviceModel.deviceAccuracyType === PPScaleDefine.PPDeviceAccuracyType.PPDeviceAccuracyTypePoint01G) {
            //            String num = String.valueOf(value);
            val unit = Energy.toG(value, type)
            val num = unit.format01()
            val unitText = UnitUtil.unitText(context, type)
            num + unitText
        } else {
            val unit = Energy.toG(value, type)
            if (unit is EnergyUnitLbOz) {
                val split = ":"
                val values = unit.format().split(split.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val unitText = UnitUtil.unitText(context, type)
                val units = unitText.split(split.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                values[0] + split + values[1] + units[0] + split + units[1]
            } else {
                val num = unit.format()
                val unitText = UnitUtil.unitText(context, type)
                num + unitText
            }
        }
        return valueStr
    }


}