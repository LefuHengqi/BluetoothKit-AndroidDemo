package com.lefu.ppscale.wifi.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.lefu.ppscale.wifi.DBManager;
import com.lefu.ppscale.wifi.R;
import com.lefu.ppscale.wifi.SettingManager;
import com.lefu.ppscale.wifi.adapter.DeviceListAdapter;
import com.lefu.ppscale.wifi.develop.DeveloperActivity;
import com.lefu.ppscale.wifi.model.DeviceModel;
import com.lefu.ppscale.wifi.net.okhttp.DataTask;
import com.lefu.ppscale.wifi.net.okhttp.NetUtil;
import com.lefu.ppscale.wifi.net.okhttp.RetCallBack;
import com.lefu.ppscale.wifi.net.okhttp.vo.SaveWifiGroupBean;
import com.peng.ppscale.business.device.PPDeviceType;
import com.peng.ppscale.vo.PPScaleDefine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class DeviceListActivity extends AppCompatActivity {

    private DeviceListAdapter adapter;
    private List<DeviceModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        list = DBManager.manager().getDeviceList();
        adapter = new DeviceListAdapter(DeviceListActivity.this, R.layout.list_view_device, list);
        ListView listView = (ListView) findViewById(R.id.list_View);
        adapter.setOnClickInItemLisenter(new DeviceListAdapter.OnItemClickViewInsideListener() {
            @Override
            public void onItemClickViewInside(int position, View v) {
                DeviceModel deviceModel = (DeviceModel) adapter.getItem(position);
                if (deviceModel.getDeviceType() == PPScaleDefine.PPDeviceType.PPDeviceTypeCC.getType()) {
                    Intent intent = new Intent(DeviceListActivity.this, DeveloperActivity.class);
                    intent.putExtra(DeveloperActivity.ADDRESS, deviceModel.getDeviceMac());
                    startActivity(intent);
                }
            }
        });
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DeviceListActivity.this);
                builder.setMessage(R.string.corfirm_delete);
                builder.setTitle(R.string.prompt);
                builder.setPositiveButton(R.string.determine, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (list != null) {
                            DeviceModel deviceModel = list.get(position);
                            DBManager.manager().deleteDevice(deviceModel);
                            list.remove(position);
                            adapter.notifyDataSetChanged();

                            clearDevice(deviceModel);
                        }

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
                DeviceModel deviceModel = (DeviceModel) adapter.getItem(position);
                switch (view.getId()) {
                    case R.id.tvSetting:
                        if (deviceModel.getDeviceType() == PPScaleDefine.PPDeviceType.PPDeviceTypeCC.getType()) {
                            Intent intent = new Intent(DeviceListActivity.this, DeveloperActivity.class);
                            intent.putExtra(DeveloperActivity.ADDRESS, deviceModel.getDeviceMac());
                            startActivity(intent);
                        }
                        break;
                    default:
                        if (deviceModel.getDeviceType() == PPScaleDefine.PPDeviceType.PPDeviceTypeCC.getType()) {
                            Intent intent = new Intent(DeviceListActivity.this, BleConfigWifiActivity.class);
                            intent.putExtra("address", deviceModel.getDeviceMac());
                            startActivity(intent);
                        }
                        break;
                }


            }
        });
    }


    public void clearDevice(DeviceModel deviceModel) {

        if (deviceModel != null) {
            String sn = deviceModel.getSn();
            if (!TextUtils.isEmpty(sn)) {
                Map<String, String> map = new HashMap<>();
                map.put("sn", sn);
                map.put("uid", SettingManager.get().getUid());

                DataTask.get(NetUtil.CLEAR_DEVICE_DATA, map, new RetCallBack<SaveWifiGroupBean>(SaveWifiGroupBean.class) {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(DeviceListActivity.this, R.string.clear_fail, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(SaveWifiGroupBean response, int id) {
                        if (response.isStatus()) {
                            Toast.makeText(DeviceListActivity.this, R.string.clear_success, Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            String content = TextUtils.isEmpty(response.getMsg()) ? getString(R.string.clear_fail) : response.getMsg();
                            Toast.makeText(DeviceListActivity.this, content, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        list = DBManager.manager().getDeviceList();
        if (adapter != null) {
            adapter.clear();
            adapter.addAll(list);
//            adapter.notifyDataSetChanged();
        }
    }
}
