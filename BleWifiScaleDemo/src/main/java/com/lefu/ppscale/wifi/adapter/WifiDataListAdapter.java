package com.lefu.ppscale.wifi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.lefu.ppscale.wifi.R;
import com.lefu.ppscale.wifi.data.WifiDataBean;
import com.lefu.ppscale.wifi.data.WifiDataVo;
import com.lefu.ppscale.wifi.util.PPUtil;
import com.peng.ppscale.business.device.PPUnitType;

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

        weightKgText.setText(PPUtil.getWeight(PPUnitType.Unit_KG, wifiDataBean.getWeight(), wifiDataBean.getType()));

        try {
            String dateTime = PPUtil.formatDate2(Long.parseLong(wifiDataBean.getTimestamp()));
            weichtTime.setText(dateTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }
}
