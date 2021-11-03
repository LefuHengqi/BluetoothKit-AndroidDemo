package com.lefu.ppscale.db.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

@Entity
public class DeviceModel {
    @Id(autoincrement = true)
    private Long id;
    @Unique
    private String deviceMac;
    private String deviceName;
    private int deviceType;
    private String sn;
    private String ssid;
    @Generated(hash = 1805901528)
    public DeviceModel(Long id, String deviceMac, String deviceName, int deviceType,
            String sn, String ssid) {
        this.id = id;
        this.deviceMac = deviceMac;
        this.deviceName = deviceName;
        this.deviceType = deviceType;
        this.sn = sn;
        this.ssid = ssid;
    }



    @Generated(hash = 210163102)
    public DeviceModel() {
    }

    public DeviceModel(String deviceMac, String deviceName, int deviceType) {
        this.deviceMac = deviceMac;
        this.deviceName = deviceName;
        this.deviceType = deviceType;
    }

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getDeviceMac() {
        return this.deviceMac;
    }
    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }
    public String getDeviceName() {
        return this.deviceName;
    }
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    public int getDeviceType() {
        return this.deviceType;
    }
    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }
    public String getSn() {
        return this.sn;
    }
    public void setSn(String sn) {
        this.sn = sn;
    }
    public String getSsid() {
        return this.ssid;
    }
    public void setSsid(String ssid) {
        this.ssid = ssid;
    }
    

}
