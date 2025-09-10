package com.lefu.ppblutoothkit.device.apple

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.lefu.ppbase.PPBodyBaseModel
import com.lefu.ppbase.PPDeviceModel
import com.lefu.ppbase.vo.PPUnitType
import com.lefu.ppbase.vo.PPUserModel
import com.lefu.ppblutoothkit.BaseImmersivePermissionActivity
import com.lefu.ppblutoothkit.R
import com.lefu.ppblutoothkit.device.apple.data.BodyFataDataModel
import com.lefu.ppblutoothkit.device.apple.data.WifiDataBean
import com.peng.ppscale.business.device.DeviceManager

class WifiBodyDataDetailActivity : BaseImmersivePermissionActivity() {
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_data_detail)

        // 在 setContentView 之后调用沉浸式设置
        setupImmersiveMode()

        // 初始化Toolbar
        initToolbar()

        initView()
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar?>(R.id.toolbar)
        if (toolbar != null) {
            setupUnifiedToolbar(
                toolbar,
                "WiFi体重数据详情",
                true
            )
        }
    }

    private fun initView() {
        val textView = findViewById<TextView>(R.id.data_detail)

        //        BodyFataDataModel bodyFataDataModel = (BodyFataDataModel) getIntent().getSerializableExtra("bodyFataDataModel");
        val wifiDataBean = getIntent().getSerializableExtra("wifiDataBean") as WifiDataBean?

        val userModel = getIntent().getSerializableExtra("userinfo") as PPUserModel?

        //        PPUserModel userModel = DataUtil.util().getUserModel();
        if (wifiDataBean != null) {
            val deviceModel = PPDeviceModel(wifiDataBean.getMac(), DeviceManager.HEALTH_SCALE6)

            val bodyBaseModel = PPBodyBaseModel()
            bodyBaseModel.impedance = wifiDataBean.getImpedance().toLong()
            bodyBaseModel.deviceModel = deviceModel
            bodyBaseModel.userModel = userModel
            bodyBaseModel.unit = PPUnitType.Unit_KG
            bodyBaseModel.weight = (wifiDataBean.getWeight() * 100).toInt()

            val bodyFataDataModel = BodyFataDataModel(bodyBaseModel)

            textView.setText(bodyFataDataModel.toString())
        }
    }
}
