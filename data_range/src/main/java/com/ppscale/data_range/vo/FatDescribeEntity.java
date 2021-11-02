package com.ppscale.data_range.vo;

import java.io.Serializable;

public class FatDescribeEntity implements Serializable {

    public int side;
    public int advice;

    public FatDescribeEntity(int side, int advice) {
        this.side = side;
        this.advice = advice;
    }

    public int getSide() {
        return side;
    }

    public void setSide(int side) {
        this.side = side;
    }

    public int getAdvice() {
        return advice;
    }

    public void setAdvice(int advice) {
        this.advice = advice;
    }
}
