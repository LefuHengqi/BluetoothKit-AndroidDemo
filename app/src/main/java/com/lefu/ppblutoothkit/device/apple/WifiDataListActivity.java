package com.lefu.ppblutoothkit.device.apple;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.lefu.base.SettingManager;
import com.lefu.ppblutoothkit.R;
import com.lefu.ppblutoothkit.device.apple.data.WifiDataBean;
import com.lefu.ppblutoothkit.okhttp.DataTask;
import com.lefu.ppblutoothkit.okhttp.NetUtil;
import com.lefu.ppblutoothkit.okhttp.RetCallBack;
import com.peng.ppscale.vo.PPUserModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class WifiDataListActivity extends AppCompatActivity {

    private ListView listView;
    private WifiDataListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_data_list);

        adapter = new WifiDataListAdapter(WifiDataListActivity.this, R.layout.list_view_device, new ArrayList<WifiDataVo.Data>());
        listView = (ListView) findViewById(R.id.list_View);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                WifiDataVo.Data dataVo = (WifiDataVo.Data) parent.getAdapter().getItem(position);

                WifiDataBean wifiDataBean = JSON.parseObject(dataVo.getWeightJson(), WifiDataBean.class);

                PPUserModel userModel = (PPUserModel) getIntent().getSerializableExtra("userinfo");

                Intent intent = new Intent(WifiDataListActivity.this, WifiBodyDataDetailActivity.class);
                intent.putExtra("wifiDataBean", wifiDataBean);
                intent.putExtra("userinfo", userModel);
                startActivity(intent);

            }
        });
        initData();
    }

    private void initData() {
        Map<String, String> map = new HashMap<>();
        map.put("uid", SettingManager.get().getUid());
        DataTask.get(NetUtil.GET_SCALE_WEIGHTS, map, new RetCallBack<WifiDataVo>(WifiDataVo.class) {

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(WifiDataVo response, int id) {
                if (response.isStatus()) {
                    List<WifiDataVo.Data> data = response.getData();
                    adapter.addAll(data == null ? new ArrayList<WifiDataVo.Data>() : data);
                }
            }
        });

    }

}
