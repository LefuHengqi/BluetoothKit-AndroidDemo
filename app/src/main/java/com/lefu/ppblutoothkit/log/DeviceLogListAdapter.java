package com.lefu.ppblutoothkit.log;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lefu.ppblutoothkit.R;

import java.io.File;

public class DeviceLogListAdapter extends BaseQuickAdapter<File, BaseViewHolder> {

    public DeviceLogListAdapter() {
        super(R.layout.log_item);
    }

    @Override
    protected void convert(final BaseViewHolder holder, File deviceVo) {
        String fileName = deviceVo.getName();
        TextView nameText = holder.getView(R.id.device_name);
        nameText.setText(fileName);
    }

}
