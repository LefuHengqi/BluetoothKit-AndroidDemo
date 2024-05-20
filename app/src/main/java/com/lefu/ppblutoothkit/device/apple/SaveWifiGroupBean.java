package com.lefu.ppblutoothkit.device.apple;

import java.io.Serializable;

public class SaveWifiGroupBean implements Serializable {

    int code;

    String msg;

    boolean status;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
