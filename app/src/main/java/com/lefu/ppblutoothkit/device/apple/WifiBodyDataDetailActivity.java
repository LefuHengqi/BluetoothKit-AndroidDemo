package com.lefu.ppblutoothkit.device.apple;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.lefu.ppblutoothkit.BaseImmersivePermissionActivity;

import com.lefu.ppblutoothkit.R;
import com.lefu.ppblutoothkit.device.apple.data.BodyFataDataModel;
import com.lefu.ppblutoothkit.device.apple.data.WifiDataBean;
import com.peng.ppscale.business.device.DeviceManager;
import com.lefu.ppbase.vo.PPUnitType;
import com.lefu.ppbase.PPBodyBaseModel;
import com.lefu.ppbase.PPDeviceModel;
import com.lefu.ppbase.vo.PPUserModel;

public class WifiBodyDataDetailActivity extends BaseImmersivePermissionActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_data_detail);
        
        // 在 setContentView 之后调用沉浸式设置
        setupImmersiveMode();
        
        // 初始化Toolbar
        initToolbar();
        
        initView();
    }
    
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setupUnifiedToolbar(
                toolbar,
                "WiFi体重数据详情",
                true
            );
        }
    }

    private void initView() {
        TextView textView = findViewById(R.id.data_detail);

//        BodyFataDataModel bodyFataDataModel = (BodyFataDataModel) getIntent().getSerializableExtra("bodyFataDataModel");
        WifiDataBean wifiDataBean = (WifiDataBean) getIntent().getSerializableExtra("wifiDataBean");

        PPUserModel userModel = (PPUserModel) getIntent().getSerializableExtra("userinfo");

//        PPUserModel userModel = DataUtil.util().getUserModel();

        if (wifiDataBean != null) {
            PPDeviceModel deviceModel = new PPDeviceModel(wifiDataBean.getMac(), DeviceManager.HEALTH_SCALE6);

            PPBodyBaseModel bodyBaseModel = new PPBodyBaseModel();
            bodyBaseModel.impedance = wifiDataBean.getImpedance();
            bodyBaseModel.deviceModel = deviceModel;
            bodyBaseModel.userModel = userModel;
            bodyBaseModel.unit = PPUnitType.Unit_KG;
            bodyBaseModel.weight = (int) (wifiDataBean.getWeight() * 100);

            BodyFataDataModel bodyFataDataModel = new BodyFataDataModel(bodyBaseModel);

            textView.setText(bodyFataDataModel.toString());
        }
    }
}
