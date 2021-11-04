package com.lefu.ppscale.wifi.data;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.lefu.ppscale.db.dao.DBManager;
import com.lefu.ppscale.db.dao.DeviceModel;
import com.lefu.ppscale.wifi.R;
import com.lefu.ppscale.wifi.SettingManager;
import com.lefu.ppscale.wifi.activity.WifiBodyDataDetailActivity;
import com.lefu.ppscale.wifi.adapter.WifiDataListAdapter;
import com.lefu.ppscale.wifi.net.okhttp.DataTask;
import com.lefu.ppscale.wifi.net.okhttp.NetUtil;
import com.lefu.ppscale.wifi.net.okhttp.RetCallBack;
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
        final List<DeviceModel> list = DBManager.manager().getDeviceList();

        adapter = new WifiDataListAdapter(WifiDataListActivity.this, R.layout.list_view_device, new ArrayList<WifiDataVo.Data>());
        listView = (ListView) findViewById(R.id.list_View);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WifiDataListActivity.this);
                builder.setMessage(R.string.corfirm_delete);
                builder.setTitle(R.string.prompt);

                builder.setPositiveButton(R.string.determine, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DBManager.manager().deleteDevice(list.get(position));
                        list.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });

                builder.setNegativeButton(R.string.cancel, null);
                builder.show();
                return true;
            }

        });
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
        map.put("uid", SettingManager.get(this).getUid());

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
