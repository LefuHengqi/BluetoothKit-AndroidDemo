package com.lefu.ppblutoothkit.util

import android.content.Context
import com.lefu.ppscale.ble.R
import com.peng.ppscale.business.device.PPUnitType

object UnitUtil {

    fun unitText(context: Context, type: PPUnitType): String {
        return when (type) {
            PPUnitType.PPUnitG -> context.getString(R.string.string_unit_g)
            PPUnitType.PPUnitOZ -> context.getString(R.string.string_unit_oz)
            PPUnitType.PPUnitLBOZ -> context.getString(R.string.string_unit_lb_oz)
            PPUnitType.PPUnitFL_OZ_WATER -> context.getString(R.string.string_unit_fl_oz_water)
            PPUnitType.PPUnitFL_OZ_MILK -> context.getString(R.string.string_unit_fl_oz_milk)
            PPUnitType.PPUnitMLWater -> context.getString(R.string.string_unit_water_ml)
            PPUnitType.PPUnitMLMilk -> context.getString(R.string.string_unit_milk_ml)
            PPUnitType.Unit_LB -> context.getString(R.string.string_unit_lb)
            else -> ""
        }
    }

    /**
     * 处理精度问题
     */
    @JvmStatic
    fun getWeight(double: Double): Int {
        return ((double + 0.005) * 100).toInt()
    }


}