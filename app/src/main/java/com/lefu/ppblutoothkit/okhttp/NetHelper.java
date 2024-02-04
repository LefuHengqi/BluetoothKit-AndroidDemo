package com.lefu.ppblutoothkit.okhttp;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.lefu.ppscale.db.dao.DeviceModel;

import java.util.HashMap;
import java.util.Map;

public class NetHelper {

    public static void clearDevice(final Context context, DeviceModel deviceModel, String uid) {
        if (deviceModel != null) {
            String sn = deviceModel.getSn();
            if (!TextUtils.isEmpty(sn)) {
                Map<String, String> map = new HashMap<>();
                map.put("sn", sn);
                map.put("uid", uid);

//                DataTask.get(NetUtil.CLEAR_DEVICE_DATA, map, new RetCallBack<SaveWifiGroupBean>(SaveWifiGroupBean.class) {
//
//                    @Override
//                    public void onError(okhttp3.Call call, Exception e, int i) {
//                        Toast.makeText(context, R.string.clear_fail, Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onResponse(SaveWifiGroupBean response, int id) {
//                        if (response.isStatus()) {
//                            Toast.makeText(context, R.string.clear_success, Toast.LENGTH_SHORT).show();
//                        } else {
//                            String content = TextUtils.isEmpty(response.getMsg()) ? context.getString(R.string.clear_fail) : response.getMsg();
//                            Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });

            }
        }
    }

}
