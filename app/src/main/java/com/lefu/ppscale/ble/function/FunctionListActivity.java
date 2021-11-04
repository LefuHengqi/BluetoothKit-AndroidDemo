package com.lefu.ppscale.ble.function;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lefu.ppscale.ble.R;
import com.lefu.ppscale.ble.activity.BindingDeviceActivity;
import com.lefu.ppscale.ble.activity.ReadHistoryListActivity;
import com.lefu.ppscale.ble.activity.ScanDeviceListActivity;
import com.lefu.ppscale.ble.bmdj.BMDJConnectActivity;
import com.peng.ppscale.business.ble.PPScale;

public class FunctionListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_list);

        Button mBtnSearchDevice = findViewById(R.id.searchDeviceListBtn);
        mBtnSearchDevice.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //扫描设备列表
                Intent intent = new Intent(FunctionListActivity.this, ScanDeviceListActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.eadHistoryListBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //读取历史数据
                Intent intent = new Intent(FunctionListActivity.this, ReadHistoryListActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.bmdjBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FunctionListActivity.this, BMDJConnectActivity.class);
                startActivity(intent);
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
