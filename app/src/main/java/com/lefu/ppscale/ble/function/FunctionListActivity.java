package com.lefu.ppscale.ble.function;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lefu.ppscale.ble.R;
import com.lefu.ppscale.ble.activity.BindingDeviceActivity;
import com.lefu.ppscale.ble.activity.OTAActivity;
import com.lefu.ppscale.ble.activity.ReadHistoryListActivity;
import com.lefu.ppscale.ble.activity.ScanDeviceListActivity;
import com.lefu.ppscale.ble.bmdj.BMDJConnectActivity;
import com.lefu.ppscale.ble.model.DataUtil;
import com.lefu.ppscale.ble.wififunction.WifiFunctionListActivity;
import com.lefu.ppscale.db.dao.DBManager;
import com.lefu.ppscale.db.dao.DeviceModel;
import com.lefu.ppscale.wifi.activity.BleConfigWifiActivity;
import com.lefu.ppscale.wifi.data.WifiDataListActivity;
import com.lefu.ppscale.wifi.develop.DeveloperActivity;
import com.peng.ppscale.business.ble.PPScale;
import com.peng.ppscale.vo.PPScaleDefine;

public class FunctionListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_list);

        final String address = getIntent().getStringExtra("address");

        findViewById(R.id.eadHistoryListBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //读取历史数据
                if (!TextUtils.isEmpty(address)) {
                    DeviceModel device = DBManager.manager().getDevice(address);
                    if (device != null &&
                            (device.getDeviceType() & PPScaleDefine.PPDeviceFuncType.PPDeviceFuncTypeHistory.getType())
                                    == PPScaleDefine.PPDeviceFuncType.PPDeviceFuncTypeHistory.getType()) {
                        Intent intent = new Intent(FunctionListActivity.this, ReadHistoryListActivity.class);
                        intent.putExtra("address", address);
                        startActivity(intent);
                    } else {
                        Toast.makeText(FunctionListActivity.this, getString(R.string.device_not_aupported), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        findViewById(R.id.bmdjBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(address)) {
                    DeviceModel device = DBManager.manager().getDevice(address);
                    if (device != null &&
                            (device.getDeviceType() & PPScaleDefine.PPDeviceFuncType.PPDeviceFuncTypeBMDJ.getType())
                                    == PPScaleDefine.PPDeviceFuncType.PPDeviceFuncTypeBMDJ.getType()) {
                        Intent intent = new Intent(FunctionListActivity.this, BMDJConnectActivity.class);
                        intent.putExtra("address", address);
                        startActivity(intent);
                    } else {
                        Toast.makeText(FunctionListActivity.this, getString(R.string.device_not_aupported), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

//        findViewById(R.id.otaBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(FunctionListActivity.this, OTAActivity.class);
//                startActivity(intent);
//            }
//        });

        findViewById(R.id.developerBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(address)) {
                    DeviceModel device = DBManager.manager().getDevice(address);
                    if (device != null && device.getDeviceType() == PPScaleDefine.PPDeviceType.PPDeviceTypeCC.getType()) {
                        Intent intent = new Intent(FunctionListActivity.this, DeveloperActivity.class);
                        intent.putExtra("address", address);
                        startActivity(intent);
                    } else {
                        Toast.makeText(FunctionListActivity.this, getString(R.string.device_not_aupported), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        Button mBtnConfigWifi = findViewById(R.id.wificonfigBtn);
        mBtnConfigWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(address)) {
                    DeviceModel device = DBManager.manager().getDevice(address);
                    if (device != null && device.getDeviceType() == PPScaleDefine.PPDeviceType.PPDeviceTypeCC.getType()) {
                        if (PPScale.isBluetoothOpened()) {
                            Intent intent = new Intent(FunctionListActivity.this, BleConfigWifiActivity.class);
                            intent.putExtra("address", address);
                            startActivity(intent);
                        } else {
                            PPScale.openBluetooth();
                        }
                    } else {
                        Toast.makeText(FunctionListActivity.this, getString(R.string.device_not_aupported), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        Button dataListBtn = findViewById(R.id.dataListBtn);
        dataListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PPScale.isBluetoothOpened()) {
                    //读取服务端存的该用户下的数据
                    Intent intent = new Intent(FunctionListActivity.this, WifiDataListActivity.class);
                    intent.putExtra("userinfo", DataUtil.util().getUserModel());
//                intent.putExtra("unit", PPUtil.getWeightUnitNum(DataUtil.util().getUnit()));
                    startActivity(intent);
                } else {
                    PPScale.openBluetooth();
                }
            }
        });


    }

    private void showDialog() {
        String content = getString(R.string.please_select_function);
        AlertDialog.Builder builder = new AlertDialog.Builder(FunctionListActivity.this);
        builder.setMessage(content);
        builder.setPositiveButton(R.string.bmdj, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton(R.string.measure_weight, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (PPScale.isBluetoothOpened()) {
                    Intent intent = new Intent(FunctionListActivity.this, BindingDeviceActivity.class);
                    intent.putExtra(BindingDeviceActivity.SEARCH_TYPE, 1);
                    startActivity(intent);
                } else {
                    PPScale.openBluetooth();
                }
            }
        });
        builder.show();
    }


}
