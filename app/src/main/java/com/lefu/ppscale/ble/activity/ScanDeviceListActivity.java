package com.lefu.ppscale.ble.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lefu.ppscale.ble.R;
import com.lefu.ppscale.ble.adapter.DeviceListAdapter;
import com.lefu.ppscale.ble.function.FunctionListActivity;
import com.lefu.ppscale.ble.model.DataUtil;
import com.lefu.ppscale.ble.torre.DeviceSetActivity;
import com.lefu.ppscale.db.dao.DeviceModel;
import com.lefu.ppscale.wifi.develop.DeveloperActivity;
import com.peng.ppscale.business.ble.PPScale;
import com.peng.ppscale.business.ble.listener.PPBleStateInterface;
import com.peng.ppscale.business.ble.listener.PPSearchDeviceInfoInterface;
import com.peng.ppscale.business.ble.listener.ProtocalFilterImpl;
import com.peng.ppscale.business.state.PPBleSwitchState;
import com.peng.ppscale.business.state.PPBleWorkState;
import com.peng.ppscale.util.Logger;
import com.peng.ppscale.vo.PPDeviceModel;
import com.peng.ppscale.vo.PPScaleDefine;
import com.peng.ppscale.vo.PPUserModel;

import java.util.ArrayList;
import java.util.List;

public class ScanDeviceListActivity extends AppCompatActivity {

    PPScale ppScale;

    /**
     * PPUnitKG = 0,
     * PPUnitLB = 1,
     * PPUnitST = 2,
     * PPUnitJin = 3,
     * PPUnitG = 4,
     * PPUnitLBOZ = 5,
     * PPUnitOZ = 6,
     * PPUnitMLWater = 7,
     * PPUnitMLMilk = 8,
     */
    public static final String UNIT_TYPE = "unitType";
    //0是绑定设备 1是搜索已有设备
    public static final String SEARCH_TYPE = "SearchType";

    private PPUserModel userModel;
    private AlertDialog alertDialog;
    boolean isOnResume = false;//页面可见时再重新发起扫描
    private DeviceListAdapter adapter;
    ArrayList<DeviceModel> deviceModels = new ArrayList<>();
    private TextView tv_starts;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        tv_starts = findViewById(R.id.tv_starts);

        tv_starts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ppScale != null) {
                    ppScale.stopSearch();
                }
                startScanDeviceList();
            }
        });

        userModel = DataUtil.util().getUserModel();

        adapter = new DeviceListAdapter(this, R.layout.list_view_device, deviceModels);
        ListView listView = (ListView) findViewById(R.id.list_View);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (deviceModels != null && !deviceModels.isEmpty()) {

                }
            }
        });

        adapter.setOnClickInItemLisenter(new DeviceListAdapter.OnItemClickViewInsideListener() {
            @Override
            public void onItemClickViewInside(int position, View view) {
                if (view.getId() == R.id.tvSetting) {
                    DeviceModel deviceModel = (DeviceModel) adapter.getItem(position);
                    List<String> models = new ArrayList<>();
                    models.add(deviceModel.getDeviceMac());
                    if (ppScale != null) {
                        ppScale.connectWithMacAddressList(models);
                    }
                }
            }
        });


        bindingDevice();
    }

    /**
     * 解析数据回调
     *
     * @return
     */
    private ProtocalFilterImpl getProtocalFilter() {
        deviceModels.clear();
        final ProtocalFilterImpl protocalFilter = new ProtocalFilterImpl();
        protocalFilter.setSearchDeviceInfoInterface(new PPSearchDeviceInfoInterface() {
            @Override
            public void onSearchDevice(PPDeviceModel ppDeviceModel) {
                if (ppDeviceModel != null) {
                    DeviceModel deviceModel = null;
                    for (int i = 0; i < deviceModels.size(); i++) {
                        DeviceModel model = deviceModels.get(i);
                        if (model.getDeviceMac().equals(ppDeviceModel.getDeviceMac())) {
                            model.setRssi(ppDeviceModel.getRssi());
                            deviceModel = model;
                            deviceModels.set(i, deviceModel);
                        }
                    }
                    if (deviceModel == null) {
                        deviceModel = new DeviceModel(ppDeviceModel.getDeviceMac(), ppDeviceModel.getDeviceName(), ppDeviceModel.deviceType.getType());
                        deviceModel.setRssi(deviceModel.getRssi());
                        deviceModels.add(deviceModel);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        return protocalFilter;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isOnResume = false;
        ppScale.stopSearch();
    }

    private void bindingDevice() {
        ppScale = new PPScale.Builder(this)
                .setProtocalFilterImpl(getProtocalFilter())
//                    .setDeviceList(null)
                .setUserModel(userModel)
                .setBleStateInterface(bleStateInterface)
                .build();

        startScanDeviceList();
    }

    /**
     * Get around bluetooth scale devices
     */
    public void startScanDeviceList() {
        if (ppScale != null) {
            //ppScale.monitorSurroundDevice();      //The default scan time is 300000ms
            ppScale.monitorSurroundDevice(300000);  //You can dynamically set the scan time in ms
        }
    }

    PPBleStateInterface bleStateInterface = new PPBleStateInterface() {

        @Override
        public void monitorBluetoothWorkState(PPBleWorkState ppBleWorkState, PPDeviceModel deviceModel) {
            if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnected) {
                Logger.d(getString(R.string.device_connected));
                tv_starts.setText(getString(R.string.bluetooth_status) + getString(R.string.device_connected));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnecting) {
                Logger.d(getString(R.string.device_connecting));
                tv_starts.setText(getString(R.string.bluetooth_status) + getString(R.string.device_connecting));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateDisconnected) {
                Logger.d(getString(R.string.device_disconnected));
                tv_starts.setText(getString(R.string.bluetooth_status) + getString(R.string.device_disconnected));
            } else if (ppBleWorkState == PPBleWorkState.PPBleStateSearchCanceled) {
                Logger.d(getString(R.string.stop_scanning));
                tv_starts.setText(getString(R.string.bluetooth_status) + getString(R.string.stop_scanning));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkSearchTimeOut) {
                Logger.d(getString(R.string.scan_timeout));
                tv_starts.setText(getString(R.string.bluetooth_status) + getString(R.string.scan_timeout));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateSearching) {
                Logger.d(getString(R.string.scanning));
                tv_starts.setText(getString(R.string.bluetooth_status) + getString(R.string.scanning));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateWritable) {
                Logger.d(getString(R.string.writable));
            } else {
                Logger.e(getString(R.string.bluetooth_status_is_abnormal));
                tv_starts.setText(getString(R.string.bluetooth_status) + getString(R.string.bluetooth_status_is_abnormal));
            }
        }

        @Override
        public void monitorBluetoothSwitchState(PPBleSwitchState ppBleSwitchState) {
            if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOff) {
                Logger.e(getString(R.string.system_bluetooth_disconnect));
                Toast.makeText(ScanDeviceListActivity.this, getString(R.string.system_bluetooth_disconnect), Toast.LENGTH_SHORT).show();
                tv_starts.setText(getString(R.string.bluetooth_status) + getString(R.string.system_bluetooth_disconnect));
            } else if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOn) {
                delayScan();
                Logger.d(getString(R.string.system_blutooth_on));
                Toast.makeText(ScanDeviceListActivity.this, getString(R.string.system_blutooth_on), Toast.LENGTH_SHORT).show();
            } else {
                Logger.e(getString(R.string.system_bluetooth_abnormal));
            }
        }
    };

    public void delayScan() {
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isOnResume) {
                    bindingDevice();
                }
            }
        }, 1000);

    }

    @Override
    protected void onResume() {
        super.onResume();
        isOnResume = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ppScale != null) {
            ppScale.stopSearch();
            ppScale.disConnect();
        }
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
            alertDialog = null;
        }
    }
}


