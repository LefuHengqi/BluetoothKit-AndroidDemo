package com.lefu.ppscale.ble.torre;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.peng.ppscale.business.torre.listener.PPTorreConfigWifiInterface;
import com.peng.ppscale.business.ble.listener.PPUserInfoInterface;
import com.peng.ppscale.business.ble.listener.ProtocalFilterImpl;
import com.peng.ppscale.business.device.PPUnitType;
import com.peng.ppscale.business.ota.OnOTAStateListener;
import com.peng.ppscale.business.state.PPBleSwitchState;
import com.peng.ppscale.business.state.PPBleWorkState;
import com.peng.ppscale.util.Logger;
import com.peng.ppscale.util.PPUtil;
import com.peng.ppscale.vo.PPBodyFatModel;
import com.peng.ppscale.vo.PPDeviceModel;
import com.peng.ppscale.vo.PPUserModel;
import com.peng.ppscale.vo.PPWifiModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DeviceSetActivity extends AppCompatActivity implements View.OnClickListener {

    private PPUnitType unitType;
    private PPUserModel userModel;
    private PPScale ppScale;
    private PPScale.Builder builder1;
    private boolean isSendData;
    private String address;
    private TextView device_set_deviceinfo, wifi_name;
    private TextView device_set_connect_state;
    private Button device_set_light, device_set_sync_log, device_set_sync_time, device_set_reset,
            device_set_synchistory, device_set_startOTA, device_set_sync_userinfo, device_set_wifi_list, device_set_startConfigWifi,
            device_set_exitConfigWifi, device_set_delete_userinfo, device_set_confirm_current_userinfo, device_set_get_userinfo_list;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_set_layout);

        unitType = DataUtil.util().getUnit();

        userModel = DataUtil.util().getUserModel();
        userModel.userID = "1006451068@qq.com";
        userModel.memberID = "4C2D82A7-AA9B-46F2-99BB-8B82A1F63626";
        userModel.userName = "AB";

        address = getIntent().getStringExtra("address");

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
        wifi_name = findViewById(R.id.wifi_name);

        device_set_light.setOnClickListener(this);
        device_set_sync_log.setOnClickListener(this);
        device_set_sync_time.setOnClickListener(this);
        device_set_reset.setOnClickListener(this);
        device_set_synchistory.setOnClickListener(this);
        device_set_startOTA.setOnClickListener(this);
        device_set_sync_userinfo.setOnClickListener(this);
        device_set_wifi_list.setOnClickListener(this);
        device_set_startConfigWifi.setOnClickListener(this);
        device_set_exitConfigWifi.setOnClickListener(this);
        device_set_delete_userinfo.setOnClickListener(this);
        device_set_confirm_current_userinfo.setOnClickListener(this);
        device_set_get_userinfo_list.setOnClickListener(this);

        //初始化PPSCale
        initPPScale();

//        initBitmap();
    }

    private void initBitmap() {


        ImageView userNameImage = findViewById(R.id.userNameImage);

        Bitmap bitmap = Bitmap.createBitmap(104, 32, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
//            canvas.drawColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.BLACK);
        int sp = 20;
        paint.setTextSize(sp);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        Rect rect = new Rect(0, 0, 104, 32);

        int textWidth = getTextWidth(paint, "123");

        int baseline = (int) ((rect.bottom - rect.top) / 2
                + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom);

        canvas.drawText("123", Math.abs((rect.width() - textWidth) / 2), baseline, paint);

        userNameImage.setImageBitmap(bitmap);

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
                Logger.d("sync log path :" + logFilePath);
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
        protocalFilter.setDeviceInfoInterface(new PPDeviceInfoInterface() {

            @Override
            public void serialNumber(PPDeviceModel deviceModel) {
            }

            @Override
            public void onIlluminationChange(int illumination) {

            }

            @Override
            public void readDeviceInfoComplete(PPDeviceModel deviceModel) {
                Logger.d("DeviceInfo :  " + deviceModel.toString());
                device_set_deviceinfo.setText(deviceModel.toString());
            }
        });
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
            public void deleteUserInfoSuccess() {
                Toast.makeText(DeviceSetActivity.this, "删除用户信息成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void deleteUserInfoFail() {

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
            public void monitorHistoryData(PPBodyFatModel bodyFatModel, String dateTime) {
                if (bodyFatModel != null) {
                    Logger.d("ppScale_  dateTime = " + dateTime + " bodyBaseModel weight kg = " + bodyFatModel.getPpWeightKg());
                }
                if (bodyFatModel != null) {
                    Logger.d("ppScale_ bodyFatModel = " + bodyFatModel.toString());
                    String weightStr = PPUtil.getWeight(bodyFatModel.getUnit(), bodyFatModel.getPpWeightKg(), bodyFatModel.getDeviceModel().deviceAccuracyType.getType());
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
                    ppScale.sendSyncTimeDataAdoreScale();
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
                    ppScale.getTorreDeviceManager().deleteAllUserInfo();
                    break;
                case R.id.device_set_confirm_current_userinfo:
                    ppScale.getTorreDeviceManager().confirmCurrentUser();
                    break;
                case R.id.device_set_get_userinfo_list:
                    ppScale.getTorreDeviceManager().getUserList();
                    break;
                case R.id.device_set_startOTA:
                    ppScale.getTorreDeviceManager().startOTA(new OnOTAStateListener() {

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
                    });
                    break;
            }
        } else {
            Toast.makeText(this, "设备未连接", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ppScale != null) {
            ppScale.stopSearch();
            ppScale.disConnect();
        }
    }
}