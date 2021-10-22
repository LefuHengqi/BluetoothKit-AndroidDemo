package com.lefu.ppscale.ble.activity;

import android.app.Activity;
import android.app.AlertDialog;
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
import com.peng.ppscale.business.ble.listener.PPHistoryDataInterface;
import com.peng.ppscale.business.ble.listener.ProtocalFilterImpl;
import com.peng.ppscale.business.ble.send.BleSendDelegate;
import com.peng.ppscale.business.device.PPUnitType;
import com.peng.ppscale.business.state.PPBleSwitchState;
import com.peng.ppscale.business.state.PPBleWorkState;
import com.peng.ppscale.util.Logger;
import com.peng.ppscale.vo.PPBodyFatModel;
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

    private PPUnitType unitType;
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

        int unit = getIntent().getIntExtra(UNIT_TYPE, 0);

        unitType = PPUnitType.values()[unit];

        userModel = DataUtil.util().getUserModel();

        adapter = new DeviceListAdapter(this, R.layout.list_view_device, deviceModels);
        ListView listView = (ListView) findViewById(R.id.list_View);
        listView.setAdapter(adapter);

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

        protocalFilter.setPPHistoryDataInterface(new PPHistoryDataInterface() {
            /**
             * 历史数据
             *
             * @param bodyFatModel
             * @param isEnd
             * @param dateTime
             */
            @Override
            public void monitorHistoryData(PPBodyFatModel bodyFatModel, boolean isEnd, String dateTime) {
                if (bodyFatModel != null) {
                    Logger.d("ppScale_ isEnd = " + isEnd + " dateTime = " + dateTime + " bodyBaseModel weight kg = " + bodyFatModel.getPpWeightKg());
                } else {
                    Logger.d("ppScale_ isEnd = " + isEnd);
                }
                if (!isEnd) {
                    if (bodyFatModel != null) {

                        String weightStr = PPUtil.getWeight(bodyFatModel.getUnit(), bodyFatModel.getPpWeightKg(), bodyFatModel.getScaleName());

                        DeviceModel bodyModel = new DeviceModel(bodyFatModel.getImpedance() + "", weightStr, -1);

                        deviceModels.add(bodyModel);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    if (deviceModels == null || deviceModels.isEmpty()) {
                        tv_starts.setText("没有离线数据");
                    } else {
                        tv_starts.setText("读取历史数据结束");
                    }
                    //历史数据结束，删除历史数据，并断开连接
                    if (ppScale != null) {
                        //删除历史
//                        ppScale.deleteHistoryData();
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                if (ppScale != null) {
                                    ppScale.disConnect();
                                }
                            }
                        }, BleSendDelegate.POST_DELAY_MILLS);
                    }
                }
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
    private void bindingDevice() {

        List<DeviceModel> deviceList = DBManager.manager().getDeviceList();

        if (deviceList != null && !deviceList.isEmpty()) {
            List<String> addressList = new ArrayList<>();
            for (DeviceModel deviceModel : deviceList) {
                addressList.add(deviceModel.getDeviceMac());
            }
            ppScale = new PPScale.Builder(this)
                    .setProtocalFilterImpl(getProtocalFilter())
                    .setBleOptions(getBleOptions())
                    .setDeviceList(addressList)//注意：这里是必传项
                    .setUserModel(userModel)
                    .setBleStateInterface(bleStateInterface)
                    .build();
            //获取历史数据
            tv_starts.setText("开始读取离线数据");
            ppScale.fetchHistoryData();
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


