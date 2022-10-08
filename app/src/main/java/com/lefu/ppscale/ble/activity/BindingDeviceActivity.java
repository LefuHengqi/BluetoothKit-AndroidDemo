package com.lefu.ppscale.ble.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.lefu.healthu.business.mine.binddevice.BindDeviceWiFiLockSelectConfigNetDialog;
import com.lefu.ppscale.ble.R;
import com.lefu.ppscale.ble.model.DataUtil;
import com.lefu.ppscale.ble.torre.DeviceSetActivity;
import com.lefu.ppscale.db.dao.DBManager;
import com.lefu.ppscale.db.dao.DeviceModel;
import com.lefu.ppscale.wifi.activity.BleConfigWifiActivity;
import com.peng.ppscale.business.ble.BleOptions;
import com.peng.ppscale.business.ble.PPScale;
import com.peng.ppscale.business.ble.listener.PPBindDeviceInterface;
import com.peng.ppscale.business.ble.listener.PPBleSendResultCallBack;
import com.peng.ppscale.business.ble.listener.PPBleStateInterface;
import com.peng.ppscale.business.ble.listener.PPDeviceInfoInterface;
import com.peng.ppscale.business.ble.listener.PPLockDataInterface;
import com.peng.ppscale.business.ble.listener.PPProcessDateInterface;
import com.peng.ppscale.business.ble.listener.ProtocalFilterImpl;
import com.peng.ppscale.business.device.PPUnitType;
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

public class BindingDeviceActivity extends AppCompatActivity {

    TextView weightTextView;
    PPScale ppScale;

    public static final String SEARCH_TYPE = "SearchType";
    public static final String CONNECT_ADDRESS = "connectAddress";//Specify the connected device Address

    private PPUnitType unitType;
    private PPUserModel userModel;
    /**
     * searchType
     * 0 is to bind the device
     * 1 is to search for an existing device
     * 2 to connect to the specified device
     */
    private int searchType;
    private String connectAddress;////Specify the connected device Address
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
        connectAddress = getIntent().getStringExtra(CONNECT_ADDRESS);

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
        } else if (searchType == 2) {
            if (connectAddress == null) return;
            List<String> addressList = new ArrayList<>();
            addressList.add(connectAddress);
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
        //启动扫描
        startScanData();
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

                saveDevice(ppDeviceModel);

                DataUtil.util().setBodyDataModel(bodyDataModel);

                Intent intent = new Intent(BindingDeviceActivity.this, BodyDataDetailActivity.class);
                startActivity(intent);
                finish();
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

    private void saveDevice(PPDeviceModel ppDeviceModel) {
        DeviceModel deviceModel = new DeviceModel(ppDeviceModel.getDeviceMac(), ppDeviceModel.getDeviceName(), ppDeviceModel.deviceType.getType());

        deviceModel.setDeviceProtocolType(ppDeviceModel.deviceProtocolType.getType());
        deviceModel.setDevicePower(ppDeviceModel.getBatteryPower());
        deviceModel.setFirmwareVersion(ppDeviceModel.getFirmwareVersion());
        deviceModel.setSerialNumber(ppDeviceModel.getSerialNumber());
        deviceModel.setHardwareVersion(ppDeviceModel.getHardwareVersion());
        deviceModel.setAccuracyType(ppDeviceModel.deviceAccuracyType.getType());
        deviceModel.setDeviceFuncType(ppDeviceModel.deviceFuncType);
        deviceModel.setDeviceUnitType(ppDeviceModel.deviceUnitType);
        deviceModel.setDeviceCalcuteType(ppDeviceModel.deviceCalcuteType.getType());
        deviceModel.setDevicePowerType(ppDeviceModel.devicePowerType.getType());

        DBManager.manager().insertDevice(deviceModel);
    }

    /**
     * Connection configuration
     *
     * @return
     */
    private BleOptions getBleOptions() {
        return new BleOptions.Builder()
                .setSearchTag(BleOptions.SEARCH_TAG_NORMAL)//broadcast
//                .setSearchTag(BleOptions.SEARCH_TAG_DIRECT_CONNECT)//direct connection
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
        if (searchType == 0) {
            protocalFilter.setBindDeviceInterface(new PPBindDeviceInterface() {
                @Override
                public void onBindDevice(PPDeviceModel deviceModel) {
                    if (deviceModel != null) {
                        DeviceModel device = DBManager.manager().getDevice(deviceModel.getDeviceMac());
                        if (device == null) {
                            saveDevice(deviceModel);
                            finish();
                            startActivity(new Intent(BindingDeviceActivity.this, DeviceListActivity.class));
                        }
                    }
                }
            });
        } else {
            protocalFilter.setPPProcessDateInterface(new PPProcessDateInterface() {

                // 过程数据
                @Override
                public void monitorProcessData(PPBodyBaseModel bodyBaseModel, PPDeviceModel deviceModel) {
                    Logger.d("bodyBaseModel scaleName " + bodyBaseModel);
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

                @Override
                public void monitorOverWeight() {
                    Logger.e("over weight ");
                }
            });
        }

        protocalFilter.setDeviceInfoInterface(new PPDeviceInfoInterface() {

            @Override
            public void serialNumber(PPDeviceModel deviceModel) {
                if (deviceModel.devicePowerType == PPScaleDefine.PPDevicePowerType.PPDevicePowerTypeSolar
                        && (deviceModel.deviceFuncType & PPScaleDefine.PPDeviceFuncType.PPDeviceFuncTypeHeartRate.getType()) == PPScaleDefine.PPDeviceFuncType.PPDeviceFuncTypeHeartRate.getType()
                        && deviceModel.getSerialNumber().equals("20220212")) {
                    disConnect();
                    Intent intent = new Intent(BindingDeviceActivity.this, OTAActivity.class);
                    intent.putExtra("otaAddress", deviceModel.getDeviceMac());
                    startActivityForResult(intent, 0x0003);
                } else {
                    Logger.d("getSerialNumber  " + deviceModel.getSerialNumber());
                }
            }

            @Override
            public void readDeviceInfoComplete(PPDeviceModel deviceModel) {
                Logger.d("DeviceInfo :  " + deviceModel.toString());
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
                if (searchType != 0 && deviceModel.isDeviceConnectAbled()) {
                    ppScale.connectDevice(deviceModel);
                } else {
                    //绑定设备时不发起连接，非可连接设备，不发起连接
                }
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
            if (deviceModel.getDeviceCalcuteType() == PPScaleDefine.PPDeviceCalcuteType.PPDeviceCalcuteTypeInScale) {
                //秤端计算，需要发送身体详情数据给秤，发送完成后不断开（切换单位）
                ppScale.sendData2ElectronicScale(unitType);
            } else {
                //切换单位
                ppScale.sendUnitDataScale(unitType);
                ppScale.setSendResultCallBack(new PPBleSendResultCallBack() {
                    @Override
                    public void onResult(@NonNull PPScaleSendState sendState) {
                        if (sendState == PPScaleSendState.PP_SEND_FAIL) {
                            //Failed to send
                        } else if (sendState == PPScaleSendState.PP_SEND_SUCCESS) {
                            //sentSuccessfully

                        } else if (sendState == PPScaleSendState.PP_DEVICE_ERROR) {
                            //Device error, indicating that the command is not supported
                        } else if (sendState == PPScaleSendState.PP_DEVICE_NO_CONNECT) {
                            //deviceNotConnected
                        }
                        if (deviceModel != null && deviceModel.deviceConnectType != PPScaleDefine.PPDeviceConnectType.PPDeviceConnectTypeDirect) {
                            disConnect();
                        }
                    }
                });
            }
        }
    }

    /**
     * 延迟开始搜索
     */
    public void delayScan() {
        new Handler().postDelayed(new Runnable() {
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
                            if (deviceModel.deviceProtocolType == PPScaleDefine.PPDeviceProtocolType.PPDeviceProtocolTypeTorre) {
                                //Torre Bluetooth Wifi Scale
                                Intent intent = new Intent(BindingDeviceActivity.this, DeviceSetActivity.class);
                                intent.putExtra("address", deviceModel.getDeviceMac());
                                startActivity(intent);
                            } else {
                                //Ordinary Bluetooth WiFi Scale
                                Intent intent = new Intent(BindingDeviceActivity.this, BleConfigWifiActivity.class);
                                intent.putExtra("address", deviceModel.getDeviceMac());
                                startActivity(intent);
                            }
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
        saveDevice(ppDeviceModel);

        DataUtil.util().setBodyDataModel(bodyDataModel);
    }

    private void disConnect() {
        if (ppScale != null) {
            ppScale.stopSearch();
            ppScale.disConnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x0003) {
            delayScan();
        }
    }
}


