package com.lefu.ppscale.ble.torre;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.lefu.ppscale.ble.R;
import com.lefu.ppscale.ble.model.DataUtil;
import com.peng.ppscale.business.ble.BleOptions;
import com.peng.ppscale.business.ble.PPScale;
import com.peng.ppscale.business.ble.configWifi.PPConfigStateMenu;
import com.peng.ppscale.business.ble.listener.PPBleStateInterface;
import com.peng.ppscale.business.ble.listener.PPDeviceInfoInterface;
import com.peng.ppscale.business.ble.listener.PPDeviceLogInterface;
import com.peng.ppscale.business.ble.listener.PPDeviceSetInfoInterface;
import com.peng.ppscale.business.ble.listener.PPHistoryDataInterface;
import com.peng.ppscale.business.ble.listener.PPLockDataInterface;
import com.peng.ppscale.business.ble.listener.PPProcessDateInterface;
import com.peng.ppscale.business.ble.listener.PPTorreDeviceModeChangeInterface;
import com.peng.ppscale.business.ble.listener.PPUserInfoInterface;
import com.peng.ppscale.business.ble.listener.ProtocalFilterImpl;
import com.peng.ppscale.business.device.PPUnitType;
import com.peng.ppscale.business.ota.OnOTAStateListener;
import com.peng.ppscale.business.state.PPBleSwitchState;
import com.peng.ppscale.business.state.PPBleWorkState;
import com.peng.ppscale.business.torre.listener.OnDFUStateListener;
import com.peng.ppscale.business.torre.listener.PPClearDataInterface;
import com.peng.ppscale.business.torre.listener.PPTorreConfigWifiInterface;
import com.peng.ppscale.util.Logger;
import com.peng.ppscale.util.PPUtil;
import com.peng.ppscale.vo.PPBodyBaseModel;
import com.peng.ppscale.vo.PPBodyFatModel;
import com.peng.ppscale.vo.PPDeviceModel;
import com.peng.ppscale.vo.PPUserModel;
import com.peng.ppscale.vo.PPWifiModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DeviceSetActivity extends Activity implements View.OnClickListener {

    public static final String TouristUID = "0000000000000000000000000000000000000000000000000000000000000000";

    private static final int REQUEST_CODE = 1024;

    private PPUnitType unitType;
    private PPUserModel userModel;
    private PPScale ppScale;
    private PPScale.Builder builder1;
    private boolean isSendData;
    private String address;
    private TextView device_set_deviceinfo, wifi_name;
    private TextView device_set_connect_state, weightTextView, functinonTypeTvState;
    private Button device_set_light, device_set_sync_log, device_set_sync_time, device_set_reset,
            device_set_synchistory, device_set_startOTA, device_set_startLocalOTA, device_set_sync_userinfo, device_set_wifi_list, device_set_startConfigWifi,
            device_set_exitConfigWifi, device_set_delete_userinfo, device_set_confirm_current_userinfo, device_set_get_userinfo_list,
            device_set_startDFU, device_set_getFilePath, startMeasureBtn, pregnancyMode, closePregnancyMode, getWifiSSID, device_set_clearUser;
    private ToggleButton whetherFullyDFUToggleBtn;//控制是否全量升级，true开启全量
    private String dfuFilePath;
    private boolean isCopyEnd;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_set_layout);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        unitType = DataUtil.util().getUnit();

        userModel = DataUtil.util().getUserModel();
        userModel.userID = "1006451068@qq.com";
//        userModel.userID = TouristUID;
        userModel.memberID = "4C2D82A7-AA9B-46F2-99BB-8B82A1F63626";
//        userModel.memberID = TouristUID;
        userModel.userName = "AB";

        address = getIntent().getStringExtra("address");

        weightTextView = findViewById(R.id.weightTextView);
        functinonTypeTvState = findViewById(R.id.functinonTypeTvState);
        device_set_getFilePath = findViewById(R.id.device_set_getFilePath);
        device_set_deviceinfo = findViewById(R.id.device_set_deviceinfo);
        device_set_connect_state = findViewById(R.id.device_set_connect_state);
        device_set_light = findViewById(R.id.device_set_light);
        device_set_sync_log = findViewById(R.id.device_set_sync_log);
        device_set_sync_time = findViewById(R.id.device_set_sync_time);
        device_set_reset = findViewById(R.id.device_set_reset);
        device_set_synchistory = findViewById(R.id.device_set_synchistory);
        device_set_startOTA = findViewById(R.id.device_set_startOTA);
        device_set_sync_userinfo = findViewById(R.id.device_set_sync_userinfo);
        device_set_wifi_list = findViewById(R.id.device_set_wifi_list);
        device_set_startConfigWifi = findViewById(R.id.device_set_startConfigWifi);
        device_set_exitConfigWifi = findViewById(R.id.device_set_exitConfigWifi);
        device_set_delete_userinfo = findViewById(R.id.device_set_delete_userinfo);
        device_set_confirm_current_userinfo = findViewById(R.id.device_set_confirm_current_userinfo);
        device_set_get_userinfo_list = findViewById(R.id.device_set_get_userinfo_list);
        device_set_startDFU = findViewById(R.id.device_set_startDFU);
        wifi_name = findViewById(R.id.wifi_name);
        device_set_startLocalOTA = findViewById(R.id.device_set_startLocalOTA);
        startMeasureBtn = findViewById(R.id.startMeasureBtn);
        pregnancyMode = findViewById(R.id.pregnancyMode);
        closePregnancyMode = findViewById(R.id.closePregnancyMode);
        getWifiSSID = findViewById(R.id.getWifiSSID);
        device_set_clearUser = findViewById(R.id.device_set_clearUser);
        whetherFullyDFUToggleBtn = findViewById(R.id.whetherFullyDFUToggleBtn);

        device_set_light.setOnClickListener(this);
        device_set_sync_log.setOnClickListener(this);
        device_set_sync_time.setOnClickListener(this);
        device_set_reset.setOnClickListener(this);
        device_set_synchistory.setOnClickListener(this);
        device_set_startOTA.setOnClickListener(this);
        device_set_startLocalOTA.setOnClickListener(this);
        device_set_sync_userinfo.setOnClickListener(this);
        device_set_wifi_list.setOnClickListener(this);
        device_set_startConfigWifi.setOnClickListener(this);
        device_set_exitConfigWifi.setOnClickListener(this);
        device_set_delete_userinfo.setOnClickListener(this);
        device_set_confirm_current_userinfo.setOnClickListener(this);
        device_set_get_userinfo_list.setOnClickListener(this);
        device_set_startDFU.setOnClickListener(this);
        device_set_getFilePath.setOnClickListener(this);
        startMeasureBtn.setOnClickListener(this);
        pregnancyMode.setOnClickListener(this);
        closePregnancyMode.setOnClickListener(this);
        getWifiSSID.setOnClickListener(this);
        device_set_clearUser.setOnClickListener(this);

        dfuFilePath = this.getFilesDir().getAbsolutePath() + "/dfu/";
//        moveDFUFile(dfuFilePath);
        //初始化PPSCale
        initPPScale();

        whetherFullyDFUToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
    }

    public int getTextWidth(Paint paint, String content) {
        int width = 0;
        if (content != null && content.length() > 0) {
            int length = content.length();
            float[] widths = new float[length];
            paint.getTextWidths(content, widths);
            for (int i = 0; i < length; i++) {
                width += (int) Math.ceil(widths[i]);
            }
        }
        return width;
    }

    private void initPPScale() {
        List<String> addressList = new ArrayList<>();
        addressList.add(address);
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
        initLitener();
        startConnectDevice(address);
    }

    private void initLitener() {
        ppScale.getTorreDeviceManager().setTorreDeviceLogInterface(new PPDeviceLogInterface() {
            @Override
            public void syncLogStart() {

            }

            @Override
            public void syncLoging(int progress) {

            }

            @Override
            public void syncLogEnd(String logFilePath) {
                Logger.w("sync log path :" + logFilePath);
                Toast.makeText(DeviceSetActivity.this, "日志同步完成", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void startConnectDevice(String address) {
        ppScale.connectAddress(address);
    }

    PPBleStateInterface bleStateInterface = new PPBleStateInterface() {
        @Override
        public void monitorBluetoothWorkState(PPBleWorkState ppBleWorkState, PPDeviceModel deviceModel) {

            if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnected) {
                Logger.d(getString(R.string.device_connected));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnecting) {
                Logger.d(getString(R.string.device_connecting));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateDisconnected) {
                device_set_connect_state.setText("已断开");
                isSendData = false;
                Logger.d(getString(R.string.device_disconnected));
            } else if (ppBleWorkState == PPBleWorkState.PPBleStateSearchCanceled) {
                Logger.d(getString(R.string.stop_scanning));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkSearchTimeOut) {
                isSendData = false;
                Logger.d(getString(R.string.scan_timeout));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateSearching) {
                Logger.d(getString(R.string.scanning));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateWritable) {
                Logger.d(getString(R.string.writable));
                //可写状态，可以发送指令，例如切换单位，获取历史数据等
//                sendUnitDataScale(deviceModel);
                isSendData = true;
                device_set_connect_state.setText("已连接");
//                ppScale.getTorreDeviceManager().startKeepAlive();
                ppScale.getTorreDeviceManager().readDeviceInfoFromCharacter(deviceInfoInterface);
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
                Toast.makeText(DeviceSetActivity.this, getString(R.string.system_bluetooth_disconnect), Toast.LENGTH_SHORT).show();
            } else if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOn) {
                Logger.d(getString(R.string.system_blutooth_on));
                Toast.makeText(DeviceSetActivity.this, getString(R.string.system_blutooth_on), Toast.LENGTH_SHORT).show();
            } else {
                Logger.e(getString(R.string.system_bluetooth_abnormal));
            }
        }
    };


    /**
     * 解析数据回调
     * <p>
     * bodyFatModel 身体数据
     * deviceModel 设备信息
     */
    private ProtocalFilterImpl getProtocalFilter() {
        final ProtocalFilterImpl protocalFilter = new ProtocalFilterImpl();

        protocalFilter.setPPProcessDateInterface(new PPProcessDateInterface() {
            @Override
            public void monitorProcessData(PPBodyBaseModel bodyBaseModel, PPDeviceModel deviceModel) {
                String weightStr = PPUtil.getWeight(bodyBaseModel.unit, bodyBaseModel.getPpWeightKg(), deviceModel.deviceAccuracyType.getType());
                weightTextView.setText("ProcessWeight:" + weightStr);
            }
        });

        protocalFilter.setPPLockDataInterface(new PPLockDataInterface() {
            @Override
            public void monitorLockData(PPBodyBaseModel bodyBaseModel, PPDeviceModel deviceModel) {
                String weightStr = PPUtil.getWeight(bodyBaseModel.unit, bodyBaseModel.getPpWeightKg(), deviceModel.deviceAccuracyType.getType());
                weightTextView.setText("LockWeight:" + weightStr);
                Logger.d("bodyBaseModel:" + bodyBaseModel.toString());
//                device_set_deviceinfo.setText(bodyBaseModel.toString());
                PPBodyFatModel ppBodyFatModel = new PPBodyFatModel(bodyBaseModel);
                device_set_deviceinfo.setText(ppBodyFatModel.toString());

            }

            @Override
            public void monitorOverWeight() {
                weightTextView.setText("OverWeight");
            }
        });
        protocalFilter.setDeviceInfoInterface(deviceInfoInterface);
        protocalFilter.setDeviceSetInfoInterface(new PPDeviceSetInfoInterface() {
            @Override
            public void monitorResetStateSuccess() {
                Toast.makeText(DeviceSetActivity.this, "重置成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void monitorResetStateFail() {
                Toast.makeText(DeviceSetActivity.this, "重置失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void monitorLightValueChange(int light) {
                Toast.makeText(DeviceSetActivity.this, "当前亮度" + light, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void monitorLightReviseSuccess() {
                Toast.makeText(DeviceSetActivity.this, "亮度设置成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void monitorLightReviseFail() {
                Toast.makeText(DeviceSetActivity.this, "亮度设置失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void monitorMTUChange(PPDeviceModel deviceModel) {

            }
        });
        protocalFilter.setTorreConfigWifiInterface(new PPTorreConfigWifiInterface() {

            @Override
            public void configResult(PPConfigStateMenu configStateMenu, String resultCode) {
                Logger.d(configStateMenu.toString());
            }

            @Override
            public void monitorWiFiListSuccess(List<PPWifiModel> wifiModels) {
                Logger.d(wifiModels.toString());
                if (wifiModels != null) {
                    for (PPWifiModel wifiModel : wifiModels) {
                        wifi_name.append(wifiModel.getSsid() + "\n");
                    }
                }
            }
        });
        protocalFilter.setUserInfoInterface(new PPUserInfoInterface() {
            @Override
            public void getUserListSuccess(List<String> userIds) {

            }

            @Override
            public void syncUserInfoSuccess() {

            }

            @Override
            public void syncUserInfoFail() {

            }

            @Override
            public void deleteUserInfoSuccess(PPUserModel userModel) {
                Toast.makeText(DeviceSetActivity.this, "删除用户信息成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void deleteUserInfoFail(PPUserModel userModel) {

            }

            @Override
            public void confirmCurrentUserInfoSuccess() {

            }

            @Override
            public void confirmCurrentUserInfoFail() {

            }
        });
        protocalFilter.setPPHistoryDataInterface(new PPHistoryDataInterface() {
            @Override
            public void monitorHistoryData(PPBodyBaseModel bodyFatModel, String dateTime) {
                if (bodyFatModel != null) {
                    Logger.d("ppScale_  dateTime = " + dateTime + " bodyBaseModel weight kg = " + bodyFatModel.getPpWeightKg());
                }
                if (bodyFatModel != null) {
                    Logger.d("ppScale_ bodyFatModel = " + bodyFatModel.toString());
                    String weightStr = PPUtil.getWeight(bodyFatModel.unit, bodyFatModel.getPpWeightKg(), bodyFatModel.deviceModel.deviceAccuracyType.getType());
                }
            }

            @Override
            public void monitorHistoryEnd(PPDeviceModel deviceModel) {
                Logger.d("ppScale_ bodyFatModel = 历史数据读取完成");
                //历史数据结束，删除历史数据
//                        deleteHistoryData();
            }

            @Override
            public void monitorHistoryFail() {
                Logger.e("ppScale_ bodyFatModel = 历史数据读取失败");
            }

        });
        return protocalFilter;
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


    @Override
    public void onClick(View v) {
        if (isSendData) {
            switch (v.getId()) {
                case R.id.device_set_light:
                    int light = (int) (Math.random() * 100);
                    ppScale.getTorreDeviceManager().setLight(light);
                    break;
                case R.id.device_set_sync_log:
                    ppScale.getTorreDeviceManager().syncLog();
                    break;
                case R.id.device_set_reset:
                    ppScale.sendResetDevice();
                    break;
                case R.id.device_set_sync_time:
                    ppScale.sendSyncTimeDataAdoreScale(null);
                    break;
                case R.id.device_set_synchistory:
                    ppScale.getTorreDeviceManager().syncHistory(userModel);
                    break;
                case R.id.device_set_sync_userinfo:
                    ppScale.getTorreDeviceManager().syncUserInfo(userModel);
                    break;
                case R.id.device_set_wifi_list:
                    ppScale.getTorreDeviceManager().getWifiList();
                    break;
                case R.id.device_set_startConfigWifi://开始配网
                    String ssid = "IT42";
                    String password = "lefu123456";
                    String domainName = "http://nat.lefuenergy.com:10081";
                    ppScale.getTorreDeviceManager().configWifi(ssid, password, domainName);
                    break;
                case R.id.device_set_exitConfigWifi:
                    ppScale.getTorreDeviceManager().exitConfigWifi();
                    break;
                case R.id.device_set_delete_userinfo:
                    userModel.userID = "1006451068@qq.com";
                    userModel.memberID = "4C2D82A7-AA9B-46F2-99BB-8B82A1F63626";
                    userModel.userName = "AB";
                    ppScale.getTorreDeviceManager().deleteAllUserInfo(userModel);
                    break;
                case R.id.device_set_confirm_current_userinfo:
                    userModel.userID = "";
                    userModel.memberID = "";
                    ppScale.getTorreDeviceManager().confirmCurrentUser(userModel);
                    break;
                case R.id.device_set_get_userinfo_list:
                    ppScale.getTorreDeviceManager().getUserList();
                    break;
                case R.id.device_set_startLocalOTA:
                    ppScale.getTorreDeviceManager().startLocalOTA(onOTAStateListener);
                    break;
                case R.id.device_set_startOTA:
                    ppScale.getTorreDeviceManager().startUserOTA(onOTAStateListener);
                    break;
                case R.id.device_set_startDFU:
                    if (isCopyEnd) {
                        String dfuFilePath = this.dfuFilePath;//文件地址，统一放到包路径下的files/dfu/目录下getFilesDir().getAbsolutePath() + "/dfu/"
                        wifi_name.append("DFU 启动DFU" + "\n");
                        functinonTypeTvState.setText("DFU状态");
                        if (!ppScale.getTorreDeviceManager().isDFU()) {
                            boolean isFullyDFUState = whetherFullyDFUToggleBtn.isChecked();//是否全量升级
                            ppScale.getTorreDeviceManager().startDFU(isFullyDFUState, dfuFilePath, new OnDFUStateListener() {

                                @Override
                                public void onDfuFail(String errorType) {
                                    weightTextView.setText("DFU错误码：" + errorType);
                                    wifi_name.append("DFU 错误码：" + errorType + "\n");
                                }

                                @Override
                                public void onInfoOout(String outInfo) {
                                    wifi_name.append("DFU " + outInfo + "\n");
                                }

                                @Override
                                public void onStartSendDfuData() {
                                    wifi_name.append("DFU 开始发送文件数据" + "\n");
                                }

                                @Override
                                public void onDfuProgress(int progress) {
                                    weightTextView.setText(progress + "%");
                                }

                                @Override
                                public void onDfuSucess() {
                                    weightTextView.setText("DFU成功");
                                    wifi_name.append("DFU 所有文件发送成功" + "\n");
                                }
                            });
                        } else {
                            Toast.makeText(this, "请等待文件复制结束", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case R.id.device_set_getFilePath:
                    requestPermission();
                    break;
                case R.id.startMeasureBtn:
                    ppScale.getTorreDeviceManager().startMeasure(null);
                    break;
                case R.id.pregnancyMode:
                    //0打开 1关闭
                    ppScale.getTorreDeviceManager().controlImpendance(0);
                    break;
                case R.id.closePregnancyMode:
                    ppScale.getTorreDeviceManager().controlImpendance(1);
                    break;
                case R.id.getWifiSSID:
                    //设置设备模式切换监听
                    ppScale.getTorreDeviceManager().setPPTorreDeviceModeChangeInterface(modeChangeInterface);
//                    ppScale.getTorreDeviceManager().getWifiSSID();//获取WiFi SSID
                    ppScale.getTorreDeviceManager().getWifiMac();//获取WIFI MAC
                    break;
                case R.id.device_set_clearUser:
                    ppScale.getTorreDeviceManager().clearDeviceUserInfo(new PPClearDataInterface() {
                        @Override
                        public void onClearSuccess() {

                        }

                        @Override
                        public void onClearFail() {

                        }
                    });
                    break;

            }
        } else {
            Toast.makeText(this, "设备未连接", Toast.LENGTH_SHORT).show();
        }

    }

    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 先判断有没有权限
            if (Environment.isExternalStorageManager()) {
                performFileSearch();
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                startActivityForResult(intent, REQUEST_CODE);
            }
        } else {
            int permission_read = ContextCompat.checkSelfPermission(DeviceSetActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permission_read != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(DeviceSetActivity.this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, REQUEST_CODE);
            } else {
                performFileSearch();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ppScale != null) {
            if (ppScale.getTorreDeviceManager() != null) {
                ppScale.getTorreDeviceManager().stopDFU();
            }
            ppScale.stopSearch();
            ppScale.disConnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                performFileSearch();
            } else {
                Toast.makeText(DeviceSetActivity.this, "存储权限获取失败", Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == Activity.RESULT_OK && data != null) {
            //当单选选了一个文件后返回
            if (data.getData() != null) {
                handleSingleDocument(data);
            } else {
                //多选
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    Uri[] uris = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        uris[i] = clipData.getItemAt(i).getUri();
                    }
                }
            }
        }
    }

    //选择文件
    private void performFileSearch() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        //允许多选 长按多选
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.putExtra(Intent.EXTRA_ALARM_COUNT, true);
        //不限制选取类型
        intent.setType("*/*");
//        startActivityForResult(intent, -1);
        startActivityForResult(Intent.createChooser(intent, "选择DFU文件"), 1);
    }

    private void handleSingleDocument(Intent data) {
        Uri uri = data.getData();
        dfuFilePath = this.getFilesDir().getAbsolutePath() + "/dfu/";
        dfuFilePath = ZipFileUtil.zipUriToLocalFile(this, uri, dfuFilePath, new ZipFileUtil.ZipFileCallBack() {
            @Override
            public void onFilePath(String filePath) {
                wifi_name.append("DFU 升级文件路径：" + filePath + "\n");
            }
        });
        wifi_name.append("DFU 文件解压路径：" + dfuFilePath + "\n");
        isCopyEnd = true;
//        String filePath = FileUtils.getRealPath(this, uri);
//        isCopyEnd = true;    isCopyEnd = true;
//        if (filePath.endsWith(".zip")) {
//            String dfuFileName = unZip(filePath, dfuFilePath);
//            dfuFilePath = dfuFilePath + dfuFileName.replace(".zip", "") + File.separator;
//            wifi_name.append("DFU 文件解压路径：" + dfuFilePath + "\n");
//            isCopyEnd = true;
//        } else {
//            isCopyEnd = false;
//            FileUtils.moveDFUFile(this, filePath);
//            isCopyEnd = true;
//        }
    }

    private String unZip(String zipFielPath, String dfuFilePath) {
//        return ZipFileUtil.unZip(this, zipFielPath, dfuFilePath);
        return ZipFileUtil.unZip(this, zipFielPath, dfuFilePath);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                performFileSearch();
            } else {
                Toast.makeText(DeviceSetActivity.this, "存储权限获取失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    PPTorreDeviceModeChangeInterface modeChangeInterface = new PPTorreDeviceModeChangeInterface() {

        @Override
        public void readDeviceSsidCallBack(String ssid) {
            if (TextUtils.isEmpty(ssid)) {
                weightTextView.setText("未配网");
            } else {
                weightTextView.setText("ssid:" + ssid);
            }

        }

        @Override
        public void readDeviceWifiMacCallBack(String wifiMac) {
            if (TextUtils.isEmpty(wifiMac)) {
                weightTextView.setText("未获取到wifiMac");
            } else {
                weightTextView.setText("wifiMac:" + wifiMac);
            }
        }
    };

    OnOTAStateListener onOTAStateListener = new OnOTAStateListener() {

        @Override
        public void onUpdateFail() {
            Toast.makeText(DeviceSetActivity.this, "升级失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStartUpdate() {
            Toast.makeText(DeviceSetActivity.this, "开始升级", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onUpdateProgress(int progress) {
            device_set_startOTA.setText(String.format(Locale.CHINA, "启动WiFi升级(%d%%)", progress));
        }

        @Override
        public void onUpdateSucess() {
            Toast.makeText(DeviceSetActivity.this, "升级成功", Toast.LENGTH_SHORT).show();
            device_set_startOTA.setText("启动WiFi升级");
        }

        @Override
        public void onReadyToUpdate() {

        }

        @Override
        public void onUpdateEnd() {

        }

        @Override
        public boolean isOTA() {
            return false;
        }
    };

    PPDeviceInfoInterface deviceInfoInterface = new PPDeviceInfoInterface() {

        @Override
        public void serialNumber(PPDeviceModel deviceModel) {
        }

        @Override
        public void onIlluminationChange(int illumination) {
        }

        @Override
        public void readDeviceInfoComplete(PPDeviceModel deviceModel) {
            if (ppScale != null) {
                ppScale.getTorreDeviceManager().readDeviceBattery(this);
            }
            Logger.d("DeviceInfo :  " + deviceModel.toString());
            device_set_deviceinfo.setText(deviceModel.toString());
        }

        @Override
        public void readDevicePower(int power) {
            device_set_deviceinfo.append("power:" + power);
        }
    };


}
