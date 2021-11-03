package com.lefu.ppscale.wifi;

import android.content.Context;
import android.content.SharedPreferences;

import com.peng.ppscale.util.Logger;

public class SettingManager {

    private final String SHARE_PREFERENCES = "set";
    private final String UID = "UID";

    private static SettingManager settingManager;
    private static SharedPreferences spf;

    private SettingManager(Context context) {
        spf = context.getSharedPreferences(SHARE_PREFERENCES, Context.MODE_PRIVATE);
    }


    public static SettingManager get(Context context) {
        if (settingManager == null) {
            synchronized (SettingManager.class) {
                if (settingManager == null) {
                    settingManager = new SettingManager(context);
                }
            }
        }
        return settingManager;
    }

    public String getUid() {
        return spf.getString(UID, "");
    }

    public void setUid(String uid) {
        Logger.d("uid" + uid);
        spf.edit().putString(UID, uid).commit();
    }


}
