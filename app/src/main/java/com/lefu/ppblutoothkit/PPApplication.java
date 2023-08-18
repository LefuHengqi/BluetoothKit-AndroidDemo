package com.lefu.ppblutoothkit;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;


import com.lefu.base.SettingManager;
import com.lefu.ppscale.ble.BuildConfig;
import com.lefu.ppscale.db.dao.DBManager;
import com.peng.ppscale.business.ble.PPScale;

public class PPApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DBManager.initGreenDao(this);
        PPScale.setDebug(BuildConfig.DEBUG);
        SettingManager.get(this);
    }
}

