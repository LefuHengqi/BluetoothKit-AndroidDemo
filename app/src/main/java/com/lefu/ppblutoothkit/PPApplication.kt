package com.lefu.ppblutoothkit

import android.app.Application
import com.lefu.base.SettingManager
import com.lefu.ppblutoothkit.util.LogUtils
import com.lefu.ppblutoothkit.util.log.MyQueueLinkedUtils
import com.lefu.ppscale.db.dao.DBManager
import com.peng.ppscale.PPBlutoothKit
import com.peng.ppscale.util.OnLogCallBack

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
        PPBlutoothKit.setDebugLogCallBack(object : OnLogCallBack() {
            override fun logd(s: String?, s1: String?) {
                s1?.let { LogUtils.writeBluetoothLog(it) }
            }

            override fun logv(s: String?, s1: String?) {
//                s1?.let { LogUtils.writeBluetoothLog(it) }
            }

            override fun logw(s: String?, s1: String?) {
                s1?.let { LogUtils.writeBluetoothLog(it) }
            }

            override fun loge(s: String?, s1: String?) {
                s1?.let { LogUtils.writeBluetoothLog(it) }
            }

            override fun logi(s: String?, s1: String?) {
                s1?.let { LogUtils.writeBluetoothLog(it) }
            }
        })
        /*********************以下内容为SDK的配置项***************************************/
        //SDK日志打印控制，true会打印
        PPBlutoothKit.setDebug(true)
        /**
         * SDK 初始化 所需参数需要自行到开放平台自行申请，请勿直接使用Demo中的参数，
         * Demo中的参数仅供Demo使用
         * @param appKey App的标识
         * @param appSecret Appp的密钥
         * @param configPath 在开放平台下载相应的配置文件以.config结尾，并放到assets目录下，将config文件全名传给SDK
         */
        PPBlutoothKit.initSdk(this, appKey, Companion.appSecret, "lefu.config")

    }


}