package com.lefu.ppscale.ble.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.lefu.base.SettingManager;
import com.lefu.ppscale.ble.R;
import com.lefu.ppscale.ble.adapter.DeviceListAdapter;
import com.lefu.ppscale.ble.function.FunctionListActivity;
import com.lefu.ppscale.ble.torre.DeviceSetActivity;
import com.lefu.ppscale.db.dao.DBManager;
import com.lefu.ppscale.db.dao.DeviceModel;
import com.lefu.ppscale.wifi.activity.BleConfigWifiActivity;
import com.lefu.ppscale.wifi.develop.DeveloperActivity;
import com.lefu.ppscale.wifi.net.okhttp.NetHelper;
import com.peng.ppscale.vo.PPScaleDefine;

import java.util.List;

public class DeviceListActivity extends AppCompatActivity {

    private DeviceListAdapter adapter;
    private List<DeviceModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        list = DBManager.manager().getDeviceList();
        adapter = new DeviceListAdapter(DeviceListActivity.this, R.layout.list_view_device, list);
        final ListView listView = (ListView) findViewById(R.id.list_View);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DeviceListActivity.this);
                builder.setMessage(R.string.confirm_delete);
                builder.setTitle(R.string.hint);

                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!list.isEmpty()) {
                            list = DBManager.manager().getDeviceList();
                            DeviceModel deviceModel = list.get(position);

                            DBManager.manager().deleteDevice(deviceModel);

                            list.remove(position);
                            adapter.remove(deviceModel);
                            if (deviceModel.getDeviceType() == PPScaleDefine.PPDeviceType.PPDeviceTypeCC.getType()) {
                                clearDevice(deviceModel);
                            }
                        }
//                        adapter.notifyAll();
//                        listView.setAdapter(adapter);
                    }
                });

                builder.setNegativeButton(R.string.cancle, null);
                builder.show();
                return false;
            }

        });

        adapter.setOnClickInItemLisenter(new DeviceListAdapter.OnItemClickViewInsideListener() {
            @Override
            public void onItemClickViewInside(int position, View view) {
                if (view.getId() == R.id.tvSetting) {
                    DeviceModel deviceModel = (DeviceModel) adapter.getItem(position);
                    if (deviceModel.getDeviceProtocolType() == PPScaleDefine.PPDeviceProtocolType.PPDeviceProtocolTypeTorre.getType()) {
                        Intent intent = new Intent(DeviceListActivity.this, DeviceSetActivity.class);
                        intent.putExtra("address", deviceModel.getDeviceMac());
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(DeviceListActivity.this, FunctionListActivity.class);
                        intent.putExtra(DeveloperActivity.ADDRESS, deviceModel.getDeviceMac());
                        startActivity(intent);
                    }
                }
            }
        });

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                DeviceModel deviceModel = (DeviceModel) adapter.getItem(position);
//                if (deviceModel.getDeviceType() == PPScaleDefine.PPDeviceType.PPDeviceTypeCC.getType()) {
//                    if (deviceModel.getDeviceProtocolType() == PPScaleDefine.PPDeviceProtocolType.PPDeviceProtocolTypeTorre.getType()) {
//                        Intent intent = new Intent(DeviceListActivity.this, DeviceSetActivity.class);
//                        intent.putExtra("address", deviceModel.getDeviceMac());
//                        startActivity(intent);
//                    } else {
//                        Intent intent = new Intent(DeviceListActivity.this, BleConfigWifiActivity.class);
//                        intent.putExtra("address", deviceModel.getDeviceMac());
//                        startActivity(intent);
//                    }
//                }
//
//            }
//        });

    }

    public void clearDevice(DeviceModel deviceModel) {

        NetHelper.clearDevice(this, deviceModel, SettingManager.get().getUid());
    }

    @Override
    protected void onResume() {
        super.onResume();

        list = DBManager.manager().getDeviceList();
        if (adapter != null) {
            adapter.clear();
            adapter.addAll(list);
//            adapter.notifyDataSetChanged();
        }
    }

}
