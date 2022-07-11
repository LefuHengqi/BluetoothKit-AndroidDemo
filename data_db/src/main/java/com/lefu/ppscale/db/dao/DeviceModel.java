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
    /**
     * 设备类型
     *
     * @see PPScaleDefine.PPDeviceType
     */
    private int deviceType;
    private String sn;
    private String ssid;
    /**
     * 精度
     *
     * @see PPScaleDefine.PPDeviceAccuracyType
     */
    private int accuracyType;
    /**
     * 协议模式
     *
     * @see PPScaleDefine.PPDeviceProtocolType
     */
    private int deviceProtocolType;
    /**
     * 计算方式
     *
     * @see PPScaleDefine.PPDeviceCalcuteType
     */
    private int deviceCalcuteType;
    /**
     * 供电模式
     *
     * @see PPScaleDefine.PPDevicePowerType
     */
    public int devicePowerType;
    /**
     * 功能类型，可多功能叠加
     *
     * @see PPScaleDefine.PPDeviceFuncType
     */
    public int deviceFuncType;
    /**
     * 支持的单位
     *
     * @see PPScaleDefine.PPDeviceUnitType
     */
    public int deviceUnitType;
    /**
     * 电量
     */
    int devicePower = -1;
    /**
     * 固件版本号
     */
    String firmwareVersion;
    /**
     * 硬件版本号
     */
    String hardwareVersion;
    /**
     * 序列号
     */
    String serialNumber;

    public DeviceModel(String deviceMac, String deviceName, int deviceType) {
        this.deviceMac = deviceMac;
        this.deviceName = deviceName;
        this.deviceType = deviceType;
    }

    @Generated(hash = 960684195)
    public DeviceModel(Long id, String deviceMac, String deviceName, int deviceType,
            String sn, String ssid, int accuracyType, int deviceProtocolType,
            int deviceCalcuteType, int devicePowerType, int deviceFuncType,
            int deviceUnitType, int devicePower, String firmwareVersion,
            String hardwareVersion, String serialNumber) {
        this.id = id;
        this.deviceMac = deviceMac;
        this.deviceName = deviceName;
        this.deviceType = deviceType;
        this.sn = sn;
        this.ssid = ssid;
        this.accuracyType = accuracyType;
        this.deviceProtocolType = deviceProtocolType;
        this.deviceCalcuteType = deviceCalcuteType;
        this.devicePowerType = devicePowerType;
        this.deviceFuncType = deviceFuncType;
        this.deviceUnitType = deviceUnitType;
        this.devicePower = devicePower;
        this.firmwareVersion = firmwareVersion;
        this.hardwareVersion = hardwareVersion;
        this.serialNumber = serialNumber;
    }

    @Generated(hash = 210163102)
    public DeviceModel() {
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

    public int getAccuracyType() {
        return this.accuracyType;
    }

    public void setAccuracyType(int accuracyType) {
        this.accuracyType = accuracyType;
    }

    public int getDeviceProtocolType() {
        return this.deviceProtocolType;
    }

    public void setDeviceProtocolType(int deviceProtocolType) {
        this.deviceProtocolType = deviceProtocolType;
    }

    public int getDeviceCalcuteType() {
        return this.deviceCalcuteType;
    }

    public void setDeviceCalcuteType(int deviceCalcuteType) {
        this.deviceCalcuteType = deviceCalcuteType;
    }

    public int getDevicePowerType() {
        return this.devicePowerType;
    }

    public void setDevicePowerType(int devicePowerType) {
        this.devicePowerType = devicePowerType;
    }

    public int getDeviceFuncType() {
        return this.deviceFuncType;
    }

    public void setDeviceFuncType(int deviceFuncType) {
        this.deviceFuncType = deviceFuncType;
    }

    public int getDeviceUnitType() {
        return this.deviceUnitType;
    }

    public void setDeviceUnitType(int deviceUnitType) {
        this.deviceUnitType = deviceUnitType;
    }

    public int getDevicePower() {
        return this.devicePower;
    }

    public void setDevicePower(int devicePower) {
        this.devicePower = devicePower;
    }

    public String getFirmwareVersion() {
        return this.firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getHardwareVersion() {
        return this.hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public String getSerialNumber() {
        return this.serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }


}
