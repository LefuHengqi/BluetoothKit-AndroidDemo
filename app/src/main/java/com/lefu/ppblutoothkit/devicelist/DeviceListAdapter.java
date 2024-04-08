package com.lefu.ppblutoothkit.devicelist;

import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lefu.ppblutoothkit.R;
import com.peng.ppscale.vo.PPDeviceModel;
import com.peng.ppscale.vo.PPScaleDefine;

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
            TextView device_Weight = holder.getView(R.id.device_Weight);
            nameText.setText(deviceModel.getDeviceName());
            macText.setText(deviceModel.getDeviceMac());
            device_rssi.setText(String.format(Locale.getDefault(), "RSSI: %d dBm", deviceModel.getRssi()));
            device_type.setText(deviceModel.getDevicePeripheralType().toString());
        }

    }

    public boolean isFuncTypeWifi(PPDeviceModel device) {
        if (device != null) {
            return (device.deviceFuncType & PPScaleDefine.PPDeviceFuncType.PPDeviceFuncTypeWifi.getType())
                    == PPScaleDefine.PPDeviceFuncType.PPDeviceFuncTypeWifi.getType();
        } else {
            return false;
        }
    }

}
