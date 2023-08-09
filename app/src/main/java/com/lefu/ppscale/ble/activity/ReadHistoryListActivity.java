package com.lefu.ppscale.ble.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.lefu.ppscale.db.dao.DBManager;
import com.lefu.ppscale.ble.R;
import com.lefu.ppscale.ble.adapter.DeviceListAdapter;
import com.lefu.ppscale.ble.util.DataUtil;
import com.lefu.ppscale.db.dao.DeviceModel;
import com.peng.ppscale.business.ble.listener.PPBleSendResultCallBack;
import com.peng.ppscale.util.PPUtil;
import com.peng.ppscale.business.ble.BleOptions;
import com.peng.ppscale.business.ble.PPScale;
import com.peng.ppscale.business.ble.listener.PPBleStateInterface;
import com.peng.ppscale.business.ble.listener.PPHistoryDataInterface;
import com.peng.ppscale.business.ble.listener.ProtocalFilterImpl;
import com.peng.ppscale.business.state.PPBleSwitchState;
import com.peng.ppscale.business.state.PPBleWorkState;
import com.peng.ppscale.util.Logger;
import com.peng.ppscale.vo.PPBodyBaseModel;
import com.peng.ppscale.vo.PPDeviceModel;
import com.peng.ppscale.vo.PPUserModel;

import java.util.ArrayList;
import java.util.List;


/**
 * 读取历史数据，必须先绑定设备
 */
public class ReadHistoryListActivity extends Activity {

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
        setContentView(R.layout.activity_hitory);

        tv_starts = findViewById(R.id.tv_starts);


        userModel = DataUtil.util().getUserModel();

        adapter = new DeviceListAdapter(this, R.layout.list_view_device, deviceModels);
        ListView listView = (ListView) findViewById(R.id.list_View);
        listView.setAdapter(adapter);

        initPPScaleAndConnectDevice();
    }

    /**
     * 获取历史数据
     */
    private void fetchHistoryData(PPBleSendResultCallBack sendResultCallBack) {
        if (ppScale != null) {
            ppScale.fetchHistoryData(sendResultCallBack);
        }
    }

    private void deleteHistoryData() {
        //删除历史
        if (ppScale != null) {
            ppScale.deleteHistoryData();
        }
    }

    private BleOptions getBleOptions() {
        return new BleOptions.Builder()
                .setSearchTag(BleOptions.SEARCH_TAG_NORMAL)
                .build();
    }

    /**
     * 解析数据回调
     *
     * @return
     */
    private ProtocalFilterImpl getProtocalFilter() {
        deviceModels.clear();
        final ProtocalFilterImpl protocalFilter = new ProtocalFilterImpl();

        protocalFilter.setPPHistoryDataInterface(new PPHistoryDataInterface() {

            @Override
            public void monitorHistoryData(PPBodyBaseModel bodyBaseModel, String dateTime) {
                if (bodyBaseModel != null) {
                    Logger.d("ppScale_ " + " dateTime = " + dateTime + " bodyBaseModel weight kg = " + bodyBaseModel.getPpWeightKg());
                    String weightStr = PPUtil.getWeight(bodyBaseModel.unit, bodyBaseModel.getPpWeightKg(), bodyBaseModel.deviceModel.deviceAccuracyType.getType());
                    DeviceModel bodyModel = new DeviceModel(bodyBaseModel.impedance + "", weightStr, -1);
                    deviceModels.add(bodyModel);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void monitorHistoryEnd(PPDeviceModel deviceModel) {
                if (deviceModels == null || deviceModels.isEmpty()) {
                    tv_starts.setText("No offline data");//没有离线数据
                } else {
                    tv_starts.setText("End of reading historical data");//读取历史数据结束
                }
                //End of historical data, delete historical data 历史数据结束，删除历史数据
//                        deleteHistoryData();
            }

        });
        return protocalFilter;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isOnResume = false;
        if (ppScale != null) {
            ppScale.stopSearch();
        }
    }

    //直接读取历史数据，需要传入要读取的秤
    private void initPPScaleAndConnectDevice() {
        List<DeviceModel> deviceList = DBManager.manager().getDeviceList();
        if (deviceList != null && !deviceList.isEmpty()) {
            ppScale = new PPScale.Builder(this)
                    .setProtocalFilterImpl(getProtocalFilter())
                    .setBleOptions(getBleOptions())
                    .setUserModel(userModel)
                    .setBleStateInterface(bleStateInterface)
                    .build();
            tv_starts.setText("开始读取离线数据");
            //连接设备
            ppScale.connectAddress(deviceList.get(0).getDeviceMac());
        } else {
            tv_starts.setText("请先绑定设备");
        }
    }

    PPBleStateInterface bleStateInterface = new PPBleStateInterface() {
        @Override
        public void monitorBluetoothWorkState(PPBleWorkState ppBleWorkState, PPDeviceModel deviceModel) {
            if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnected) {
                Logger.d(getString(R.string.device_connected));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnecting) {
                Logger.d(getString(R.string.device_connecting));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateDisconnected) {
                Logger.d(getString(R.string.device_disconnected));
            } else if (ppBleWorkState == PPBleWorkState.PPBleStateSearchCanceled) {
                Logger.d(getString(R.string.stop_scanning));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkSearchTimeOut) {
                Logger.d(getString(R.string.scan_timeout));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateSearching) {
                Logger.d(getString(R.string.scanning));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateWritable) {
                Logger.d(getString(R.string.writable));
                fetchHistoryData(null);
            } else {
                Logger.e(getString(R.string.bluetooth_status_is_abnormal));
            }
        }

        @Override
        public void monitorBluetoothSwitchState(PPBleSwitchState ppBleSwitchState) {
            if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOff) {
                Logger.e(getString(R.string.system_bluetooth_disconnect));
                Toast.makeText(ReadHistoryListActivity.this, getString(R.string.system_bluetooth_disconnect), Toast.LENGTH_SHORT).show();
            } else if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOn) {
                delayScan();
                Logger.d(getString(R.string.system_blutooth_on));
                Toast.makeText(ReadHistoryListActivity.this, getString(R.string.system_blutooth_on), Toast.LENGTH_SHORT).show();
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
                    initPPScaleAndConnectDevice();
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


