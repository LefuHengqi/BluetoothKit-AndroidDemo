package com.lefu.ppblutoothkit

import android.app.Application
import com.lefu.ppbase.PPSDKKit
import com.lefu.ppblutoothkit.util.SettingManager
import com.lefu.ppblutoothkit.util.LogUtils
import com.lefu.ppblutoothkit.util.log.MyQueueLinkedUtils
import com.lefu.ppscale.db.dao.DBManager
import com.peng.ppscale.PPBluetoothKit
import com.lefu.ppbase.util.OnLogCallBack
import com.lefu.ppcalculate.PPCalculateKit


class PPApplication : Application() {

    companion object {
        //使用时请务必替换成你自己的AppKey/AppSecret，需要增加设备配置请查看：
        // https://xinzhiyun.feishu.cn/wiki/XU4ZwABN0iFWU0kr0cpcxy9onMd?from=from_copylink
        const val appKey = "lefub60060202a15ac8a"
        const val appSecret = "UCzWzna/eazehXaz8kKAC6WVfcL25nIPYlV9fXYzqDM="
    }

    override fun onCreate() {
        super.onCreate()
        
        //数据库初始化
        DBManager.initGreenDao(this)
        //SP缓存
        SettingManager.get(this)
        //日志写入文件
        MyQueueLinkedUtils.start(this)
        /**
         * SDK日志打印
         * SDK日志写入文件，App内日志管理可控
         */
        PPSDKKit.setDebugLogCallBack(object : OnLogCallBack() {
            override fun logd(s: String?, s1: String?) {

                s1?.let { LogUtils.writeBluetoothLog(it) }
            }

            override fun logi(s: String?, s1: String?) {

                s1?.let { LogUtils.writeBluetoothLog(it) }
            }

            override fun logv(s: String?, s1: String?) {
                s1?.let { LogUtils.writeBluetoothLog(it) }
            }

            override fun logw(s: String?, s1: String?) {
                s1?.let { LogUtils.writeBluetoothLog(it) }
            }

            override fun loge(s: String?, s1: String?) {
                s1?.let { LogUtils.writeBluetoothLog(it) }
            }
        })
        /*********************以下内容为SDK的配置项***************************************/
        /**
         *  SDK日志打印控制，true会打印
         */
        PPBluetoothKit.setDebug(true)
        /**
         * PPBluetoothKit 蓝牙库初始化 所需参数需要自行到开放平台自行申请，请勿直接使用Demo中的参数，
         * Demo中的参数仅供Demo使用
         * @param appKey App的标识
         * @param appSecret Appp的密钥
         * @param configPath 在开放平台下载相应的配置文件以.config结尾，并放到assets目录下，将config文件全名传给SDK
         */
//        PPBluetoothKit.initSdk(this, appKey, appSecret, "lefu.config")
        PPBluetoothKit.initSdk(this, appKey, appSecret, "Device.json")
        /**
         * PPCalculateKit 计算库初始化
         */
        PPCalculateKit.initSdk(this)
    }


}