package com.lefu.ppscale.wifi.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lefu.base.SettingManager;
import com.lefu.ppscale.db.dao.DBManager;
import com.lefu.ppscale.db.dao.DeviceModel;
import com.lefu.ppscale.wifi.R;
import com.lefu.ppscale.wifi.net.okhttp.DataTask;
import com.lefu.ppscale.wifi.net.okhttp.NetUtil;
import com.lefu.ppscale.wifi.net.okhttp.RetCallBack;
import com.lefu.ppscale.wifi.net.okhttp.vo.SaveWifiGroupBean;
import com.lefu.ppscale.wifi.util.WifiUtil;
import com.peng.ppscale.business.ble.BleOptions;
import com.peng.ppscale.business.ble.PPScale;
import com.peng.ppscale.business.ble.configWifi.PPConfigWifiInfoInterface;
import com.peng.ppscale.business.ble.configWifi.PPConfigWifiInterface;
import com.peng.ppscale.business.ble.listener.PPBleSendResultCallBack;
import com.peng.ppscale.business.ble.listener.PPBleStateInterface;
import com.peng.ppscale.business.ble.listener.ProtocalFilterImpl;
import com.peng.ppscale.business.state.PPBleSwitchState;
import com.peng.ppscale.business.state.PPBleWorkState;
import com.peng.ppscale.util.Logger;
import com.peng.ppscale.vo.PPDeviceModel;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;

public class BleConfigWifiActivity extends AppCompatActivity {
    private final static int MSG_START_HINT = 1;
    int RET_CODE_SYSTEM_WIFI_SETTINGS = 8161;

    private PPScale ppScale;
    private EditText etWifiName;
    private EditText etWifiKey;

    WifiUtil mWifiUtil;
    private boolean is2_4G = false;//2.4G false    非2.4G true
    private Timer spHintTimer;
    private TimerTask spHintTimerTask;
    private static PreviewHandler mHandler;
    private TextView tvHint;
    private TextView tvOthers;
    private TextView snTv;
    private String address;
    private String ssid;

    String scaleDomain = NetUtil.SCALE_DOMAIN;
    private TextView tvNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_config);

        mHandler = new PreviewHandler(this);

        address = getIntent().getStringExtra("address");
        initView();

    }

    private void initView() {
        TextView tv_loginBack = findViewById(R.id.tv_loginBack);
        if (tv_loginBack != null) {
            tv_loginBack.setVisibility(View.VISIBLE);
            tv_loginBack.setText("");
            tv_loginBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        tvHint = findViewById(R.id.tvHint);
        if (tvHint != null) {
            tvHint.setVisibility(View.INVISIBLE);
        }
        tvNext = findViewById(R.id.tvNext);
        etWifiName = findViewById(R.id.etWifiName);
        etWifiKey = findViewById(R.id.etWifiKey);
        snTv = findViewById(R.id.snTv);
        if (etWifiName != null) {
            etWifiName.setText("");
            etWifiName.setKeyListener(null);
        }
        findViewById(R.id.tvNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!is2_4G) {
                    startNextStep();
                }
            }
        });

        tvOthers = findViewById(R.id.tvOthers);
        if (tvOthers != null) {
            tvOthers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // open system wifi settings
                    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    startActivityForResult(intent, RET_CODE_SYSTEM_WIFI_SETTINGS);
                }
            });
        }
        tvNext.setEnabled(true);
    }

    private void startNextStep() {
        tvNext.setEnabled(false);
        snTv.setText("");
        ProtocalFilterImpl protocalFilter = new ProtocalFilterImpl();
        protocalFilter.setConfigWifiInterface(new PPConfigWifiInterface() {

            /**
             * wifi设备配网成功并获取到SN
             *
             * @param sn 设备识别码
             */
            @Override
            public void monitorConfigState(final String sn, PPDeviceModel deviceModel) {
                tvNext.setEnabled(true);
                //拿到sn 处理业务逻辑
                Logger.e("xxxxxxxxxxxx-" + sn);
                Logger.e("xxxxxxxxxxxx-deviceName = " + deviceModel.getDeviceName() + " mac = " + deviceModel.getDeviceMac());
                stopPPScale();
                snTv.setText("sn:" + sn);
                Map<String, String> map = new HashMap<>();
                map.put("sn", sn);
                map.put("uid", SettingManager.get().getUid());

                DataTask.post(NetUtil.SAVE_WIFI_GROUP, map, new RetCallBack<SaveWifiGroupBean>(SaveWifiGroupBean.class) {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(BleConfigWifiActivity.this, R.string.config_wifi_fail, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(SaveWifiGroupBean response, int id) {
                        if (response.isStatus()) {
                            Toast.makeText(BleConfigWifiActivity.this, R.string.config_wifi_success, Toast.LENGTH_SHORT).show();
                            DeviceModel device = DBManager.manager().getDevice(address);
                            if (device != null) {
                                device.setSn(sn);
                                device.setSsid(ssid);
                                DBManager.manager().updateDevice(device);
                            }
                            finish();
                        } else {
                            String content = TextUtils.isEmpty(response.getMsg()) ? getString(R.string.config_wifi_fail) : response.getMsg();
                            Toast.makeText(BleConfigWifiActivity.this, content, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
        protocalFilter.setConfigWifiInfoInterface(new PPConfigWifiInfoInterface() {
            @Override
            public void monitorConfigSn(String sn, PPDeviceModel deviceModel) {

            }

            @Override
            public void monitorConfigSsid(String ssid, PPDeviceModel deviceModel) {
            }

            @Override
            public void monitorConfigPassword(String password, PPDeviceModel deviceModel) {
            }

            @Override
            public void monitorModifyServerDNSSuccess() {
                Logger.d("DNS下发成功，开始下发配网");
                //wifi秤配网
                startConfigWifi(null);
            }

            @Override
            public void monitorModifyServerIpSuccess() {

            }
        });

        ppScale = new PPScale.Builder(this)
                .setProtocalFilterImpl(protocalFilter)
                .setBleOptions(getBleOptions())
                .setBleStateInterface(bleStateInterface)
                .build();
        ppScale.connectAddress(address);
    }

    /**
     * 参数配置
     */
    private BleOptions getBleOptions() {
        return new BleOptions.Builder()
                .setFeaturesFlag(BleOptions.ScaleFeatures.FEATURES_CONFIG_WIFI)
                .build();
    }

    PPBleStateInterface bleStateInterface = new PPBleStateInterface() {

        @Override
        public void monitorBluetoothWorkState(PPBleWorkState ppBleWorkState, PPDeviceModel ppDeviceModel) {
            if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnected) {
                Logger.d(getString(R.string.device_connected));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnecting) {
                Logger.d(getString(R.string.device_connecting));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateDisconnected) {
                Logger.d(getString(R.string.device_disconnected));
            } else if (ppBleWorkState == PPBleWorkState.PPBleStateSearchCanceled) {
                Logger.d(getString(R.string.stop_scanning));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkSearchTimeOut) {
                Logger.d(getString(R.string.stop_scanning));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateSearching) {
                Logger.d(getString(R.string.scanning));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateWritable) {
                Logger.d(getString(R.string.Writable));
                sendModifyServerDomain();
            } else {
                Logger.e(getString(R.string.bluetooth_status_is_abnormal));
            }
        }

        @Override
        public void monitorBluetoothSwitchState(PPBleSwitchState ppBleSwitchState) {
            if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOff) {
                Logger.e(getString(R.string.system_bluetooth_disconnect));
                Toast.makeText(BleConfigWifiActivity.this, getString(R.string.system_bluetooth_disconnect), Toast.LENGTH_SHORT).show();
            } else if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOn) {
                Logger.d(getString(R.string.system_blutooth_on));
                Toast.makeText(BleConfigWifiActivity.this, getString(R.string.system_blutooth_on), Toast.LENGTH_SHORT).show();
            } else {
                Logger.e(getString(R.string.system_bluetooth_abnormal));
            }
        }
    };

    /**
     * 绑定时请确保WIFI是2.4G，并且账号密码正确
     */
    private void startConfigWifi(PPBleSendResultCallBack sendResultCallBack) {
        ssid = etWifiName.getText().toString();
        String password = etWifiKey.getText().toString();
        if (ppScale != null) {
            ppScale.configWifi(ssid, password, sendResultCallBack);
        }
    }

    /**
     * 配置秤端域名
     */
    private void sendModifyServerDomain() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ppScale != null && !TextUtils.isEmpty(scaleDomain)) {
                    ppScale.sendModifyServerDNS(scaleDomain);
                } else {
                    startConfigWifi(null);
                }
            }
        }, 500);
    }

    private void stopPPScale() {
        if (ppScale != null) {
//            ppScale.stopWifiConfig();
            ppScale.disConnect();
            ppScale.stopSearch();
            ppScale = null;
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Logger.d("liyp_  onPostResume");
        checkWifiTimer();

    }

    private void checkWifiTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        checkWifiIs2_4G();
                    }
                });
            }
        }, 800);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPPScale();
        stopSplashHint();
    }

    private boolean checkWifiIs2_4G() {
        if (mWifiUtil == null) {
            mWifiUtil = WifiUtil.getInstance(this);
        }
        Logger.d("liyp_ isWifiConnect = " + mWifiUtil.isWifiConnect());
        if (!mWifiUtil.isWifiConnect()) {
            Toast.makeText(this, getString(R.string.wifi_config_disconnected), Toast.LENGTH_SHORT).show();
            return false;
        }
        String wifiName = mWifiUtil.getCurrentSSID();
        if (wifiName != null && etWifiName != null) {
            etWifiName.setText(wifiName);
        }
        is2_4G = !mWifiUtil.is2_4GFrequency();
        showSplashHint();
        verifyPwdStatus(is2_4G);
        return is2_4G;
    }

    private void showSplashHint() {
        stopTimer();

        initTimer();
        spHintTimer.schedule(spHintTimerTask, 100, 1000);
    }

    private void initTimer() {
        spHintTimer = new Timer();
        spHintTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (mHandler != null) {
                    Message msg = mHandler.obtainMessage();
                    msg.what = MSG_START_HINT;
                    mHandler.sendMessage(msg);
                }
            }
        };
    }

    private void verifyPwdStatus(boolean b) {
        if (etWifiKey != null) {
            if (b) {
                etWifiKey.setInputType(EditorInfo.TYPE_NULL);
                etWifiKey.setEnabled(false);
                etWifiKey.setFocusable(false);
                etWifiKey.setCursorVisible(false);
                etWifiKey.setFocusableInTouchMode(false);
            } else {
                etWifiKey.setFocusableInTouchMode(true);
                etWifiKey.setCursorVisible(true);
                etWifiKey.setFocusable(true);
                etWifiKey.setEnabled(true);
                etWifiKey.setInputType(EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD);
//                setKeyStatus();
            }
        }
    }

    private void stopTimer() {
        if (spHintTimer != null) {
            spHintTimer.cancel();
            spHintTimer = null;
        }
        if (spHintTimerTask != null) {
            spHintTimerTask.cancel();
            spHintTimerTask = null;
        }
    }

    private void stopSplashHint() {
        if (spHintTimer != null) {
            spHintTimer.cancel();
            spHintTimer = null;
        }
        tvHint.setVisibility(View.INVISIBLE);
        etWifiKey.setEnabled(true);
    }


    private static class PreviewHandler extends Handler {
        private final WeakReference<BleConfigWifiActivity> mActivity;

        PreviewHandler(BleConfigWifiActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BleConfigWifiActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case MSG_START_HINT:
                        if (activity.tvHint != null) {
                            if (!activity.is2_4G) {
                                activity.tvHint.setVisibility(View.INVISIBLE);
                            } else {
//                                activity.checkWifiIs2_4G();
                                activity.is2_4G = !activity.mWifiUtil.is2_4GFrequency();
                                if (activity.tvHint.getVisibility() == View.INVISIBLE) {
                                    activity.tvHint.setVisibility(View.VISIBLE);
                                } else {
                                    activity.tvHint.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (spHintTimer != null) {
            spHintTimer.cancel();
            spHintTimer = null;
        }
        if (mHandler != null) {
            mHandler.removeMessages(MSG_START_HINT);
            mHandler.removeCallbacks(null);
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RET_CODE_SYSTEM_WIFI_SETTINGS) {
            if (resultCode == Activity.RESULT_CANCELED) {
                return;
            } else {

            }
        }
    }

}
