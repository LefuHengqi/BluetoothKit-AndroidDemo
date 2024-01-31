package com.lefu.ppblutoothkit.device.apple;

import java.io.Serializable;
import java.util.List;

public class WifiDataVo implements Serializable {


    /**
     * code : 200
     * msg : success
     * status : true
     * data : [{"id":1373894800331337700,"uid":"679d8b6a-b18e-48cd-9f33-362f3ea9546a","snNum":"BW01LF1012600043","weightJson":"{"sn\":\"BW01LF1012600043\",\"type\":\"CF516\",\"mac\":\"88:4a:18:5f:f6:c5\",\"firmwareVersion\":\"1000.2.01025\",\"WifiVersion\":\"1000.2.01025\",\"charge\":\"0.45\",\"Total index\":\"1\",\"Current index\":\"1\",\"weight\":\"13.30\",\"impedance\":\"0\",\"data_type\":\"0\",\"rssi\":\"-50\",\"timestamp\":\"1616397011000\"}"},{"id":1373894801224724500,"uid":"679d8b6a-b18e-48cd-9f33-362f3ea9546a","snNum":"BW01LF1012600043","weightJson":"{\"sn\":\"BW01LF1012600043\",\"type\":\"CF516\",\"mac\":\"88:4a:18:5f:f6:c5\",\"firmwareVersion\":\"1000.2.01025\",\"WifiVersion\":\"1000.2.01025\",\"charge\":\"0.51\",\"Total index\":\"1\",\"Current index\":\"1\",\"weight\":\"13.10\",\"impedance\":\"0\",\"data_type\":\"0\",\"rssi\":\"-55\",\"timestamp\":\"1616393764000\"}"}]
     */

    private int code;
    private String msg;
    private boolean status;
    private List<Data> data;

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

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public static class Data implements Serializable{
        /**
         * id : 1373894800331337700
         * uid : 679d8b6a-b18e-48cd-9f33-362f3ea9546a
         * snNum : BW01LF1012600043
         * weightJson : {"sn":"BW01LF1012600043","type":"CF516","mac":"88:4a:18:5f:f6:c5","firmwareVersion":"1000.2.01025","WifiVersion":"1000.2.01025","charge":"0.45","Total index":"1","Current index":"1","weight":"13.30","impedance":"0","data_type":"0","rssi":"-50","timestamp":"1616397011000"}
         */

        private long id;
        private String uid;
        private String snNum;
        private String weightJson;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getSnNum() {
            return snNum;
        }

        public void setSnNum(String snNum) {
            this.snNum = snNum;
        }

        public String getWeightJson() {
            return weightJson;
        }

        public void setWeightJson(String weightJson) {
            this.weightJson = weightJson;
        }
    }
}
