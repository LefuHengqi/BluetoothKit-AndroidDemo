package com.lefu.ppscale.wifi.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.lefu.ppscale.wifi.R;
import com.lefu.ppscale.wifi.data.BodyFataDataModel;
import com.lefu.ppscale.wifi.data.WifiDataBean;
import com.peng.ppscale.business.device.DeviceManager;
import com.peng.ppscale.business.device.PPUnitType;
import com.peng.ppscale.vo.PPDeviceModel;
import com.peng.ppscale.vo.PPUserModel;

public class WifiBodyDataDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_data_detail);
        TextView textView = findViewById(R.id.data_detail);

//        BodyFataDataModel bodyFataDataModel = (BodyFataDataModel) getIntent().getSerializableExtra("bodyFataDataModel");
        WifiDataBean wifiDataBean = (WifiDataBean) getIntent().getSerializableExtra("wifiDataBean");

        PPUserModel userModel = (PPUserModel) getIntent().getSerializableExtra("userinfo");

//        PPUserModel userModel = DataUtil.util().getUserModel();

        if (wifiDataBean != null) {
            PPDeviceModel deviceModel = new PPDeviceModel(wifiDataBean.getMac(), DeviceManager.HEALTH_SCALE6);

            BodyFataDataModel bodyFataDataModel = new BodyFataDataModel(wifiDataBean.getWeight(),
                    wifiDataBean.getImpedance(),
                    userModel,
                    deviceModel,
                    PPUnitType.Unit_KG);

            textView.setText(bodyFataDataModel.toString());
        }


    }
}
