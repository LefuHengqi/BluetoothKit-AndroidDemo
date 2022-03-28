package com.lefu.ppscale.ble.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.lefu.healthu.business.mine.binddevice.BindDeviceWiFiLockSelectConfigNetDialog;
import com.lefu.ppscale.db.dao.DBManager;

import com.lefu.ppscale.ble.R;
import com.lefu.ppscale.ble.model.DataUtil;
import com.lefu.ppscale.db.dao.DeviceModel;
import com.lefu.ppscale.wifi.activity.BleConfigWifiActivity;
import com.peng.ppscale.business.ble.listener.PPDeviceInfoInterface;
import com.peng.ppscale.util.PPUtil;
import com.peng.ppscale.business.ble.BleOptions;
import com.peng.ppscale.business.ble.PPScale;
import com.peng.ppscale.business.ble.listener.PPBleSendResultCallBack;
import com.peng.ppscale.business.ble.listener.PPBleStateInterface;
import com.peng.ppscale.business.device.PPUnitType;
import com.peng.ppscale.business.ble.listener.PPLockDataInterface;
import com.peng.ppscale.business.ble.listener.PPProcessDateInterface;
import com.peng.ppscale.business.ble.listener.ProtocalFilterImpl;
import com.peng.ppscale.business.state.PPBleSwitchState;
import com.peng.ppscale.business.state.PPBleWorkState;
import com.peng.ppscale.util.Logger;
import com.peng.ppscale.vo.PPBodyBaseModel;
import com.peng.ppscale.vo.PPBodyFatModel;
import com.peng.ppscale.vo.PPDeviceModel;
import com.peng.ppscale.vo.PPScaleDefine;
import com.peng.ppscale.vo.PPScaleSendState;
import com.peng.ppscale.vo.PPUserModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BindingDeviceActivity extends AppCompatActivity {

    TextView weightTextView;
    PPScale ppScale;

//    private BindDeviceWiFiLockSelectConfigNetDialog configWifiDialog;

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
        ppScale.startSearchBluetoothScaleWithMacAddressList();
    }

    private void initPPScale() {
        if (searchType == 0) {
            //绑定新设备
            ppScale = new PPScale.Builder(this)
                    .setProtocalFilterImpl(getProtocalFilter())
                    .setBleOptions(getBleOptions())
//                    .setDeviceList(null)
                    .setUserModel(userModel)
                    .setBleStateInterface(bleStateInterface)
                    .build();
        } else {
            //绑定已有设备
            List<DeviceModel> deviceList = DBManager.manager().getDeviceList();
            List<String> addressList = new ArrayList<>();
            for (DeviceModel deviceModel : deviceList) {
                addressList.add(deviceModel.getDeviceMac());
            }

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
                ppScale.setBuilder(builder1);
            }
        }
    }

    private void showDialog(final PPDeviceModel ppDeviceModel, final PPBodyFatModel bodyDataModel) {
        String content = getString(R.string.whether_to_save_the_) + PPUtil.getWeight(bodyDataModel.getUnit(), bodyDataModel.getPpWeightKg(), ppDeviceModel.deviceAccuracyType.getType());
        if (builder == null) {
            builder = new AlertDialog.Builder(BindingDeviceActivity.this);
        }
        builder.setTitle(R.string.is_this_your_data);
        builder.setMessage(content);
        builder.setPositiveButton(R.string.corfirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                DeviceModel deviceModel = new DeviceModel(ppDeviceModel.getDeviceMac(), ppDeviceModel.getDeviceName(), ppDeviceModel.deviceType.getType());

                DBManager.manager().insertDevice(deviceModel);

                DataUtil.util().setBodyDataModel(bodyDataModel);

                Intent intent = new Intent(BindingDeviceActivity.this, BodyDataDetailActivity.class);
                startActivity(intent);

            }
        });
        builder.setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                ppScale.reSearchDevice();
                startScanData();
            }
        });
        if (!this.isDestroyed() && !this.isFinishing()) {
            if (alertDialog == null || !alertDialog.isShowing()) {
                alertDialog = builder.show();
            }
        }
    }

    /**
     * 参数配置
     *
     * @return
     * @parm unitType 单位，用于秤端切换单位
     */
    private BleOptions getBleOptions() {
        return new BleOptions.Builder()
                .setSearchTag(BleOptions.SEARCH_TAG_NORMAL)//直连  孕妇模式时请开启直连
                .build();
    }

    /**
     * 解析数据回调
     * <p>
     * bodyFatModel 身体数据
     * deviceModel 设备信息
     */
    private ProtocalFilterImpl getProtocalFilter() {
        final ProtocalFilterImpl protocalFilter = new ProtocalFilterImpl();
        protocalFilter.setPPProcessDateInterface(new PPProcessDateInterface() {

            // 过程数据
            @Override
            public void monitorProcessData(PPBodyBaseModel bodyBaseModel, PPDeviceModel deviceModel) {
                Logger.d("bodyBaseModel scaleName " + bodyBaseModel.getScaleName());
                String weightStr = PPUtil.getWeight(bodyBaseModel.getUnit(), bodyBaseModel.getPpWeightKg(), deviceModel.deviceAccuracyType.getType());
                weightTextView.setText(weightStr);
            }
        });
        protocalFilter.setPPLockDataInterface(new PPLockDataInterface() {

            //锁定数据
            @Override
            public void monitorLockData(PPBodyFatModel bodyFatModel, PPDeviceModel deviceModel) {
                onDataLock(bodyFatModel, deviceModel);
            }
        });
        protocalFilter.setDeviceInfoInterface(new PPDeviceInfoInterface() {
            @Override
            public void softwareRevision(PPDeviceModel deviceModel) {

            }

            @Override
            public void serialNumber(PPDeviceModel deviceModel) {
                if (deviceModel.devicePowerType == PPScaleDefine.PPDevicePowerType.PPDevicePowerTypeSolar
                        && (deviceModel.deviceFuncType & PPScaleDefine.PPDeviceFuncType.PPDeviceFuncTypeHeartRate.getType()) == PPScaleDefine.PPDeviceFuncType.PPDeviceFuncTypeHeartRate.getType()
                        && deviceModel.getSerialNumber().equals("20220212")) {

                    Intent intent = new Intent(BindingDeviceActivity.this, OTAActivity.class);
                    intent.putExtra("otaAddress", deviceModel.getDeviceMac());
                    startActivity(intent);
                    finish();
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

    private void onDataLock(PPBodyFatModel bodyFatModel, PPDeviceModel deviceModel) {
        if (bodyFatModel != null) {
            if (bodyFatModel.isHeartRateEnd()) {
                Logger.d("monitorLockData  bodyFatModel weightKg = " + bodyFatModel.toString());

                if (ppScale != null) {
                    ppScale.stopSearch();
                }
                String weightStr = PPUtil.getWeight(bodyFatModel.getUnit(), bodyFatModel.getPpWeightKg(), deviceModel.deviceAccuracyType.getType());
                if (weightTextView != null) {
                    weightTextView.setText(weightStr);
//                    showDialog(deviceModel, bodyFatModel);
                }
                if (deviceModel.deviceType == PPScaleDefine.PPDeviceType.PPDeviceTypeCC) {
                    //Bluetooth WiFi scale
                    showWiFiConfigDialog(bodyFatModel, deviceModel);
                } else {
                    //Ordinary bluetooth scale
                    showDialog(deviceModel, bodyFatModel);
                }
            } else {
                Logger.d("正在测量心率");
            }
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
                //可写状态，可以发送指令，例如切换单位，获取历史数据等
                sendUnitDataScale(deviceModel);
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnectable) {
                Logger.d(getString(R.string.Connectable));
                //连接，在ppBleWorkState == PPBleWorkState.PPBleWorkStateWritable时开始发送数据
                ppScale.connectDevice(deviceModel);
            } else {
                Logger.e(getString(R.string.bluetooth_status_is_abnormal));
            }
        }

        @Override
        public void monitorBluetoothSwitchState(PPBleSwitchState ppBleSwitchState) {
            if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOff) {
                Logger.e(getString(R.string.system_bluetooth_disconnect));
                Toast.makeText(BindingDeviceActivity.this, getString(R.string.system_bluetooth_disconnect), Toast.LENGTH_SHORT).show();
            } else if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOn) {
                delayScan();
                Logger.d(getString(R.string.system_blutooth_on));
                Toast.makeText(BindingDeviceActivity.this, getString(R.string.system_blutooth_on), Toast.LENGTH_SHORT).show();
            } else {
                Logger.e(getString(R.string.system_bluetooth_abnormal));
            }
        }
    };

    /**
     * 切换单位指令
     */
    private void sendUnitDataScale(final PPDeviceModel deviceModel) {
        if (ppScale != null) {
            ppScale.sendUnitDataScale(unitType);
            ppScale.setSendResultCallBack(new PPBleSendResultCallBack() {
                @Override
                public void onResult(@NonNull PPScaleSendState sendState) {
                    if (sendState == PPScaleSendState.PP_SEND_FAIL) {
                        //发送失败
                    } else if (sendState == PPScaleSendState.PP_SEND_SUCCESS) {
                        //发送成功

                    } else if (sendState == PPScaleSendState.PP_DEVICE_ERROR) {
                        //设备错误，说明不支持该指令
                    } else if (sendState == PPScaleSendState.PP_DEVICE_NO_CONNECT) {
                        //设备未连接
                    }
                    if (deviceModel != null && deviceModel.deviceConnectType != PPScaleDefine.PPDeviceConnectType.PPDeviceConnectTypeDirect) {
                        disConnect();
                    }
                }
            });
        }
    }

    /**
     * 延迟开始搜索
     */
    public void delayScan() {
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isOnResume) {
                    startScanData();
                }
            }
        }, 1000);
    }

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

    private void showWiFiConfigDialog(final PPBodyFatModel bodyFatModel, final PPDeviceModel deviceModel) {
        if (configWifiDialog == null) {
            configWifiDialog = new BindDeviceWiFiLockSelectConfigNetDialog();
        }
        try {
            Bundle bundle = new Bundle();
            String weightStr = PPUtil.getWeight(bodyFatModel.getUnit(), bodyFatModel.getPpWeightKg(), deviceModel.deviceAccuracyType.getType());
            bundle.putString("content", weightStr);
            configWifiDialog.setArguments(bundle);

            if (!configWifiDialog.isAdded() && !configWifiDialog.isVisible() && !configWifiDialog.isRemoving()) {
                configWifiDialog.show(getSupportFragmentManager(), "BUNDLE_FRAGMENTS_KEY");
                configWifiDialog.setOnSelectListener(new BindDeviceWiFiLockSelectConfigNetDialog.OnSelectListener() {

                    @Override
                    public void onGoToConfigWiFi(@NotNull DialogFragment dialog) {
                        dialog.dismiss();
                        saveDeviceAndBodyFat(deviceModel, bodyFatModel);
//                        UnitMatchTypeHelper.setDeviceMac(deviceModel.getDeviceMac());

                        if (PPScale.isBluetoothOpened()) {
                            Intent intent = new Intent(BindingDeviceActivity.this, BleConfigWifiActivity.class);
                            intent.putExtra("address", deviceModel.getDeviceMac());
                            startActivity(intent);
                            finish();
                        } else {
                            PPScale.openBluetooth();
                        }
                    }

                    @Override
                    public void onCorfirm(@NotNull DialogFragment dialog) {
                        saveDeviceAndBodyFat(deviceModel, bodyFatModel);
                        dialog.dismiss();
                        finish();
                    }

                    @Override
                    public void onCancle(@NotNull DialogFragment dialog) {
                        startSearchDevice();
                        dialog.dismiss();
                    }

                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startSearchDevice() {
        if (ppScale != null) {
            ppScale.disConnect();
            ppScale.startSearchBluetoothScaleWithMacAddressList(false);
        }
    }

    private void saveDeviceAndBodyFat(PPDeviceModel ppDeviceModel, PPBodyFatModel bodyDataModel) {
        DeviceModel deviceModel = new DeviceModel(ppDeviceModel.getDeviceMac(), ppDeviceModel.getDeviceName(), ppDeviceModel.deviceType.getType());

        DBManager.manager().insertDevice(deviceModel);

        DataUtil.util().setBodyDataModel(bodyDataModel);
    }

    private void disConnect() {
        if (ppScale != null) {
            ppScale.stopSearch();
            ppScale.disConnect();
        }
    }


}


