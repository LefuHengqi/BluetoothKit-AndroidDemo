package com.lefu.ppscale.ble.foodscale;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lefu.ppscale.ble.R;
import com.lefu.ppscale.ble.util.UnitUtil;
import com.lefu.ppscale.ble.util.DataUtil;
import com.lefu.ppscale.db.dao.DBManager;
import com.lefu.ppscale.db.dao.DeviceModel;
import com.peng.ppscale.business.ble.BleOptions;
import com.peng.ppscale.business.ble.PPScale;
import com.peng.ppscale.business.ble.listener.FoodScaleDataProtocoInterface;
import com.peng.ppscale.business.device.DeviceManager;
import com.peng.ppscale.vo.LFFoodScaleGeneral;
import com.peng.ppscale.business.ble.listener.PPBleStateInterface;
import com.peng.ppscale.business.ble.listener.ProtocalFilterImpl;
import com.peng.ppscale.business.device.PPUnitType;
import com.peng.ppscale.business.state.PPBleSwitchState;
import com.peng.ppscale.business.state.PPBleWorkState;
import com.peng.ppscale.util.Energy;
import com.peng.ppscale.util.EnergyUnit;
import com.peng.ppscale.util.EnergyUnitLbOz;
import com.peng.ppscale.util.Logger;
import com.peng.ppscale.vo.PPDeviceModel;
import com.peng.ppscale.vo.PPScaleDefine;
import com.peng.ppscale.vo.PPUserModel;

import java.util.ArrayList;
import java.util.List;

public class FoodSclaeDeviceActivity extends AppCompatActivity {

    TextView weightTextView;
    TextView historyDataText;
    PPScale ppScale;
    TextView textView;
    private PPScale.Builder builder1;
    private PPUnitType unitType;
    private PPUserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_scale_layout);
        weightTextView = findViewById(R.id.weightTextView);
        historyDataText = findViewById(R.id.historyDataText);
        textView = findViewById(R.id.textView7);

        unitType = DataUtil.util().getUnit();
        userModel = DataUtil.util().getUserModel();

        //初始化PPSCale
        initPPScale();


        PPDeviceModel deviceModel = new PPDeviceModel("", DeviceManager.KITCHEN_SCALE);
        deviceModel.deviceAccuracyType = PPScaleDefine.PPDeviceAccuracyType.PPDeviceAccuracyTypePointG;

        String value = FoodScaleCacluteHelper.getValue(textView.getContext(), 151.0f, PPUnitType.PPUnitMLMilk, deviceModel);
        Log.d("liyp_", value);

    }

    private void startScanData() {
        ppScale.startSearchDeviceList(30000, null);
    }

    private void initPPScale() {
        //赛选出本地有的食物秤，没有传null
        List<DeviceModel> deviceList = DBManager.manager().getDeviceList();
        List<String> addressList = new ArrayList<>();
//        addressList.add(deviceList);
        if (deviceList != null && !deviceList.isEmpty()) {
            for (DeviceModel deviceModel : deviceList) {
                addressList.add(deviceModel.getDeviceMac());
            }
        }

        if (builder1 == null) {
            builder1 = new PPScale.Builder(this);
        }
        if (ppScale == null) {
            ppScale = builder1.setProtocalFilterImpl(getProtocalFilter())
//                    .setBleOptions(getBleOptions())
//                    .setDeviceList(addressList)
                    .setUserModel(userModel)
                    .setBleStateInterface(bleStateInterface)
                    .build();
        }
        if (builder1 != null) {
            builder1.setUserModel(userModel);
            ppScale.setBuilder(builder1);
        }

        //启动扫描
        startScanData();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ppScale != null) {
            ppScale.stopSearch();
        }

    }

    /**
     * 解析数据回调
     * <p>
     * bodyFatModel 身体数据
     * deviceModel 设备信息
     */
    private ProtocalFilterImpl getProtocalFilter() {
        final ProtocalFilterImpl protocalFilter = new ProtocalFilterImpl();
        protocalFilter.setFoodScaleDataProtocoInterface(new FoodScaleDataProtocoInterface() {
            @Override
            public void processData(LFFoodScaleGeneral foodScaleGeneral, PPDeviceModel deviceModel) {
                textView.setText("过程数据");
                extracted(foodScaleGeneral, deviceModel);
            }

            @Override
            public void lockedData(LFFoodScaleGeneral foodScaleGeneral, PPDeviceModel deviceModel) {
                textView.setText("锁定数据");
                extracted(foodScaleGeneral, deviceModel);
            }

            @Override
            public void historyData(LFFoodScaleGeneral foodScaleGeneral, PPDeviceModel deviceModel, String yyyyMMdd, boolean isEnd) {
                if (foodScaleGeneral != null && !isEnd) {
                    String valueStr = getValue(foodScaleGeneral, deviceModel);
                    historyDataText.append(valueStr + "\n");
                } else {
                    historyDataText.append("history end");
                }

            }
        });
        return protocalFilter;
    }

    PPBleStateInterface bleStateInterface = new PPBleStateInterface() {
        @Override
        public void monitorBluetoothWorkState(PPBleWorkState ppBleWorkState, PPDeviceModel deviceModel) {
            if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnected) {
                Logger.d(getString(R.string.device_connected));
                weightTextView.setText(getString(R.string.device_connected));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateConnecting) {
                Logger.d(getString(R.string.device_connecting));
                weightTextView.setText(getString(R.string.device_connecting));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateDisconnected) {
                Logger.d(getString(R.string.device_disconnected));
                weightTextView.setText(getString(R.string.device_disconnected));
            } else if (ppBleWorkState == PPBleWorkState.PPBleStateSearchCanceled) {
                Logger.d(getString(R.string.stop_scanning));
                weightTextView.setText(getString(R.string.stop_scanning));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkSearchTimeOut) {
                Logger.d(getString(R.string.scan_timeout));
                weightTextView.setText(getString(R.string.scan_timeout));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateSearching) {
                Logger.d(getString(R.string.scanning));
                weightTextView.setText(getString(R.string.scanning));
            } else if (ppBleWorkState == PPBleWorkState.PPBleWorkStateWritable) {
                Logger.d(getString(R.string.writable));
                //可写状态，可以发送指令，例如切换单位，获取历史数据等
                weightTextView.setText(getString(R.string.writable));
                //切换单位
//                ppScale.sendUnitDataScale(unitType, null);
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
                Toast.makeText(FoodSclaeDeviceActivity.this, getString(R.string.system_bluetooth_disconnect), Toast.LENGTH_SHORT).show();
            } else if (ppBleSwitchState == PPBleSwitchState.PPBleSwitchStateOn) {
                delayScan();
                Logger.d(getString(R.string.system_blutooth_on));
                Toast.makeText(FoodSclaeDeviceActivity.this, getString(R.string.system_blutooth_on), Toast.LENGTH_SHORT).show();
            } else {
                Logger.e(getString(R.string.system_bluetooth_abnormal));
            }
        }
    };

    /**
     * 延迟开始搜索
     */
    public void delayScan() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!FoodSclaeDeviceActivity.this.isDestroyed() && !FoodSclaeDeviceActivity.this.isFinishing()) {
                    startScanData();
                }
            }
        }, 1000);
    }

    private void extracted(final LFFoodScaleGeneral foodScaleGeneral, final PPDeviceModel deviceModel) {
        //                String weightStr = PPUtil.getWeight(userModel.unit, foodScaleGeneral.getLfWeightKg(), foodScaleGeneral.getThanZero());
//                weightTextView.setText(weightStr);
//        DeviceType.setDeviceType(DeviceUtil.getDeviceType(deviceModel.getDeviceName()));

        weightTextView.setText(getValue(foodScaleGeneral, deviceModel));
//        DataUtil.util().setFoodScaleGeneral(foodScaleGeneral);
//                dismissSelf();
    }

    private String getValue(final LFFoodScaleGeneral foodScaleGeneral, PPDeviceModel deviceModel) {
        String valueStr = "";

        float value = (float) foodScaleGeneral.getLfWeightKg();
        if (foodScaleGeneral.getThanZero() == 0) {
            value *= -1;
        }

        PPUnitType type = foodScaleGeneral.getUnit();

        if (deviceModel.deviceAccuracyType == PPScaleDefine.PPDeviceAccuracyType.PPDeviceAccuracyTypePoint01G) {
//            String num = String.valueOf(value);
            EnergyUnit unit = Energy.toG(value, type);
            String num = unit.format01();
            String unitText = UnitUtil.INSTANCE.unitText(FoodSclaeDeviceActivity.this, type);
            valueStr = num + unitText;
        } else {
            EnergyUnit unit = Energy.toG(value, type);

            if (unit instanceof EnergyUnitLbOz) {
                String split = ":";
                String[] values = unit.format().split(split);
                String unitText = UnitUtil.INSTANCE.unitText(FoodSclaeDeviceActivity.this, type);
                String[] units = unitText.split(split);
                valueStr = values[0] + split + values[1] + units[0] + split + units[1];
            } else {
                String num = unit.format();
                String unitText = UnitUtil.INSTANCE.unitText(FoodSclaeDeviceActivity.this, type);
                valueStr = num + unitText;
            }
        }
        return valueStr;
    }

    private void dismissSelf() {
        ppScale.stopSearch();
//        ppScale.disConnect();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissSelf();
    }


}


