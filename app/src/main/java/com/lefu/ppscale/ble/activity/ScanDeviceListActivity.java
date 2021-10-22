package com.lefu.ppscale.ble.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.lefu.ppscale.ble.DBManager;
import com.lefu.ppscale.ble.R;
import com.lefu.ppscale.ble.adapter.DeviceListAdapter;
import com.lefu.ppscale.ble.model.DataUtil;
import com.lefu.ppscale.ble.model.DeviceModel;
import com.lefu.ppscale.ble.model.PPUtil;
import com.peng.ppscale.business.ble.BleOptions;
import com.peng.ppscale.business.ble.PPScale;
import com.peng.ppscale.business.ble.listener.PPBleStateInterface;
import com.peng.ppscale.business.ble.listener.PPDeviceInfoInterface;
import com.peng.ppscale.business.ble.listener.PPHistoryDataInterface;
import com.peng.ppscale.business.ble.listener.PPLockDataInterface;
import com.peng.ppscale.business.ble.listener.PPProcessDateInterface;
import com.peng.ppscale.business.ble.listener.ProtocalFilterImpl;
import com.peng.ppscale.business.ble.search.PPSearchDeviceInfoInterface;
import com.peng.ppscale.business.ble.send.BleSendDelegate;
import com.peng.ppscale.business.device.PPUnitType;
import com.peng.ppscale.business.state.PPBleSwitchState;
import com.peng.ppscale.business.state.PPBleWorkState;
import com.peng.ppscale.util.Logger;
import com.peng.ppscale.vo.PPBodyBaseModel;
import com.peng.ppscale.vo.PPBodyFatModel;
import com.peng.ppscale.vo.PPDeviceModel;
import com.peng.ppscale.vo.PPUserModel;

import java.util.ArrayList;
import java.util.List;

public class ScanDeviceListActivity extends Activity {

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

    private PPUnitType unitType;
    private PPUserModel userModel;
    private int searchType;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    boolean isOnResume = false;//页面可见时再重新发起扫描
    private PPScale.Builder builder1;
    private DeviceListAdapter adapter;
    ArrayList<DeviceModel> deviceModels = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        int unit = getIntent().getIntExtra(UNIT_TYPE, 0);

        unitType = PPUnitType.values()[unit];

        searchType = getIntent().getIntExtra(SEARCH_TYPE, 0);

        userModel = DataUtil.util().getUserModel();


        adapter = new DeviceListAdapter(this, R.layout.list_view_device, deviceModels);
        ListView listView = (ListView) findViewById(R.id.list_View);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (deviceModels != null && !deviceModels.isEmpty()) {
                    DeviceModel model = deviceModels.get(position);
                    List<String> models = new ArrayList<>();
                    models.add(model.getDeviceMac());
                    if (ppScale != null) {
                        ppScale.connectWithMacAddressList(models);
                    }
                }
            }
        });

        bindingDevice();
    }

    /**
     * 参数配置
     * <p>
     *
     * @param //ScaleFeatures 为了更快的搜索你的设备，你可以选择你需要使用的设备能力
     *                        具备的能力：
     *                        体重秤{@link BleOptions.ScaleFeatures#FEATURES_WEIGHT}
     *                        脂肪秤{@link BleOptions.ScaleFeatures#FEATURES_FAT}
     *                        心率秤{@link BleOptions.ScaleFeatures#FEATURES_HEART_RATE}
     *                        离线秤{@link BleOptions.ScaleFeatures#FEATURES_HISTORY}
     *                        闭目单脚秤{@link BleOptions.ScaleFeatures#FEATURES_BMDJ}
     *                        秤端计算{@link BleOptions.ScaleFeatures#FEATURES_CALCUTE_IN_SCALE}
     *                        WIFI秤{@link BleOptions.ScaleFeatures#FEATURES_CONFIG_WIFI} 请参考{@link BleConfigWifiActivity}
     *                        食物秤{@link BleOptions.ScaleFeatures#FEATURES_FOOD_SCALE}
     *                        所有人体秤{@link BleOptions.ScaleFeatures#FEATURES_NORMAL}  //不包含食物秤
     *                        所有秤{@link BleOptions.ScaleFeatures#FEATURES_ALL}
     *                        自定义{@link BleOptions.ScaleFeatures#FEATURES_CUSTORM} //选则自定义需要设置PPScale的setDeviceList()
     * @return
     * @parm unitType 单位，用于秤端切换单位
     */
    private BleOptions getBleOptions() {
        return new BleOptions.Builder()
                .setFeaturesFlag(BleOptions.ScaleFeatures.FEATURES_NORMAL)
                .setSearchTag(BleOptions.SEARCH_TAG_NORMAL)//直连  孕妇模式时请开启直连
                .setUnitType(unitType)
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
        protocalFilter.setSearchDeviceInfoInterface(new PPSearchDeviceInfoInterface() {
            @Override
            public void onSearchDevice(PPDeviceModel deviceModel) {
                if (deviceModel != null) {
                    DeviceModel model = filterDeviceData(deviceModel);
                    if (model != null) {
                        deviceModels.add(model);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            private DeviceModel filterDeviceData(PPDeviceModel deviceModel) {
                for (DeviceModel model : deviceModels) {
                    if (model.getDeviceMac().equals(deviceModel.getDeviceMac())) return null;
                }
                return new DeviceModel(deviceModel.getDeviceMac(), deviceModel.getDeviceName(), deviceModel.deviceType.getType());
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
                .setBleOptions(getBleOptions())
//                    .setDeviceList(null)
                .setUserModel(userModel)
                .setBleStateInterface(bleStateInterface)
                .build();

        //获取周围蓝牙秤设备
        ppScale.monitorSurroundDevice();
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
            } else {
                Logger.e(getString(R.string.bluetooth_status_is_abnormal));
            }
        }

        @Override
        public void monitorBluetoothSwitchState(PPBleSwitchState ppBleSwitchState) {
            if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOff) {
                Logger.e(getString(R.string.system_bluetooth_disconnect));
                Toast.makeText(ScanDeviceListActivity.this, getString(R.string.system_bluetooth_disconnect), Toast.LENGTH_SHORT).show();
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
            builder = null;
        }
    }
}


