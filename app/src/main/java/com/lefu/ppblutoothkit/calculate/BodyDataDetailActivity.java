package com.lefu.ppblutoothkit.calculate;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;


import com.lefu.ppblutoothkit.R;
import com.lefu.ppblutoothkit.util.DataUtil;
import com.lefu.ppcalculate.PPBodyFatModel;

public class BodyDataDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_data_detail);
        TextView textView = findViewById(R.id.data_detail);
        PPBodyFatModel bodyData = DataUtil.INSTANCE.getBodyDataModel();
        if (bodyData != null) {
            textView.setText(bodyData.toString());
        }
    }
}
