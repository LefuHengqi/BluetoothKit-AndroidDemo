package com.lefu.ppblutoothkit

import android.app.Application
import com.lefu.base.SettingManager
import com.lefu.ppscale.ble.BuildConfig
import com.lefu.ppscale.db.dao.DBManager
import com.peng.ppscale.PPBlutoothKit

class PPApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DBManager.initGreenDao(this)
        PPBlutoothKit.setDebug(BuildConfig.DEBUG)
        SettingManager.get(this)
        //SDK 初始化
        PPBlutoothKit.initSdk(this)
        PPBlutoothKit.setDeviceConfigFilePath(this, "", "Device.json")
    }
}