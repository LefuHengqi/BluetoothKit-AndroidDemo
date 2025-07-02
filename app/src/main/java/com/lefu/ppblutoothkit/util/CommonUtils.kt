package com.lefu.ppblutoothkit.util

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.provider.Settings


/**
 *    @author :
 *    e-mail : haisilen@163.com
 *    date   : 2023/5/5 11:02
 *    desc   : 通用工具类
 */
object CommonUtils {
    //是否是字符串
    fun isNumeric(str: String): Boolean {
        return try {
            str.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    /**
     * 判断当前手机是否为华为手机
     */
    fun isHuaweiOS(): Boolean {
        val brand = getDeviceBrand()
        return if (brand.equals("Huawei", ignoreCase = true) || brand.equals("Honor", ignoreCase = true)) {
            true
        } else {
            false
        }
    }

    private fun getDeviceBrand(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        val brand = Build.BRAND
         LogUtils.d("DeviceBrand", "Manufacturer: $manufacturer, Model: $model, Brand: $brand") // 打印设备信息，用于调试
        return if (manufacturer.equals("unknown", ignoreCase = true)) {
            brand
        } else {
            manufacturer
        }
    }

    fun isOpenBluetooth(): Boolean {
        val var0 = BluetoothAdapter.getDefaultAdapter()
        return var0?.isEnabled ?: false
    }

    @SuppressLint("MissingPermission")
    fun openOpenBluetooth(): Boolean {
        val defaultAdapter = BluetoothAdapter.getDefaultAdapter()
        return defaultAdapter?.enable() ?: false
    }

    fun isLocationEnabled(context: Context): Boolean {
        return isLocationEnabledS(context);
    }

    fun isLocationEnabledS(context: Context): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }


    fun openLocation(context: Context) {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        context.startActivity(intent)
    }



}