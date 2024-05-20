package com.lefu.ppblutoothkit.device.apple.data;

import java.io.Serializable;

public class WifiDataBean implements Serializable {


    /**
     * sn : BW01LF1012600043
     * type : CF516
     * mac : 88:4a:18:5f:f6:c5
     * firmwareVersion : 1000.2.01025
     * WifiVersion : 1000.2.01025
     * charge : 0.45
     * Total index : 1
     * Current index : 1
     * weight : 13.30
     * impedance : 0
     * data_type : 0
     * rssi : -50
     * timestamp : 1616397011000
     */

    private String sn;
    private String type;
    private String mac;
    private String firmwareVersion;
    private String WifiVersion;
    private String charge;
    private double weight;
    private int impedance;
    private String data_type;
    private String rssi;
    private String timestamp;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getWifiVersion() {
        return WifiVersion;
    }

    public void setWifiVersion(String WifiVersion) {
        this.WifiVersion = WifiVersion;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getImpedance() {
        return impedance;
    }

    public void setImpedance(int impedance) {
        this.impedance = impedance;
    }

    public String getData_type() {
        return data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
