package com.lefu.test260h.setting;

import android.content.Context;
import android.content.SharedPreferences;

import org.jetbrains.annotations.Nullable;

public class SettingManager {

    private static SharedPreferences spf;
    private static SettingManager settingManager;

    private final String SHARE_PREFERENCES = "set";
    private Context context;

    private final String SETTING_TIME = "time";
    private final String SETTING_COUNT = "count";
    private final String SETTING_RSSI = "rssi";
    private final String SETTING_DEVICE_NAME = "deviceName";

    private SettingManager(Context context) {
        this.context = context;
        spf = context.getSharedPreferences(SHARE_PREFERENCES, Context.MODE_PRIVATE);
    }

    public static SettingManager getInstance(Context context) {
        if (settingManager == null) {
            synchronized (SettingManager.class) {
                if (settingManager == null) {
                    settingManager = new SettingManager(context.getApplicationContext());
                }
            }
        }
        return settingManager;
    }

    public static SettingManager get() {
        return settingManager;
    }

    public void clean() {

    }

    public void setTime(Long time) {
        spf.edit().putLong(SETTING_TIME, time).commit();
    }

    public void setCount(int count) {
        spf.edit().putInt(SETTING_COUNT, count).commit();
    }

    public void setDeviceName(@Nullable String deviceName) {
        spf.edit().putString(SETTING_DEVICE_NAME, deviceName).commit();
    }

    public void setRssi(int rssi) {
        spf.edit().putInt(SETTING_RSSI, rssi).commit();
    }

    public Long getTime() {
        return spf.getLong(SETTING_TIME, 10000L);
    }

    public int getCount() {
        return spf.getInt(SETTING_COUNT, 20);
    }

    public String getDeviceName() {
        return spf.getString(SETTING_DEVICE_NAME, "260H");
    }

    public int getRssi() {
        return spf.getInt(SETTING_RSSI, 60);
    }

}
