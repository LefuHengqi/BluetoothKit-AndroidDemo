package com.lefu.ppscale.wifi.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.lefu.ppscale.wifi.R;
import com.lefu.ppscale.wifi.data.BodyFataDataModel;
import com.lefu.ppscale.wifi.data.WifiDataBean;
import com.lefu.ppscale.wifi.util.DataUtil;
import com.peng.ppscale.business.device.DeviceManager;
import com.peng.ppscale.business.device.PPUnitType;
import com.peng.ppscale.vo.PPBodyFatModel;
import com.peng.ppscale.vo.PPDeviceModel;
import com.peng.ppscale.vo.PPUserModel;

public class BodyDataDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_data_detail);
        TextView textView = findViewById(R.id.data_detail);

//        BodyFataDataModel bodyFataDataModel = (BodyFataDataModel) getIntent().getSerializableExtra("bodyFataDataModel");
        WifiDataBean wifiDataBean = (WifiDataBean) getIntent().getSerializableExtra("wifiDataBean");

        PPUserModel userModel = DataUtil.util().getUserModel();

        PPDeviceModel deviceModel = new PPDeviceModel(wifiDataBean.getMac(), DeviceManager.HEALTH_SCALE6);

        if (wifiDataBean != null) {
            BodyFataDataModel bodyFataDataModel = new BodyFataDataModel(wifiDataBean.getWeight(),
                    wifiDataBean.getImpedance(),
                    userModel,
                    deviceModel
                    , PPUnitType.Unit_KG);

            textView.setText(bodyFataDataModel.toString());
        } else {
            PPBodyFatModel bodyData = DataUtil.util().getBodyDataModel();
            if (bodyData != null && !TextUtils.isEmpty(bodyData.toString())) {
                textView.setText(bodyData.toString());
            }
        }


    }
}
