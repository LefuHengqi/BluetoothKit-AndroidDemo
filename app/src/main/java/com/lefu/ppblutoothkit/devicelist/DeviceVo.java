package com.lefu.ppblutoothkit.devicelist;

import com.peng.ppscale.vo.PPDeviceModel;

import java.io.Serializable;

public class DeviceVo implements Serializable {

    PPDeviceModel deviceModel;
    Double weightKg;

    public PPDeviceModel getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(PPDeviceModel deviceModel) {
        this.deviceModel = deviceModel;
    }

    public Double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(Double weightKg) {
        this.weightKg = weightKg;
    }
}
