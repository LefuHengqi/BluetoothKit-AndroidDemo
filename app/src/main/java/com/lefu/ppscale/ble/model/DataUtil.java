package com.lefu.ppscale.ble.model;

import com.peng.ppscale.business.device.PPUnitType;
import com.peng.ppscale.vo.PPBodyFatModel;
import com.peng.ppscale.vo.PPUserModel;
import com.peng.ppscale.vo.PPUserGender;

public class DataUtil {
    private static DataUtil dataUtil;
    private PPBodyFatModel bodyDataModel;
    private PPUserModel userModel;
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
        userModel = new PPUserModel.Builder().setAge(18)
                .setHeight(180)
                .setSex(PPUserGender.PPUserGenderMale)
                .setGroupNum(0)
                .build();
    }

    public PPBodyFatModel getBodyDataModel() {
        return bodyDataModel;
    }

    public void setBodyDataModel(PPBodyFatModel bodyDataModel) {
        this.bodyDataModel = bodyDataModel;
    }

    public PPUserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(PPUserModel userModel) {
        this.userModel = userModel;
    }

    public void setUnit(PPUnitType unit) {
        this.unit = unit;
    }

    public PPUnitType getUnit() {
        return unit;
    }
}
