package com.lefu.ppblutoothkit.device.apple.data;

import com.peng.ppscale.vo.PPBodyBaseModel;
import com.peng.ppscale.vo.PPBodyFatModel;

import java.io.Serializable;

public class BodyFataDataModel extends PPBodyFatModel implements Serializable {


    public BodyFataDataModel(PPBodyBaseModel bodyBaseModel) {
        super(bodyBaseModel);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
