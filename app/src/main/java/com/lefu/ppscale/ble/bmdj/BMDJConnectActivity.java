package com.lefu.ppscale.ble.bmdj;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.lefu.ppscale.db.dao.DBManager;
import com.lefu.ppscale.ble.R;
import com.lefu.ppscale.db.dao.DeviceModel;
import com.peng.ppscale.business.ble.BleOptions;
import com.peng.ppscale.business.ble.PPScale;
import com.peng.ppscale.business.ble.bmdj.PPBMDJConnectInterface;
import com.peng.ppscale.business.ble.listener.ProtocalFilterImpl;

import java.util.ArrayList;
import java.util.List;

public class BMDJConnectActivity extends AppCompatActivity {

    private boolean isAutoPush;
    private PPScale ppScale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.isAutoPush = false;
        setContentView(R.layout.activity_bmdjconnect);
        List<DeviceModel> deviceList = DBManager.manager().getDeviceList();

        List<String> addressList = new ArrayList<>();
        for (DeviceModel deviceMOdel : deviceList) {
            addressList.add(deviceMOdel.getDeviceMac());
        }

        ProtocalFilterImpl protocalFilter = new ProtocalFilterImpl();
        protocalFilter.setBmdjConnectInterface(new PPBMDJConnectInterface() {
            @Override
            public void monitorBMDJConnectSuccess() {
                isAutoPush = true;
                Intent intent = new Intent(BMDJConnectActivity.this, BMDJIntroduceActivity.class);
                startActivity(intent);
            }

            @Override
            public void monitorBMDJConnectFail() {

            }
        });
        BleOptions bleOptions = new BleOptions.Builder()
                .setFeaturesFlag(BleOptions.ScaleFeatures.FEATURES_BMDJ)
//                .setDeviceType(PPDeviceType.Contants.FAT_AND_BMDJ)
                .build();

        ppScale = new PPScale.Builder(getApplicationContext())
                .setDeviceList(addressList)
                .setBleOptions(bleOptions)
                .setProtocalFilterImpl(protocalFilter)
                .build();

        ppScale.enterBMDJModel();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isAutoPush) {
            ppScale.exitBMDJModel();
        }
    }
}
