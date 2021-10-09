package com.lefu.ppscale.wifi;

import android.database.sqlite.SQLiteDatabase;

import com.lefu.ppscale.wifi.dao.DeviceModelDao;
import com.lefu.ppscale.wifi.model.DeviceModel;
import com.peng.ppscale.vo.PPDeviceModel;

import java.util.List;

public class DBManager {
    private static DBManager dbManager;
    private DeviceModelDao deviceModelDao;
    private SQLiteDatabase sqLiteDatabase;

    public static DBManager manager() {
        if (dbManager == null) {
            synchronized (DBManager.class) {
                if (dbManager == null) {
                    dbManager = new DBManager();
                }
            }
        }
        return dbManager;
    }

    public DBManager() {
        deviceModelDao = PPApplication.getmDaoSession().getDeviceModelDao();
        sqLiteDatabase = PPApplication.getSqLiteDatabase();
    }

    public void insertDevice(PPDeviceModel model) {
        DeviceModel deviceModel = new DeviceModel(model.getDeviceMac(), model.getDeviceName(), model.deviceType.getType());
        deviceModelDao.insertOrReplace(deviceModel);
    }

    public void updateDevice(DeviceModel deviceModel) {
        deviceModelDao.update(deviceModel);
    }

    public void deleteDevice(DeviceModel model) {
        try {
            sqLiteDatabase.delete(DeviceModelDao.TABLENAME, DeviceModelDao.Properties.DeviceMac.columnName + "=?", new String[]{model.getDeviceMac()});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<DeviceModel> getDeviceList() {
        List<DeviceModel> deviceModelList = deviceModelDao.loadAll();
        return deviceModelList;
    }

    public DeviceModel getDevice(String mac) {
        DeviceModel deviceModel = deviceModelDao.queryBuilder().where(DeviceModelDao.Properties.DeviceMac.eq(mac)).unique();
        return deviceModel;
    }


}
