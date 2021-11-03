package com.lefu.ppscale.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


import java.util.List;

public class DBManager {

    public static String tableName = "";

    private static DBManager dbManager;
    private static DeviceModelDao deviceModelDao;
    private static SQLiteDatabase sqLiteDatabase;

    private static DaoSession mDaoSession;

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
        deviceModelDao = getmDaoSession().getDeviceModelDao();
        sqLiteDatabase = getSqLiteDatabase();
    }

    public static void initGreenDao(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "pplibrary.db");
        sqLiteDatabase = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(sqLiteDatabase);
        mDaoSession = daoMaster.newSession();
    }

    public DaoSession getmDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getSqLiteDatabase() {
        return sqLiteDatabase;
    }


    public void insertDevice(DeviceModel model) {
        deviceModelDao.insertOrReplace(model);
    }

    public void deleteDevice(DeviceModel model) {
        try {
            sqLiteDatabase.delete(DeviceModelDao.TABLENAME, DeviceModelDao.Properties.DeviceMac.columnName + "=?", new String[]{model.getDeviceMac()});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<DeviceModel> getDeviceList() {
        return deviceModelDao.loadAll();
    }

    public void updateDevice(DeviceModel deviceModel) {
        deviceModelDao.update(deviceModel);
    }

    public DeviceModel getDevice(String mac) {
        return deviceModelDao.queryBuilder().where(DeviceModelDao.Properties.DeviceMac.eq(mac)).unique();
    }
}

