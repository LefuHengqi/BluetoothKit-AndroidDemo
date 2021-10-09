package com.lefu.ppscale.wifi.util;

import com.peng.ppscale.vo.PPBodyFatModel;
import com.peng.ppscale.vo.PPUserGender;
import com.peng.ppscale.vo.PPUserModel;

public class DataUtil {
    private static DataUtil dataUtil;
    private PPBodyFatModel bodyDataModel;
    private PPUserModel userModel;

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
}
