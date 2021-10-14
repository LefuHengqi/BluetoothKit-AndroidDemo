package com.lefu.ppscale.ble.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import com.lefu.ppscale.ble.R;
import com.peng.ppscale.business.ble.BleOptions;
import com.peng.ppscale.business.ble.PPScale;
import com.peng.ppscale.business.ble.configWifi.PPConfigWifiInterface;
import com.peng.ppscale.business.ble.listener.PPBleStateInterface;
import com.peng.ppscale.business.ble.listener.ProtocalFilterImpl;
import com.peng.ppscale.business.state.PPBleSwitchState;
import com.peng.ppscale.business.state.PPBleWorkState;
import com.peng.ppscale.util.Logger;
import com.peng.ppscale.vo.PPDeviceModel;

public class BleConfigWifiActivity extends AppCompatActivity {

    private PPScale ppScale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_config_wifi);


        ProtocalFilterImpl protocalFilter = new ProtocalFilterImpl();
        protocalFilter.setConfigWifiInterface(new PPConfigWifiInterface() {

            /**
             * wifi设备配网成功并获取到SN
             *
             * @param sn 设备识别码
             */
            @Override
            public void monitorConfigState(String sn, PPDeviceModel deviceModel) {
                //拿到sn 处理业务逻辑
                Logger.e("xxxxxxxxxxxx-" + sn);
                Logger.e("xxxxxxxxxxxx-deviceName = " + deviceModel.getDeviceName() + " mac = " + deviceModel.getDeviceMac());
                ppScale.stopWifiConfig();
                ppScale.stopSearch();
                ppScale.disConnect();
                finish();
            }
        });

        ppScale = new PPScale.Builder(this)
                .setProtocalFilterImpl(protocalFilter)
                .setBleOptions(getBleOptions())
                .setBleStateInterface(bleStateInterface)
                .build();
        ppScale.startSearchBluetoothScaleWithMacAddressList();

    }

    /**
     * 参数配置 绑定时请确保WIFI是2.4G，并且账号密码正确
     *
     * @param password     WIFI密码
     * @param featuresFlag 具备的能力，WIFI秤{@link BleOptions.ScaleFeatures#FEATURES_CONFIG_WIFI}
     *                     具备的能力，体重秤{@link BleOptions.ScaleFeatures#FEATURES_WEIGHT}
     *                     具备的能力，脂肪秤{@link BleOptions.ScaleFeatures#FEATURES_FAT}
     *                     具备的能力，心率秤{@link BleOptions.ScaleFeatures#FEATURES_HEART_RATE}
     *                     具备的能力，离线秤{@link BleOptions.ScaleFeatures#FEATURES_HISTORY}
     *                     具备的能力，闭目单脚秤{@link BleOptions.ScaleFeatures#FEATURES_BMDJ}
     * @return
     * @parm ssid          WIFI账号  不可为空
     */
    private BleOptions getBleOptions() {
        return new BleOptions.Builder()
                .setFeaturesFlag(BleOptions.ScaleFeatures.FEATURES_CONFIG_WIFI)
                .setPassword("lefu123456")
                .setSsid("IT36")
                .build();
    }

    PPBleStateInterface bleStateInterface = new PPBleStateInterface() {
        @Override
        public void monitorBluetoothWorkState(PPBleWorkState ppBleWorkState, PPDeviceModel deviceModel) {
            if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnected) {
                Logger.d("设备已连接");
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnecting) {
                Logger.d("设备连接中");
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateDisconnected) {
                Logger.d("设备已断开");
            } else if (ppBleWorkState == PPBleWorkState.PPBleStateSearchCanceled) {
                Logger.d("停止扫描");
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkSearchTimeOut) {
                Logger.d("停止扫描");
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateSearching) {
                Logger.d("扫描中");
            } else {
                Logger.e("蓝牙状态异常");
            }
        }

        @Override
        public void monitorBluetoothSwitchState(PPBleSwitchState ppBleSwitchState) {
            if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOff) {
                Logger.e("系统蓝牙断开");
                Toast.makeText(BleConfigWifiActivity.this, "系统蓝牙断开", Toast.LENGTH_SHORT).show();
            } else if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOn) {
                Logger.d("系统蓝牙打开");
                Toast.makeText(BleConfigWifiActivity.this, "系统蓝牙打开", Toast.LENGTH_SHORT).show();
            } else {
                Logger.e("系统蓝牙异常");
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        ppScale.stopSearch();
        ppScale.stopWifiConfig();
        ppScale.disConnect();
    }
}
