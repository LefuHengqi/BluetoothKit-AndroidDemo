package com.lefu.ppscale.wifi.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.lefu.ppscale.wifi.R;
import com.lefu.ppscale.wifi.util.DataUtil;
import com.peng.ppscale.vo.PPBodyFatModel;

public class BodyDataDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_data_detail);
        TextView textView = findViewById(R.id.data_detail);
        PPBodyFatModel bodyData = DataUtil.util().getBodyDataModel();
        if (bodyData != null && !TextUtils.isEmpty(bodyData.toString())) {
            textView.setText(bodyData.toString());
        }
    }
}