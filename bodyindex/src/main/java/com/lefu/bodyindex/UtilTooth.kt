package com.lefu.bodyindex

import android.util.Log
import java.math.BigDecimal
import java.text.DecimalFormat

/**
 *    @author : whs
 *    e-mail : haisilen@163.com
 *    date   : 2023/3/25 15:44
 *    desc   : 单位转换工具
 */
object UtilTooth {


    /**
     * 四舍五入
     *
     * @param tempKg
     * @return
     */
    fun keep1Point2(tempKg: Double): Float {
        var tempKg = tempKg
        tempKg = (tempKg * 10000 + 5) / 10000
        val b = BigDecimal(tempKg)
        return b.setScale(2, BigDecimal.ROUND_HALF_UP).toFloat()
    }

    /**
     * 不四舍五入
     *
     *
     * 151.25
     * 15125
     *
     * @param tempKg
     * @return
     */
    fun keep1Point1_(tempKg: Double): Float {
        val intValue = (tempKg * 10).toInt()
        val value = intValue.toDouble() / 10
        val b = BigDecimal(value)
        return b.setScale(1, BigDecimal.ROUND_HALF_UP).toFloat()
    }

    /**
     * 分度值为0.05 KG转LB
     *
     * @param tempKg
     * @return
     */
    fun kgToLB_2Point_tofloat(tempKg: Double): Float {
        Log.d("kgToLB_2Point_tofloat", "" + tempKg)
        if (0.0 == tempKg) return 0.0f
        //        tempKg = 16.15;
        //636.55  == 637
        // 15.35 15350+5 15355 153.55 154
        val temp = Math.round(tempKg * 10).toInt()
        val value = temp / 10f //63.7
        val tempLb = Math.floor((value * 10 * 22046 / 10000).toDouble()).toInt()
        return keep1Point1((tempLb / 10.0f).toDouble())
    }

    /**
     * 四舍五入
     *
     * @param tempKg
     * @return
     */
    fun keep1Point1(tempKg: Double): Float {
        var tempKg = tempKg
        tempKg = (tempKg * 100 + 0.5) / 100
        val b = BigDecimal(tempKg)
        return b.setScale(1, BigDecimal.ROUND_HALF_UP).toFloat()
    }

    fun kgToJinPoint2(lb: Double): String {
        val kg = lb * 2
        val myformat = DecimalFormat("######0.00")
        return myformat.format(kg)
    }

    fun getKgString(htWeightKg: Double): String {
        val value: String = java.lang.String.valueOf(keep1Point2(htWeightKg))
        if (value.contains(".")) {
            val split = value.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            if (split != null && split.size >= 1) {
                var s = split[1]
                if (s.length < 2) {
                    s += "0"
                }
                return split[0] + "." + s + ""
            }
        } else {
            return "$value.00"
        }
        return value + ""
    }

    /**
     * 分度值为0.1   KG转LB 则LB 为无奇数显示
     *
     * @param tempKg
     * @return
     */
    fun kgToLB_Point_tofloat(tempKg: Double): Float {
        if (0.0 == tempKg) return 0.0f
        //        int temp = (int) ((tempKg * 100 + 5) / 10);
//        float tempFloat =  temp / 10.0f;
        var temp2_2046 = Math.floor(tempKg * 10 * 22046 / 10000).toInt()
        if (temp2_2046 % 2 > 0) {
            temp2_2046++
        }
        return keep1Point1((temp2_2046 / 10.0f).toDouble())
    }


    fun kgToJin(lb: Double): String {
        val kg = lb * 2
        val myformat = DecimalFormat("######0.0")
        return myformat.format(kg)
    }


}