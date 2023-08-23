package com.lefu.ppscale.wifi.develop;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.lefu.ppscale.wifi.R;
import com.lefu.ppscale.wifi.net.okhttp.NetUtil;
import com.peng.ppscale.business.ble.BleOptions;
import com.peng.ppscale.business.ble.PPScale;
import com.peng.ppscale.business.ble.configWifi.PPConfigWifiInfoInterface;
import com.peng.ppscale.business.ble.listener.PPBleStateInterface;
import com.peng.ppscale.business.ble.listener.ProtocalFilterImpl;
import com.peng.ppscale.business.state.PPBleSwitchState;
import com.peng.ppscale.business.state.PPBleWorkState;
import com.peng.ppscale.util.Logger;
import com.peng.ppscale.vo.PPDeviceModel;
import com.peng.ppscale.vo.PPUserModel;

import java.util.ArrayList;
import java.util.List;

public class DeveloperActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String ADDRESS = "address";

    ImageView iv_Left;
    TextView tvTitle;
    Button reStartConnectView;
    EditText inputServerIP;
    EditText inputServerDomain;
    TextView mPasswordView;
    TextView mSsidView;
    TextView mSnView;
    Button developer_mode_id_clearSSID;

    private PPScale ppScale;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.developer_activity);

        initView();
        initData();
    }

    protected void initView() {
        iv_Left = findViewById(R.id.iv_Left);
        tvTitle = findViewById(R.id.tv_title);
        reStartConnectView = findViewById(R.id.developer_mode_id_startConnect);
        inputServerIP = findViewById(R.id.developer_mode_id_input_modifyServerIP);
        inputServerDomain = findViewById(R.id.developer_mode_id_input_modifyServerDomain);
        mPasswordView = findViewById(R.id.developer_mode_id_password);
        mSsidView = findViewById(R.id.developer_mode_id_ssid);
        mSnView = findViewById(R.id.developer_mode_id_sn);
        developer_mode_id_clearSSID = findViewById(R.id.developer_mode_id_clearSSID);
        Button developer_mode_id_getSSID = findViewById(R.id.developer_mode_id_getSSID);
        Button developer_mode_id_modifyServerIP = findViewById(R.id.developer_mode_id_modifyServerIP);
        Button developer_mode_id_modifyServerDNS = findViewById(R.id.developer_mode_id_modifyServerDomain);
        iv_Left.setVisibility(View.VISIBLE);
        developer_mode_id_getSSID.setOnClickListener(this);
        developer_mode_id_modifyServerIP.setOnClickListener(this);
        developer_mode_id_modifyServerDNS.setOnClickListener(this);
        reStartConnectView.setOnClickListener(this);
        developer_mode_id_clearSSID.setOnClickListener(this);
        iv_Left.setOnClickListener(this);

        inputServerDomain.setText(NetUtil.SCALE_DOMAIN);
    }

    protected void initData() {
        startScanBle();
    }

    private void startScanBle() {
        String address = getIntent().getStringExtra(ADDRESS);
        List<String> devices = new ArrayList<>();
        devices.add(address);

        if (ppScale == null) {
            BleOptions bleOptions = new BleOptions.Builder()
                    .build();

            ProtocalFilterImpl protocalFilter = new ProtocalFilterImpl();

            protocalFilter.setConfigWifiInfoInterface(ppConfigWifiInfoInterface);

            PPUserModel userModel = new PPUserModel.Builder().build();

            ppScale = new PPScale
                    .Builder(this)
                    .setUserModel(userModel)
                    .setBleStateInterface(ppBleStateInterface)
                    .setProtocalFilterImpl(protocalFilter)
                    .build();
        }

        ppScale.startSearchBluetoothScaleWithMacAddressList();
    }

    PPConfigWifiInfoInterface ppConfigWifiInfoInterface = new PPConfigWifiInfoInterface() {

        @Override
        public void monitorConfigSn(String sn, PPDeviceModel ppDeviceModel) {
            Logger.d("configwifi 获取到sn:" + sn);
            mSnView.setText(sn);
        }

        @Override
        public void monitorConfigSsid(String ssid, PPDeviceModel ppDeviceModel) {
            Logger.d("configwifi 获取到ssid:" + ssid);
            if (!TextUtils.isEmpty(ssid)) {
                mSsidView.setText(ssid);
            }
        }

        @Override
        public void monitorConfigPassword(String password, PPDeviceModel ppDeviceModel) {
            Logger.d("configwifi 获取到password:" + password);
            if (!TextUtils.isEmpty(password)) {
                mPasswordView.setText(password);
            }
        }

        @Override
        public void monitorModifyServerDNSSuccess() {
            Toast.makeText(DeveloperActivity.this, R.string.dns_sent_successfully, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void monitorModifyServerIpSuccess() {
            Toast.makeText(DeveloperActivity.this, R.string.service_ip_sent_successfully, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void monitorConfigFail() {

        }
    };

    PPBleStateInterface ppBleStateInterface = new PPBleStateInterface() {

        @Override
        public void monitorBluetoothWorkState(PPBleWorkState ppBleWorkState, PPDeviceModel ppDeviceModel) {
            onBleWorkStateChange(ppBleWorkState, ppDeviceModel);
        }

        @Override
        public void monitorBluetoothSwitchState(PPBleSwitchState ppBleSwitchState) {
            switch (ppBleSwitchState) {
                case PPBleSwitchStateOn:
                    tvTitle.setText(R.string.system_blutooth_on);
                    break;
                case PPBleSwitchStateOff:
                    tvTitle.setText(R.string.system_bluetooth_disconnect);
                    break;
            }
            reStartConnectView.setVisibility(View.VISIBLE);
        }
    };

    private void onBleWorkStateChange(PPBleWorkState ppBleWorkState, PPDeviceModel ppDeviceModel) {
        if (reStartConnectView != null) {
            reStartConnectView.setVisibility(View.GONE);
            switch (ppBleWorkState) {
                case PPBleStateSearchCanceled:
                    Logger.d(getString(R.string.scan_cancle));

//                    tvTitle.setText(getString(R.string.scan_cancle));
                    break;
                case PPBleWorkSearchTimeOut:
                    Logger.d(getString(R.string.scan_time_out));

                    tvTitle.setText(getString(R.string.scan_time_out));
                    break;
                case PPBleWorkStateConnected:
                    Logger.d(getString(R.string.device_connected));

                    tvTitle.setText(getString(R.string.device_connected));
                    break;
                case PPBleWorkStateConnectable:
                    Logger.d("可连");
                    if (ppScale != null) {
                        ppScale.stopSearch();
//                        ppScale.connectDevice(ppDeviceModel);
                    }
                    tvTitle.setText(getString(R.string.device_connected));
                    break;
                case PPBleWorkStateSearching:
                    Logger.d(getString(R.string.scanning));
                    tvTitle.setText(getString(R.string.scanning));
                    break;
                case PPBleWorkStateConnecting:
                    Logger.d(getString(R.string.device_connecting));
                    tvTitle.setText(getString(R.string.device_connecting));
                    break;
                case PPBleWorkStateDisconnected:
                    Logger.d(getString(R.string.device_disconnected));
                    tvTitle.setText(getString(R.string.device_disconnected));
                    reStartConnectView.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ppScale != null) {
            ppScale.stopSearch();
//            ppScale.disConnect();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_Left) {
            finish();
        } else if (id == R.id.developer_mode_id_getSSID) {
            if (ppScale != null) {
//                ppScale.sendInquityWifiConfig();
            }
        } else if (id == R.id.developer_mode_id_modifyServerIP) {
            String ip = inputServerIP.getText().toString();
            if (!TextUtils.isEmpty(ip)) {
                ip = ip.trim();
                if (ppScale != null) {
//                    ppScale.sendModifyServerIp(ip);
                }
            }
        } else if (id == R.id.developer_mode_id_modifyServerDomain) {
            String dns = inputServerDomain.getText().toString();
            if (!TextUtils.isEmpty(dns)) {
                dns = dns.trim();
                if (ppScale != null) {
//                    ppScale.sendModifyServerDNS(dns);
                }
            }
        } else if (id == R.id.developer_mode_id_startConnect) {
            startScanBle();
        } else if (id == R.id.developer_mode_id_clearSSID) {
            if (ppScale != null) {
//                ppScale.sendDeleteWifiConfig();
            }
        }
    }
}
