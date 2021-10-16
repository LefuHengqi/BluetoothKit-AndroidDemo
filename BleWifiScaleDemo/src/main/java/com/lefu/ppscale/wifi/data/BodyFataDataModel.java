package com.lefu.ppscale.wifi.data;

import com.peng.ppscale.business.device.PPUnitType;
import com.peng.ppscale.vo.PPBodyFatModel;
import com.peng.ppscale.vo.PPDeviceModel;
import com.peng.ppscale.vo.PPUserModel;

import java.io.Serializable;

public class BodyFataDataModel extends PPBodyFatModel implements Serializable {


    public BodyFataDataModel(double v, int i, PPUserModel ppUserModel, PPDeviceModel ppDeviceModel, PPUnitType ppUnitType) {
        super(v, i, ppUserModel, ppDeviceModel, ppUnitType);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
