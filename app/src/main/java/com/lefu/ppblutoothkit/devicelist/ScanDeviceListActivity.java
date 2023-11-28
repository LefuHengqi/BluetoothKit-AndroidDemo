package com.lefu.ppblutoothkit.devicelist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lefu.ppblutoothkit.device.PeripheralAppleActivity;
import com.lefu.ppblutoothkit.device.PeripheralBananaActivity;
import com.lefu.ppblutoothkit.device.PeripheralCoconutActivity;
import com.lefu.ppblutoothkit.device.PeripheralDutianActivity;
import com.lefu.ppblutoothkit.device.PeripheralEggActivity;
import com.lefu.ppblutoothkit.device.PeripheralFishActivity;
import com.lefu.ppblutoothkit.device.PeripheralGrapesActivity;
import com.lefu.ppblutoothkit.device.PeripheralHamburgerActivity;
import com.lefu.ppblutoothkit.device.PeripheralTorreActivity;
import com.lefu.ppblutoothkit.R;
import com.peng.ppscale.business.ble.listener.PPBleStateInterface;
import com.peng.ppscale.business.ble.listener.PPSearchDeviceInfoInterface;
import com.peng.ppscale.business.state.PPBleSwitchState;
import com.peng.ppscale.business.state.PPBleWorkState;
import com.peng.ppscale.search.PPSearchManager;
import com.peng.ppscale.util.Logger;
import com.peng.ppscale.vo.PPDeviceModel;
import com.peng.ppscale.vo.PPScaleDefine;

import java.util.ArrayList;

public class ScanDeviceListActivity extends Activity {

    PPSearchManager ppScale;

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

    boolean isOnResume = false;//页面可见时再重新发起扫描
    private DeviceListAdapter adapter;
    ArrayList<PPDeviceModel> deviceModels = new ArrayList<>();
    private TextView tv_starts;

    long lastTimes = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        tv_starts = findViewById(R.id.tv_starts);

        tv_starts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reStartScan();
            }
        });

        adapter = new DeviceListAdapter(this, R.layout.activity_scan_list_item, deviceModels);
        ListView listView = (ListView) findViewById(R.id.list_View);
        listView.setAdapter(adapter);

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                onStartDeviceSetPager(position);
//            }
//        });
        adapter.setOnClickInItemLisenter(new DeviceListAdapter.OnItemClickViewInsideListener() {
            @Override
            public void onItemClickViewInside(int position, View view) {
                onStartDeviceSetPager(position);
            }
        });
    }

    private void reStartScan() {
        if (ppScale != null) {
            ppScale.stopSearch();
        }
        tv_starts.setText("启动扫描");
        startScanDeviceList();
    }

    private void onStartDeviceSetPager(final int position) {
        PPDeviceModel deviceModel = (PPDeviceModel) adapter.getItem(position);
        if (deviceModel != null) {
            if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralTorre) {
                Intent intent = new Intent(ScanDeviceListActivity.this, PeripheralTorreActivity.class);
                PeripheralTorreActivity.Companion.setDeviceModel(deviceModel);
                startActivity(intent);
            } else if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralApple) {
                Intent intent = new Intent(ScanDeviceListActivity.this, PeripheralAppleActivity.class);
                PeripheralAppleActivity.Companion.setDeviceModel(deviceModel);
                startActivity(intent);
            } else if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralBanana) {
                Intent intent = new Intent(ScanDeviceListActivity.this, PeripheralBananaActivity.class);
                PeripheralBananaActivity.Companion.setDeviceModel(deviceModel);
                startActivity(intent);
            } else if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralCoconut) {
                Intent intent = new Intent(ScanDeviceListActivity.this, PeripheralCoconutActivity.class);
                PeripheralCoconutActivity.Companion.setDeviceModel(deviceModel);
                startActivity(intent);
            } else if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralDurian) {
                Intent intent = new Intent(ScanDeviceListActivity.this, PeripheralDutianActivity.class);
                PeripheralDutianActivity.Companion.setDeviceModel(deviceModel);
                startActivity(intent);
            } else if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralEgg) {
                Intent intent = new Intent(ScanDeviceListActivity.this, PeripheralEggActivity.class);
                PeripheralEggActivity.Companion.setDeviceModel(deviceModel);
                startActivity(intent);
            } else if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralFish) {
                Intent intent = new Intent(ScanDeviceListActivity.this, PeripheralFishActivity.class);
                PeripheralFishActivity.Companion.setDeviceModel(deviceModel);
                startActivity(intent);
            } else if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralGrapes) {
                Intent intent = new Intent(ScanDeviceListActivity.this, PeripheralGrapesActivity.class);
                PeripheralGrapesActivity.Companion.setDeviceModel(deviceModel);
                startActivity(intent);
            } else if (deviceModel.getDevicePeripheralType() == PPScaleDefine.PPDevicePeripheralType.PeripheralHamburger) {
                Intent intent = new Intent(ScanDeviceListActivity.this, PeripheralHamburgerActivity.class);
                PeripheralHamburgerActivity.Companion.setDeviceModel(deviceModel);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isOnResume = false;
        if (ppScale != null) {
            ppScale.stopSearch();
        }
    }

    /**
     * Get around bluetooth scale devices
     */
    public void startScanDeviceList() {
        if (ppScale == null) {
            ppScale = new PPSearchManager();
        }
        //You can dynamically set the scan time in ms
        ppScale.startSearchDeviceList(300000, searchDeviceInfoInterface, bleStateInterface);
    }


    /**
     * 重新扫描
     */
    public void delayScan() {
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isOnResume) {
                    startScanDeviceList();
                }
            }
        }, 1000);

    }

    @Override
    protected void onResume() {
        super.onResume();
        isOnResume = true;
        delayScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ppScale != null) {
            ppScale.stopSearch();
        }
    }

    PPSearchDeviceInfoInterface searchDeviceInfoInterface = new PPSearchDeviceInfoInterface() {

        /**
         *
         * @param ppDeviceModel 设备对象
         * @param data  广播数据
         */
        @Override
        public void onSearchDevice(PPDeviceModel ppDeviceModel, String data) {
            if (ppDeviceModel != null) {
                PPDeviceModel deviceModel = null;
                for (int i = 0; i < deviceModels.size(); i++) {
                    PPDeviceModel model = deviceModels.get(i);
                    if (model.getDeviceMac().equals(ppDeviceModel.getDeviceMac())) {
                        model.setRssi(ppDeviceModel.getRssi());
                        deviceModel = model;
                        deviceModels.set(i, model);
                    }
                }
                if (deviceModel == null) {
                    deviceModels.add(ppDeviceModel);
                }
                if (System.currentTimeMillis() - lastTimes > 500) {
                    lastTimes = System.currentTimeMillis();
                    adapter.notifyDataSetChanged();
                }
            }
        }
    };

    PPBleStateInterface bleStateInterface = new PPBleStateInterface() {

        /**
         * 蓝牙扫描和连接状态回调
         * @param ppBleWorkState 蓝牙状态标识
         * @param deviceModel 设备对象
         */
        @Override
        public void monitorBluetoothWorkState(PPBleWorkState ppBleWorkState, PPDeviceModel deviceModel) {
            if (ppBleWorkState == PPBleWorkState.PPBleStateSearchCanceled) {
                Logger.d(getString(R.string.stop_scanning));
                tv_starts.setText(getString(R.string.bluetooth_status) + getString(R.string.stop_scanning));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkSearchTimeOut) {
                Logger.d(getString(R.string.scan_timeout));
                tv_starts.setText(getString(R.string.bluetooth_status) + getString(R.string.scan_timeout));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateSearching) {
                Logger.d(getString(R.string.scanning));
                tv_starts.setText(getString(R.string.bluetooth_status) + getString(R.string.scanning));
            } else {

            }
        }

        /**
         * 系统蓝牙状态回调
         * @param ppBleSwitchState 系统蓝牙状态标识
         */
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
            }
        }
    };


}


