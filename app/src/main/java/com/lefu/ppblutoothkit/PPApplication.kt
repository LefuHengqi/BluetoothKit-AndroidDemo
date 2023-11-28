package com.lefu.ppblutoothkit

import android.app.Application
import com.lefu.base.SettingManager
import com.lefu.ppscale.db.dao.DBManager
import com.peng.ppscale.PPBlutoothKit

class PPApplication : Application() {

    //使用时请务必替换成你自己的AppKey/AppSecret，需要增加设备配置请联系我司销售人员
    val appKey = "lefu35768440e41a2fdf"
    val appSecret = "IULNd/zBWP71d9bf80i7zrP47py2qFicwVvYEeG5kZo="

    override fun onCreate() {
        super.onCreate()
        //数据库初始化
        DBManager.initGreenDao(this)
        //SP缓存
        SettingManager.get(this)
        /*********************以下内容为SDK的配置项***************************************/
        //SDK日志打印控制，true会打印
        PPBlutoothKit.setDebug(BuildConfig.DEBUG)
        /**
         * SDK 初始化 所需参数需要自行到开放平台自行申请，请勿直接使用Demo中的参数，
         * @param appKey App的标识
         * @param appSecret Appp的密钥
         * @param configPath 在开放平台下载相应的配置文件以.config结尾，并放到assets目录下，将config文件全名传给SDK
         */
        PPBlutoothKit.initSdk(this, appKey, appSecret, "lefu.config")
    }
}