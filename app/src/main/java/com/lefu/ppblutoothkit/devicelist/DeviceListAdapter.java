package com.lefu.ppblutoothkit.devicelist;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lefu.ppblutoothkit.R;
import com.lefu.ppbase.PPDeviceModel;
import com.lefu.ppbase.PPScaleDefine;

import java.util.Locale;

public class DeviceListAdapter extends BaseQuickAdapter<DeviceVo, BaseViewHolder> {

    public DeviceListAdapter() {
        super(R.layout.activity_scan_list_item);
    }

    @Override
    protected void convert(BaseViewHolder holder, DeviceVo deviceVo) {
        PPDeviceModel deviceModel = deviceVo.getDeviceModel();
        if (deviceModel != null) {
            TextView nameText = holder.getView(R.id.device_name);
            TextView macText = holder.getView(R.id.device_mac);
            TextView device_rssi = holder.getView(R.id.device_rssi);
            TextView device_type = holder.getView(R.id.device_type);
            TextView device_AdvLen = holder.getView(R.id.device_AdvLen);
            TextView device_sign = holder.getView(R.id.device_sign);
            nameText.setText("devName:"+deviceModel.getDeviceName());
            macText.setText("Mac:" + deviceModel.getDeviceMac());
            device_AdvLen.setText("advLength: " + deviceModel.getAdvLength());
            device_sign.setText("sign: " + deviceModel.getSign());

            device_rssi.setText(String.format(Locale.getDefault(), "RSSI: %d dBm", deviceModel.getRssi()));
            device_type.setText("PeripheralType:" + deviceModel.getDevicePeripheralType());
        }

    }

    public boolean isFuncTypeWifi(PPDeviceModel device) {
        if (device != null) {
            return (device.getDeviceFuncType() & PPScaleDefine.PPDeviceFuncType.PPDeviceFuncTypeWifi.getType())
                    == PPScaleDefine.PPDeviceFuncType.PPDeviceFuncTypeWifi.getType();
        } else {
            return false;
        }
    }

}
