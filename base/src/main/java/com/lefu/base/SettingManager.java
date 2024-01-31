package com.lefu.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.lefu.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingManager {

    private final String SHARE_PREFERENCES = "set";
    private final String UID = "UID";
    public static final String USER_MODEL = "UserModel";

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

    public static SettingManager get() {
        return settingManager;
    }

    public String getUid() {
        return spf.getString(UID, "0");
    }

    public void setUid(String uid) {
        spf.edit().putString(UID, uid).commit();
    }

    /**
     * 保存对象
     *
     * @param tag
     * @param data
     */
    public <T> void setDataObj(String tag, T data) {
        if (null == data) {
            spf.edit().putString(tag, null).commit();
        } else {
            Gson gson = new Gson();
            //转换成json数据，再保存
            String strJson = gson.toJson(data);
            spf.edit().putString(tag, strJson).commit();
        }
    }

    public <T> T getDataObj(String tag, Class<T> clazz) {
        String strJson = spf.getString(tag, null);
        Gson gson = new Gson();
        if (strJson != null && !TextUtils.isEmpty(strJson)) {
            T dataArr = gson.fromJson(strJson, clazz);
            return dataArr;
        }
        return null;
    }

    /**
     * 保存List
     *
     * @param tag
     * @param datalist
     */
    public <T> void setDataList(String tag, List<T> datalist) {
        if (null == datalist || datalist.size() <= 0) {
            spf.edit().putString(tag, null).commit();
        } else {
            Gson gson = new Gson();
            //转换成json数据，再保存
            String strJson = gson.toJson(datalist);
//        spf.edit().clear();
            spf.edit().putString(tag, strJson).commit();
        }
    }

    /**
     * 获取List
     *
     * @param tag
     * @return
     */
    public <T> List<T> getDataList(String tag, Class<T[]> clazz) {
        String strJson = spf.getString(tag, null);
        Gson gson = new Gson();
        if (strJson != null && !TextUtils.isEmpty(strJson)) {
            T[] dataArr = gson.fromJson(strJson, clazz);
//           return gson.fromJson(strJson,new TypeToken<List<T>>(){}.getType());
            if (dataArr != null) {
                List<T> ts = Arrays.asList(dataArr);
                return new ArrayList<>(ts);
            }
        }
        return null;
    }


}
