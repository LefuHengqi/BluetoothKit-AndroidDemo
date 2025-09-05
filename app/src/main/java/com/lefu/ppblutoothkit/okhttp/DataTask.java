package com.lefu.ppblutoothkit.okhttp;

import android.util.Log;
import com.lefu.ppblutoothkit.util.LogUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import okhttp3.Call;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Callback;
import java.io.IOException;

public class DataTask {

    private static final String TAG = "DataTask";

    public static void post(String url, Map<String, String> parameter, RetCallBack callBack) {
        // 记录网络请求开始日志
        long startTime = System.currentTimeMillis();
        LogUtils.INSTANCE.writeNetworkLog("POST请求开始", "URL: " + url, "参数: " + parameter + ", 时间: " + startTime);
        
        // 创建包装的回调，添加日志记录功能
        RetCallBack wrappedCallback = new RetCallBack(Object.class) {
            @Override
            public void onError(Call call, Exception e, int id) {
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                // 记录网络请求失败日志
                LogUtils.INSTANCE.writeNetworkLog("POST请求失败", "URL: " + url, "错误: " + e.getMessage() + ", 耗时: " + duration + "ms");
                callBack.onError(call, e, id);
            }

            @Override
            public void onResponse(Object response, int id) {
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                // 记录网络请求成功日志
                LogUtils.INSTANCE.writeNetworkLog("POST请求成功", "URL: " + url, "响应: " + response + ", 耗时: " + duration + "ms");
                callBack.onResponse(response, id);
            }
        };
        
        // 添加超时日志监控
        OkHttpUtils.post().url(url).params(parameter).tag(url).build().execute(wrappedCallback);
    }

    public static void get(String url, Map<String, String> parameter, RetCallBack callBack) {
        // 记录网络请求开始日志
        long startTime = System.currentTimeMillis();
        // 创建自定义OkHttpClient，针对大数据传输优化
        OkHttpClient customClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)  // 连接超时30秒
            .readTimeout(120, TimeUnit.SECONDS)    // 读取超时120秒，适应大数据传输
            .writeTimeout(60, TimeUnit.SECONDS)    // 写入超时60秒
            .retryOnConnectionFailure(true)        // 连接失败时重试
            .build();
        // 构建完整的URL（包含参数）
        StringBuilder fullUrl = new StringBuilder(url);
        if (parameter != null && !parameter.isEmpty()) {
            fullUrl.append(url.contains("?") ? "&" : "?");
            boolean first = true;
            for (Map.Entry<String, String> entry : parameter.entrySet()) {
                if (!first) {
                    fullUrl.append("&");
                }
                fullUrl.append(entry.getKey()).append("=").append(entry.getValue());
                first = false;
            }
        }
        
        // 创建请求
        Request request = new Request.Builder()
            .url(fullUrl.toString())
            .get()
            .build();
        // 执行异步请求
        customClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                LogUtils.INSTANCE.writeNetworkLog("GET请求失败", "URL: " + url, "错误: " + e.getMessage() + ", 耗时: " + duration + "ms");
                // 在主线程中调用回调
                android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onError(call, e, 0);
                    }
                });
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                
                try {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        int responseSize = responseBody.length();
                        LogUtils.INSTANCE.writeNetworkLog("GET请求成功", "URL: " + url, "响应大小: " + responseSize + " 字符, 耗时: " + duration + "ms");
                        
                        // 在主线程中调用回调
                        android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onResponse(responseBody, 0);
                            }
                        });
                    } else {
                        String errorMsg = "HTTP错误: " + response.code() + " " + response.message();
                        LogUtils.INSTANCE.writeNetworkLog("GET请求HTTP错误", "URL: " + url, errorMsg + ", 耗时: " + duration + "ms");
                        
                        // 在主线程中调用回调
                        android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onError(call, new Exception(errorMsg), 0);
                            }
                        });
                    }
                } catch (Exception e) {
                    LogUtils.INSTANCE.writeNetworkLog("GET请求处理响应异常", "URL: " + url, "错误: " + e.getMessage() + ", 耗时: " + duration + "ms");
                    
                    // 在主线程中调用回调
                    android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onError(call, e, 0);
                        }
                    });
                } finally {
                    if (response.body() != null) {
                        response.body().close();
                    }
                }
            }
        });
        
    }
}
