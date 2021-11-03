package com.lefu.ppscale.ble.adapter;

import android.content.Context;

import androidx.annotation.NonNull;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.lefu.ppscale.ble.R;
import com.lefu.ppscale.db.dao.DeviceModel;
import com.peng.ppscale.vo.PPScaleDefine;

import java.util.List;

public class DeviceListAdapter extends ArrayAdapter {

    private final int resourceId;
    OnItemClickViewInsideListener onItemClickViewInsideListener;

    public DeviceListAdapter(@NonNull Context context, int resource, List<DeviceModel> objects) {
        super(context, resource, objects);
        this.resourceId = resource;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        DeviceModel deviceModel = (DeviceModel) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TextView nameText = (TextView) view.findViewById(com.lefu.ppscale.wifi.R.id.device_name);
        TextView macText = (TextView) view.findViewById(com.lefu.ppscale.wifi.R.id.device_mac);

        TextView tv_ssid = (TextView) view.findViewById(com.lefu.ppscale.wifi.R.id.device_ssid);
        nameText.setText(deviceModel.getDeviceName());
        macText.setText(deviceModel.getDeviceMac());
        View tvSetting = view.findViewById(com.lefu.ppscale.wifi.R.id.tvSetting);

        if (deviceModel.getDeviceType() == PPScaleDefine.PPDeviceType.PPDeviceTypeCC.getType()) {
            if (!TextUtils.isEmpty(deviceModel.getSsid())) {
                tv_ssid.setText(deviceModel.getSsid());
            } else {
                tv_ssid.setText(com.lefu.ppscale.wifi.R.string.to_config_the_network);
            }
        } else {
            tv_ssid.setVisibility(View.GONE);
            tvSetting.setVisibility(View.GONE);
        }
        tvSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickViewInsideListener != null) {
                    onItemClickViewInsideListener.onItemClickViewInside(position, v);
                }
            }
        });

        return view;
    }

    public void setOnClickInItemLisenter(OnItemClickViewInsideListener onItemClickViewInsideListener) {
        this.onItemClickViewInsideListener = onItemClickViewInsideListener;
    }

    public interface OnItemClickViewInsideListener {
        /**
         * @param position 列表项在列表中的位置
         * @param v        列表项被点击的子视图
         */
        public void onItemClickViewInside(int position, View v);
    }


}
