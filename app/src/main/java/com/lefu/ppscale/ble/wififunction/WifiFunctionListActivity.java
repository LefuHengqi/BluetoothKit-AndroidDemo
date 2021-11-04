package com.lefu.ppscale.ble.wififunction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lefu.ppscale.ble.R;
import com.lefu.ppscale.ble.model.DataUtil;
import com.lefu.ppscale.wifi.activity.BleConfigWifiActivity;
import com.lefu.ppscale.wifi.data.WifiDataListActivity;
import com.peng.ppscale.business.ble.PPScale;

public class WifiFunctionListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_function_list);

        Button mBtnConfigWifi = findViewById(R.id.wificonfigBtn);
        mBtnConfigWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PPScale.isBluetoothOpened()) {
                    Intent intent = new Intent(WifiFunctionListActivity.this, BleConfigWifiActivity.class);
                    startActivity(intent);
                } else {
                    PPScale.openBluetooth();
                }
            }
        });

        Button dataListBtn = findViewById(R.id.dataListBtn);
        dataListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WifiFunctionListActivity.this, WifiDataListActivity.class);
                intent.putExtra("userinfo", DataUtil.util().getUserModel());
//                intent.putExtra("unit", PPUtil.getWeightUnitNum(DataUtil.util().getUnit()));
                startActivity(intent);
            }
        });

    }

}
