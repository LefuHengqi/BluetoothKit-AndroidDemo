package com.lefu.ppblutoothkit.devicelist;

import android.content.Context;

import androidx.annotation.NonNull;

import android.hardware.display.DeviceProductInfo;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.lefu.ppblutoothkit.R;
import com.lefu.ppscale.db.dao.DeviceModel;
import com.peng.ppscale.vo.PPDeviceModel;
import com.peng.ppscale.vo.PPScaleDefine;

import java.util.List;
import java.util.Locale;

public class DeviceListAdapter extends ArrayAdapter {

    private final int resourceId;
    OnItemClickViewInsideListener onItemClickViewInsideListener;

    public DeviceListAdapter(@NonNull Context context, int resource, List<PPDeviceModel> objects) {
        super(context, resource, objects);
        this.resourceId = resource;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        PPDeviceModel deviceModel = (PPDeviceModel) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TextView nameText = (TextView) view.findViewById(R.id.device_name);
        TextView macText = (TextView) view.findViewById(R.id.device_mac);
        TextView device_rssi = (TextView) view.findViewById(R.id.device_rssi);
        TextView device_type = (TextView) view.findViewById(R.id.device_type);
        nameText.setText(deviceModel.getDeviceName());
        macText.setText(deviceModel.getDeviceMac());
        device_rssi.setText(String.format(Locale.getDefault(), "RSSI: %d dBm", deviceModel.getRssi()));
        device_type.setText(deviceModel.getDevicePeripheralType().toString());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickViewInsideListener != null) {
                    onItemClickViewInsideListener.onItemClickViewInside(position, v);
                }
            }
        });
        return view;
    }

    public boolean isFuncTypeWifi(PPDeviceModel device) {
        if (device != null) {
            return (device.deviceFuncType & PPScaleDefine.PPDeviceFuncType.PPDeviceFuncTypeWifi.getType())
                    == PPScaleDefine.PPDeviceFuncType.PPDeviceFuncTypeWifi.getType();
        } else {
            return false;
        }
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
