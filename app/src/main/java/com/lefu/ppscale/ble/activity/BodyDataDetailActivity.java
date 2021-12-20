package com.lefu.ppscale.ble.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.lefu.ppscale.ble.R;
import com.lefu.ppscale.ble.model.DataUtil;
import com.peng.ppscale.vo.PPBodyFatModel;
import com.peng.ppscale.vo.PPDeviceModel;
import com.ppscale.data_range.vo.BodyItem;
import com.ppscale.data_range.vo.PPBodyFatDetailModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BodyDataDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_data_detail);
        TextView textView = findViewById(R.id.data_detail);
        PPBodyFatModel bodyData = DataUtil.util().getBodyDataModel();
        if (bodyData != null) {
            textView.setText(bodyData.toString());
            int accuracyType = 1;
            PPDeviceModel deviceModel = DataUtil.util().getDeviceModel();
            if (deviceModel != null) {
                accuracyType = deviceModel.deviceAccuracyType.getType();
            }

            PPBodyFatDetailModel ppBodyFatDetailModel = new PPBodyFatDetailModel(this, bodyData, DataUtil.util().getUnit(), accuracyType);

            Map<String, BodyItem> bodyItems = ppBodyFatDetailModel.getBodyItems();

            if (bodyItems != null && !bodyItems.isEmpty()) {
                List<BodyItem> bodyItemList = new ArrayList<>(bodyItems.values());
                for (BodyItem bodyItem : bodyItemList) {
//                    textView.append(bodyItem.toString() + "\n");
                }
            }

        }
    }
}
