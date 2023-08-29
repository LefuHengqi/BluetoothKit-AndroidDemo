package com.lefu.ppblutoothkit

import android.app.Application
import com.lefu.base.SettingManager
import com.lefu.ppscale.ble.BuildConfig
import com.lefu.ppscale.db.dao.DBManager
import com.peng.ppscale.business.ble.PPScale
import com.peng.ppscale.device.PPBlutoothKit

class PPApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DBManager.initGreenDao(this)
        PPScale.setDebug(BuildConfig.DEBUG)
        SettingManager.get(this)
        //SDK 初始化
        PPBlutoothKit.init(this)
    }
}