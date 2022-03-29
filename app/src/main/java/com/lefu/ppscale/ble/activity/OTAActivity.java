package com.lefu.ppscale.ble.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lefu.healthu.business.mine.binddevice.BindDeviceWiFiLockSelectConfigNetDialog;
import com.lefu.ppscale.ble.R;
import com.peng.ppscale.business.ble.BleOptions;
import com.peng.ppscale.business.ble.PPScale;
import com.peng.ppscale.business.ble.listener.PPBleStateInterface;
import com.peng.ppscale.business.ble.listener.PPDeviceInfoInterface;
import com.peng.ppscale.business.ble.listener.PPLockDataInterface;
import com.peng.ppscale.business.ble.listener.PPProcessDateInterface;
import com.peng.ppscale.business.ble.listener.ProtocalFilterImpl;
import com.peng.ppscale.business.ota.OTAManager;
import com.peng.ppscale.business.ota.OnOTAStateListener;
import com.peng.ppscale.business.state.PPBleSwitchState;
import com.peng.ppscale.business.state.PPBleWorkState;
import com.peng.ppscale.util.Logger;
import com.peng.ppscale.vo.PPBodyBaseModel;
import com.peng.ppscale.vo.PPBodyFatModel;
import com.peng.ppscale.vo.PPDeviceModel;

import java.util.ArrayList;
import java.util.List;

public class OTAActivity extends AppCompatActivity {

    TextView weightTextView;
    PPScale ppScale;

    public static final String SEARCH_TYPE = "SearchType";

    private AlertDialog alertDialog;
    private OTAManager otaManager;
    private String otaAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bingdevice);
        weightTextView = findViewById(R.id.weightTextView);

        initPPScale();
    }

    private void startScanData() {
        otaManager = OTAManager.getInstance();
        otaManager.addOnOtaStateListener(new OnOTAStateListener() {
            @Override
            public void onUpdateFail() {
                isUpgradeSuccess = false;
                Logger.e("ota is fail");
                weightTextView.setText("ota is fail");
            }

            @Override
            public void onStartUpdate() {
                isUpgradeSuccess = false;
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
                Logger.d("ota is start");
                weightTextView.setText("ready update");
                otaManager.startUpgradeRequest("cf818_ota_20_v3.1.bin", OTAActivity.this);
                isUpgradeSuccess = false;
            }

            @Override
            public void onUpdateEnd() {
                if (ppScale != null) {
                    ppScale.stopSearch();
                    ppScale.disConnect();
                }
                finish();
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
        otaAddress = getIntent().getStringExtra("otaAddress");

        List<String> addressList = new ArrayList<>();
        addressList.add(otaAddress);
        //绑定新设备
        ppScale = new PPScale.Builder(this)
                .setProtocalFilterImpl(getProtocalFilter())
                .setBleOptions(getBleOptions())
                .setDeviceList(addressList)
                .setBleStateInterface(bleStateInterface)
                .build();

        //初始化PPSCale
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //启动扫描
                startScanData();
            }
        }, 1000);

    }

    public void reStartConnect(PPDeviceModel deviceModel) {
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
    protected void onPause() {
        super.onPause();
        ppScale.stopSearch();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ppScale != null) {
            ppScale.stopSearch();
            ppScale.disConnect();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}



