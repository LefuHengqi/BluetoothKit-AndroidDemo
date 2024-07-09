package com.lefu.ppblutoothkit.okhttp;

import android.text.TextUtils;


import com.alibaba.fastjson.JSON;
import com.lefu.ppbase.util.Logger;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;

import okhttp3.Response;

public abstract class RetCallBack<T> extends Callback<T> {
    private Class<T> tClass;

    public RetCallBack(Class<T> clazz) {
        this.tClass = clazz;
    }

    @Override
    public T parseNetworkResponse(Response response, int id) throws IOException {
        if (response == null || !response.isSuccessful() || response.body() == null) {
            return null;
        }

        String result = response.body().string();
        if (TextUtils.isEmpty(result)) {
            return null;
        }
        Logger.d("RetCallBack " + tClass.getName() + "  result = " + result);
        T objParsed;
        try {
            objParsed = JSON.parseObject(result, tClass);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        return objParsed;
    }
}