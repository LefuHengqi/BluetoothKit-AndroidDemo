package com.example.pengsiyuan.ppbluetoothle.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.pengsiyuan.ppbluetoothle.R;
import com.example.pengsiyuan.ppbluetoothle.util.PPUtil;
import com.peng.ppscale.business.ble.BleOptions;
import com.peng.ppscale.business.ble.PPScale;
import com.peng.ppscale.business.device.PPUnitType;
import com.peng.ppscale.business.ble.bmdj.PPBMDJDataInterface;
import com.peng.ppscale.business.ble.listener.ProtocalFilterImpl;

public class BMDJTimeActivity extends AppCompatActivity {

    private PPScale ppScale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmdjtime);
        final TextView textView = findViewById(R.id.timetextview);

        ProtocalFilterImpl protocalFilter = new ProtocalFilterImpl();

        protocalFilter.setBmdjDataInterface(new PPBMDJDataInterface() {
            @Override
            public void monitorBMDJStandTime(int standTime) {
                double second = (double) (standTime / 10.0);
                textView.setText(PPUtil.keep1Point3(second) + "");

            }

            @Override
            public void monitorBMDJMeasureEnd(int standTime) {
                double second = (double) (standTime / 10.0);
                textView.setText(PPUtil.keep1Point3(second) + "");
                ppScale.exitBMDJModel();
            }
        });

        BleOptions bleOptions = new BleOptions.Builder()
                .setFeaturesFlag(BleOptions.ScaleFeatures.FEATURES_BMDJ)
                .setUnitType(PPUnitType.Unit_KG)
                .build();

        ppScale = new PPScale.Builder(this)
                .setProtocalFilterImpl(protocalFilter)
                .setBleOptions(bleOptions)
//                .setBleStateInterface(bleStateInterface)
                .build();

    }

}
