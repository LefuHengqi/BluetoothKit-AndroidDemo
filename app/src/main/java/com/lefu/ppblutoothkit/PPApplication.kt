package com.lefu.ppblutoothkit

import android.app.Application
import com.lefu.base.SettingManager
import com.lefu.ppscale.ble.BuildConfig
import com.lefu.ppscale.db.dao.DBManager
import com.peng.ppscale.PPBlutoothKit

class PPApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //数据库初始化
        DBManager.initGreenDao(this)
        //SDK日志打印控制，true会打印
        PPBlutoothKit.setDebug(BuildConfig.DEBUG)
        //SP缓存
        SettingManager.get(this)
        /**
         * SDK 初始化
         * @param secretKey 在开放管理平台指定项目的appKey
         * @param configPath 设置支持的设备列表，要配置你自己在开放管理平台去下载相应的Json文件，并放到assets目录下，将Json文件全名传给SDK
         */
        PPBlutoothKit.initSdk(this, "", "Device.json")
    }
}