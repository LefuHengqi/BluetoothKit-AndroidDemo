package com.lefu.ppblutoothkit.okhttp;

import com.zhy.http.okhttp.OkHttpUtils;

import java.util.Map;

public class DataTask {

    public static void post(String url, Map<String, String> parameter, RetCallBack callBack) {
        OkHttpUtils.post().url(url).params(parameter).tag(url).build().execute(callBack);
    }

    public static void get(String url, Map<String, String> parameter, RetCallBack callBack) {
        OkHttpUtils.get().url(url).params(parameter).tag(url).build().execute(callBack);
    }




}
