package com.lefu.ppblutoothkit.device.apple;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.lefu.ppblutoothkit.R;
import com.lefu.ppscale.db.dao.DBManager;
import com.lefu.ppscale.db.dao.DeviceModel;
import com.lefu.ppblutoothkit.device.apple.data.WifiDataBean;
import com.lefu.ppblutoothkit.device.apple.util.WifiUtil;
import com.lefu.ppbase.vo.PPUnitType;
import com.lefu.ppbase.util.PPUtil;
import com.lefu.ppbase.PPScaleDefine;

import java.util.List;

public class WifiDataListAdapter extends ArrayAdapter {

    public WifiDataListAdapter(@NonNull Context context, int resource, List<WifiDataVo.Data> objects) {
        super(context, resource, objects);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WifiDataVo.Data dataVo = (WifiDataVo.Data) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.list_view_data_item, null);
        TextView weightKgText = (TextView) view.findViewById(R.id.weightKgText);
        TextView weichtTime = (TextView) view.findViewById(R.id.weichtTime);

        WifiDataBean wifiDataBean = JSON.parseObject(dataVo.getWeightJson(), WifiDataBean.class);

        DeviceModel device = DBManager.manager().getDevice(wifiDataBean.getMac());
        int accuracyType = PPScaleDefine.PPDeviceAccuracyType.PPDeviceAccuracyTypePoint005.getType();
        if (device != null) {
            accuracyType = device.getAccuracyType();
        }
        weightKgText.setText(PPUtil.getWeight(PPUnitType.Unit_KG, wifiDataBean.getWeight(), accuracyType));

        try {
            String dateTime = WifiUtil.formatDate2(Long.parseLong(wifiDataBean.getTimestamp()));
            weichtTime.setText(dateTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }
}
