package com.lefu.ppblutoothkit.util;

import com.lefu.ppbase.vo.PPUnitType;
import com.lefu.ppbase.PPBodyBaseModel;
import com.lefu.ppcalculate.PPBodyFatModel;
import com.lefu.ppbase.PPDeviceModel;
import com.lefu.ppbase.vo.PPUserModel;
import com.lefu.ppbase.vo.PPUserGender;

public class DataUtil {
    private static DataUtil dataUtil;
    private PPBodyFatModel bodyDataModel;
    private PPBodyBaseModel bodyBaseModel;
    private PPUserModel userModel;
    private PPDeviceModel deviceModel;
    PPUnitType unit = PPUnitType.Unit_KG;

    public static DataUtil util() {
        if (dataUtil == null) {
            synchronized (DataUtil.class) {
                if (dataUtil == null) {
                    dataUtil = new DataUtil();
                }
            }
        }
        return dataUtil;
    }

    public DataUtil() {

    }

    public PPBodyFatModel getBodyDataModel() {
        return bodyDataModel;
    }

    public void setBodyDataModel(PPBodyFatModel bodyDataModel) {
        this.bodyDataModel = bodyDataModel;
    }

    public PPUserModel getUserModel() {
        userModel = SettingManager.get().getDataObj(SettingManager.USER_MODEL, PPUserModel.class);
        if (userModel == null) {
            userModel = new PPUserModel.Builder()
                    .setAge(18)
                    .setHeight(180)
                    .setSex(PPUserGender.PPUserGenderMale)
                    .setGroupNum(0)
                    .build();
        }
        return userModel;
    }

    public void setUserModel(PPUserModel userModel) {
        SettingManager.get().setDataObj(SettingManager.USER_MODEL, userModel);
    }

    public void setUnit(PPUnitType unit) {
        this.unit = unit;
    }

    public PPUnitType getUnit() {
        return unit;
    }

    public PPDeviceModel getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(PPDeviceModel deviceModel) {
        this.deviceModel = deviceModel;
    }


    public PPBodyBaseModel getBodyBaseModel() {
        return bodyBaseModel;
    }

    public void setBodyBaseModel(PPBodyBaseModel bodyBaseModel) {
        this.bodyBaseModel = bodyBaseModel;
    }
}
