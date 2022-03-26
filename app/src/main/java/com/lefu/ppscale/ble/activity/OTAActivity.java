package com.lefu.ppscale.ble.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.lefu.healthu.business.mine.binddevice.BindDeviceWiFiLockSelectConfigNetDialog;
import com.lefu.ppscale.ble.R;
import com.lefu.ppscale.ble.model.DataUtil;
import com.lefu.ppscale.db.dao.DBManager;
import com.lefu.ppscale.db.dao.DeviceModel;
import com.lefu.ppscale.wifi.activity.BleConfigWifiActivity;
import com.peng.ppscale.business.ble.BleOptions;
import com.peng.ppscale.business.ble.PPScale;
import com.peng.ppscale.business.ble.listener.PPBleSendResultCallBack;
import com.peng.ppscale.business.ble.listener.PPBleStateInterface;
import com.peng.ppscale.business.ble.listener.PPDeviceInfoInterface;
import com.peng.ppscale.business.ble.listener.PPLockDataInterface;
import com.peng.ppscale.business.ble.listener.PPProcessDateInterface;
import com.peng.ppscale.business.ble.listener.ProtocalFilterImpl;
import com.peng.ppscale.business.device.PPUnitType;
import com.peng.ppscale.business.ota.OTAManager;
import com.peng.ppscale.business.ota.OnOTAStateListener;
import com.peng.ppscale.business.state.PPBleSwitchState;
import com.peng.ppscale.business.state.PPBleWorkState;
import com.peng.ppscale.util.Logger;
import com.peng.ppscale.util.PPUtil;
import com.peng.ppscale.vo.PPBodyBaseModel;
import com.peng.ppscale.vo.PPBodyFatModel;
import com.peng.ppscale.vo.PPDeviceModel;
import com.peng.ppscale.vo.PPScaleDefine;
import com.peng.ppscale.vo.PPScaleSendState;
import com.peng.ppscale.vo.PPUserModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OTAActivity extends AppCompatActivity {

    TextView weightTextView;
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
    private BindDeviceWiFiLockSelectConfigNetDialog configWifiDialog;
    private OTAManager otaManager;
    private boolean isStartOta;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bingdevice);
        weightTextView = findViewById(R.id.weightTextView);

        unitType = DataUtil.util().getUnit();

        searchType = getIntent().getIntExtra(SEARCH_TYPE, 0);

        userModel = DataUtil.util().getUserModel();

        //初始化PPSCale
        initPPScale();
    }


    private void startScanData() {

        otaManager = OTAManager.getInstance();

        otaManager.addOnOtaStateListener(new OnOTAStateListener() {
            @Override
            public void onUpdateFail() {
                Logger.e("ota is fail");
                weightTextView.setText("ota is fail");
            }

            @Override
            public void onStartUpdate() {
                isUpgradeSuccess = false;
                Logger.e("ota is start");
                weightTextView.setText("ota is start");
            }

            @Override
            public void onUpdateProgress(int progress) {
                weightTextView.setText(String.format("%d%%", progress));
            }

            @Override
            public void onUpdateSucess() {
                isUpgradeSuccess = true;
                weightTextView.setText("update Success");
            }

            @Override
            public void onReadyToUpdate() {
                weightTextView.setText("ready update");
                otaManager.startUpdate();
            }

            @Override
            public boolean isOTA() {
                return true;
            }
        });

        ppScale.startSearchBluetoothScaleWithMacAddressList();
    }

    private void initPPScale() {

        PPScale.setDebug(true);
        //绑定新设备
        ppScale = new PPScale.Builder(this)
                .setProtocalFilterImpl(getProtocalFilter())
                .setBleOptions(getBleOptions())
//                    .setDeviceList(null)
                .setUserModel(userModel)
                .setBleStateInterface(bleStateInterface)
                .build();
    }

    public void reStartConnect(PPDeviceModel deviceModel) {
        List<String> addressList = new ArrayList<>();
        addressList.add(deviceModel.getDeviceMac());
        if (builder1 == null) {
            builder1 = new PPScale.Builder(this);
        }
        if (ppScale == null) {
            ppScale = builder1.setProtocalFilterImpl(getProtocalFilter())
                    .setBleOptions(getBleOptions())
                    .setDeviceList(addressList)
                    .setUserModel(userModel)
                    .setBleStateInterface(bleStateInterface)
                    .build();
        }
        if (builder1 != null) {
            builder1.setUserModel(userModel);
            builder1.setDeviceList(addressList);
            ppScale.setBuilder(builder1);
        }
        ppScale.startSearchBluetoothScaleWithMacAddressList();
    }

    /**
     * 参数配置
     *
     * @return
     * @parm unitType 单位，用于秤端切换单位
     */
    private BleOptions getBleOptions() {
        return new BleOptions.Builder()
                .setSearchTag(BleOptions.SEARCH_TAG_DIRECT_CONNECT)//直连
                .build();
    }

    /**
     * 解析数据回调
     * <p>
     * bodyFatModel 身体数据
     * deviceModel 设备信息
     */
    private ProtocalFilterImpl getProtocalFilter() {
        isStartOta = false;
        final ProtocalFilterImpl protocalFilter = new ProtocalFilterImpl();
        protocalFilter.setPPProcessDateInterface(new PPProcessDateInterface() {

            // 过程数据
            @Override
            public void monitorProcessData(PPBodyBaseModel bodyBaseModel, PPDeviceModel deviceModel) {
                Logger.d("bodyBaseModel scaleName " + bodyBaseModel.getScaleName());
//                String weightStr = PPUtil.getWeight(bodyBaseModel.getUnit(), bodyBaseModel.getPpWeightKg(), deviceModel.deviceAccuracyType.getType());
//                weightTextView.setText(weightStr);
            }
        });
        protocalFilter.setPPLockDataInterface(new PPLockDataInterface() {

            //锁定数据
            @Override
            public void monitorLockData(PPBodyFatModel bodyFatModel, PPDeviceModel deviceModel) {

            }
        });
        protocalFilter.setDeviceInfoInterface(new PPDeviceInfoInterface() {

            @Override
            public void softwareRevision(PPDeviceModel deviceModel) {

            }

            @Override
            public void serialNumber(PPDeviceModel deviceModel) {
                if (!isStartOta && deviceModel.devicePowerType == PPScaleDefine.PPDevicePowerType.PPDevicePowerTypeSolar
                        && (deviceModel.deviceFuncType & PPScaleDefine.PPDeviceFuncType.PPDeviceFuncTypeHeartRate.getType()) == PPScaleDefine.PPDeviceFuncType.PPDeviceFuncTypeHeartRate.getType()
                        && deviceModel.getSerialNumber().equals("20220212")) {
                    isStartOta = true;
                    isUpgradeSuccess = false;
                    otaManager.startUpgradeRequest("cf818_ota_20_v3.1.bin", OTAActivity.this);
                } else {
                    Logger.d("getSerialNumber  " + deviceModel.getSerialNumber());
                }
            }

            @Override
            public void batteryPower(PPDeviceModel deviceModel) {

            }
        });
        return protocalFilter;
    }

    boolean isUpgradeSuccess = false;

    PPBleStateInterface bleStateInterface = new PPBleStateInterface() {
        @Override
        public void monitorBluetoothWorkState(PPBleWorkState ppBleWorkState, PPDeviceModel deviceModel) {
            if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnected) {
                Logger.d(getString(R.string.device_connected));
                if (isUpgradeSuccess) {
                    otaManager.exitUpgrade();
                    isUpgradeSuccess = false;
                }
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnecting) {
                Logger.d(getString(R.string.device_connecting));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateDisconnected) {
                Logger.d(getString(R.string.device_disconnected));
                if (isUpgradeSuccess) {
                    reStartConnect(deviceModel);
                }
            } else if (ppBleWorkState == PPBleWorkState.PPBleStateSearchCanceled) {
                Logger.d(getString(R.string.stop_scanning));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkSearchTimeOut) {
                Logger.d(getString(R.string.scan_timeout));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateSearching) {
                Logger.d(getString(R.string.scanning));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateWritable) {
                Logger.d(getString(R.string.writable));
                //可写状态，可以发送指令，例如切换单位，获取历史数据等
//                sendUnitDataScale();
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnectable) {
                Logger.d(getString(R.string.Connectable));
                //连接，在ppBleWorkState == PPBleWorkState.PPBleWorkStateWritable时开始发送数据
//                ppScale.connectDevice(deviceModel);
            } else {
                Logger.e(getString(R.string.bluetooth_status_is_abnormal));
            }
        }

        @Override
        public void monitorBluetoothSwitchState(PPBleSwitchState ppBleSwitchState) {
            if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOff) {
                Logger.e(getString(R.string.system_bluetooth_disconnect));
                Toast.makeText(OTAActivity.this, getString(R.string.system_bluetooth_disconnect), Toast.LENGTH_SHORT).show();
            } else if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOn) {
                Logger.d(getString(R.string.system_blutooth_on));
                Toast.makeText(OTAActivity.this, getString(R.string.system_blutooth_on), Toast.LENGTH_SHORT).show();
            } else {
                Logger.e(getString(R.string.system_bluetooth_abnormal));
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        isOnResume = true;
        //启动扫描
        startScanData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ppScale.stopSearch();
        isOnResume = false;
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



