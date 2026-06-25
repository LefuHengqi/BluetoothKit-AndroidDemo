package com.lefu.bluetooth.library;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;

import com.lefu.bluetooth.library.advertising.AdvertisingResponse;
import com.lefu.bluetooth.library.connect.BleConnectManager;
import com.lefu.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.lefu.bluetooth.library.connect.options.BleConnectOptions;
import com.lefu.bluetooth.library.connect.response.BleConnectResponse;
import com.lefu.bluetooth.library.connect.response.BleGeneralResponse;
import com.lefu.bluetooth.library.connect.response.BleMtuResponse;
import com.lefu.bluetooth.library.connect.response.BleNotifyResponse;
import com.lefu.bluetooth.library.connect.response.BleReadResponse;
import com.lefu.bluetooth.library.connect.response.BleReadRssiResponse;
import com.lefu.bluetooth.library.connect.response.BleUnnotifyResponse;
import com.lefu.bluetooth.library.connect.response.BleWriteResponse;
import com.lefu.bluetooth.library.connect.response.BluetoothResponse;
import com.lefu.bluetooth.library.model.BleGattProfile;
import com.lefu.bluetooth.library.receiver.BluetoothReceiver;
import com.lefu.bluetooth.library.receiver.listener.BleCharacterChangeListener;
import com.lefu.bluetooth.library.receiver.listener.BleConnectStatusChangeListener;
import com.lefu.bluetooth.library.receiver.listener.BluetoothBondListener;
import com.lefu.bluetooth.library.receiver.listener.BluetoothBondStateChangeListener;
import com.lefu.bluetooth.library.receiver.listener.BluetoothStateChangeListener;
import com.lefu.bluetooth.library.connect.listener.BluetoothStateListener;
import com.lefu.bluetooth.library.search.SearchRequest;
import com.lefu.bluetooth.library.search.SearchResult;
import com.lefu.bluetooth.library.search.response.SearchResponse;
import com.lefu.bluetooth.library.utils.BluetoothLog;
import com.lefu.bluetooth.library.utils.BluetoothUtils;
import com.lefu.bluetooth.library.utils.ListUtils;
import com.lefu.bluetooth.library.utils.proxy.ProxyBulk;
import com.lefu.bluetooth.library.utils.proxy.ProxyInterceptor;
import com.lefu.bluetooth.library.utils.proxy.ProxyUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import static com.lefu.bluetooth.library.Constants.CODE_CLEAR_REQUEST;
import static com.lefu.bluetooth.library.Constants.CODE_CONNECT;
import static com.lefu.bluetooth.library.Constants.CODE_DISCONNECT;
import static com.lefu.bluetooth.library.Constants.CODE_INDICATE;
import static com.lefu.bluetooth.library.Constants.CODE_NOTIFY;
import static com.lefu.bluetooth.library.Constants.CODE_READ;
import static com.lefu.bluetooth.library.Constants.CODE_READ_DESCRIPTOR;
import static com.lefu.bluetooth.library.Constants.CODE_READ_RSSI;
import static com.lefu.bluetooth.library.Constants.CODE_REFRESH_CACHE;
import static com.lefu.bluetooth.library.Constants.CODE_SEARCH;
import static com.lefu.bluetooth.library.Constants.CODE_STOP_SESARCH;
import static com.lefu.bluetooth.library.Constants.CODE_UNNOTIFY;
import static com.lefu.bluetooth.library.Constants.CODE_WRITE;
import static com.lefu.bluetooth.library.Constants.CODE_WRITE_DESCRIPTOR;
import static com.lefu.bluetooth.library.Constants.CODE_WRITE_NORSP;
import static com.lefu.bluetooth.library.Constants.DEVICE_FOUND;
import static com.lefu.bluetooth.library.Constants.EXTRA_BYTE_VALUE;
import static com.lefu.bluetooth.library.Constants.EXTRA_CHARACTER_UUID;
import static com.lefu.bluetooth.library.Constants.EXTRA_DESCRIPTOR_UUID;
import static com.lefu.bluetooth.library.Constants.EXTRA_GATT_PROFILE;
import static com.lefu.bluetooth.library.Constants.EXTRA_MAC;
import static com.lefu.bluetooth.library.Constants.EXTRA_MTU;
import static com.lefu.bluetooth.library.Constants.EXTRA_OPTIONS;
import static com.lefu.bluetooth.library.Constants.EXTRA_REQUEST;
import static com.lefu.bluetooth.library.Constants.EXTRA_RSSI;
import static com.lefu.bluetooth.library.Constants.EXTRA_SEARCH_RESULT;
import static com.lefu.bluetooth.library.Constants.EXTRA_SERVICE_UUID;
import static com.lefu.bluetooth.library.Constants.EXTRA_TYPE;
import static com.lefu.bluetooth.library.Constants.REQUEST_SUCCESS;
import static com.lefu.bluetooth.library.Constants.SEARCH_CANCEL;
import static com.lefu.bluetooth.library.Constants.SEARCH_FAIL;
import static com.lefu.bluetooth.library.Constants.SEARCH_START;
import static com.lefu.bluetooth.library.Constants.SEARCH_STOP;
import static com.lefu.bluetooth.library.Constants.SERVICE_UNREADY;
import static com.lefu.bluetooth.library.Constants.CODE_REQUEST_MTU;
import static com.lefu.bluetooth.library.Constants.GATT_DEF_BLE_MTU_SIZE;

/**
 * Created by dingjikerbo on 16/4/8.
 */
public class BluetoothClientImpl implements IBluetoothClient, ProxyInterceptor, Callback {

    private static final int MSG_INVOKE_PROXY = 1;
    private static final int MSG_REG_RECEIVER = 2;

    private static final String TAG = BluetoothClientImpl.class.getSimpleName();

    private Context mContext;

    private volatile IBluetoothService mBluetoothService;

    private volatile static IBluetoothClient sInstance;

    private CountDownLatch mCountDownLatch;

    private HandlerThread mWorkerThread;
    private Handler mWorkerHandler;

    private HashMap<String, HashMap<String, List<BleNotifyResponse>>> mNotifyResponses;
    private HashMap<String, List<BleConnectStatusListener>> mConnectStatusListeners;
    private List<BluetoothStateListener> mBluetoothStateListeners;
    private List<BluetoothBondListener> mBluetoothBondListeners;
    private boolean mBluetoothReceiverRegistered;
    private BluetoothLeAdvertiser advertiser;

    int state = 0;//0 停止 1启动
    AdvertisingResponse advertisingResponse;
    private Runnable mAdvertisingTimeoutRunnable;

    // 缓存当前广播数据
    private byte[] mCurrentAdvertisingData;

    private BluetoothClientImpl(Context context) {
        try {
            mContext = context.getApplicationContext();
            BluetoothContext.set(mContext);

            mWorkerThread = new HandlerThread(TAG);
            mWorkerThread.start();

            mWorkerHandler = new Handler(mWorkerThread.getLooper(), this);

            mNotifyResponses = new HashMap<String, HashMap<String, List<BleNotifyResponse>>>();
            mConnectStatusListeners = new HashMap<String, List<BleConnectStatusListener>>();
            mBluetoothStateListeners = new LinkedList<BluetoothStateListener>();
            mBluetoothBondListeners = new LinkedList<BluetoothBondListener>();

            mWorkerHandler.obtainMessage(MSG_REG_RECEIVER).sendToTarget();
        } catch (Exception e) {
            BluetoothLog.e("BluetoothClientImpl 构造函数报错 error");
            BluetoothLog.e(e);
            e.printStackTrace();
        }
//        BluetoothHooker.hook();
    }

    public static IBluetoothClient getInstance(Context context) {
        if (sInstance == null) {
            synchronized (BluetoothClientImpl.class) {
                if (sInstance == null) {
                    try {
                        BluetoothClientImpl client = new BluetoothClientImpl(context);
                        sInstance = ProxyUtils.getProxy(client, IBluetoothClient.class, client);
                    } catch (Exception e) {
                        BluetoothLog.e("BluetoothClientImpl getInstance error");
                        BluetoothLog.e(e);
                        e.printStackTrace();
                    }
                }
            }
        }
        return sInstance;
    }

    private IBluetoothService getBluetoothService() {
//        BluetoothLog.v(String.format("getBluetoothService"));
        if (mBluetoothService == null) {
            bindServiceSync();
        }
        return mBluetoothService;
    }

    private void bindServiceSync() {
        checkRuntime(true);

//        BluetoothLog.v(String.format("bindServiceSync"));

        mCountDownLatch = new CountDownLatch(1);

        Intent intent = new Intent();
        intent.setClass(mContext, BluetoothService.class);

        if (mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE)) {
//            BluetoothLog.v(String.format("BluetoothService registered"));
            waitBluetoothManagerReady();
        } else {
//            BluetoothLog.v(String.format("BluetoothService not registered"));
            mBluetoothService = BluetoothServiceImpl.getInstance();
        }
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
//            BluetoothLog.v(String.format("onServiceConnected"));
            mBluetoothService = IBluetoothService.Stub.asInterface(service);
            notifyBluetoothManagerReady();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
//            BluetoothLog.v(String.format("onServiceDisconnected"));
            mBluetoothService = null;
        }
    };

    @Override
    public void connect(String mac, BleConnectOptions options, final BleConnectResponse response) {
        Bundle args = new Bundle();
        args.putString(EXTRA_MAC, mac);
        args.putParcelable(EXTRA_OPTIONS, options);
        safeCallBluetoothApi(CODE_CONNECT, args, new BluetoothResponse() {
            @Override
            protected void onAsyncResponse(int code, Bundle data) {
                checkRuntime(true);
                if (response != null) {
                    data.setClassLoader(getClass().getClassLoader());
                    BleGattProfile profile = data.getParcelable(EXTRA_GATT_PROFILE);
                    response.onResponse(code, profile);
                }
            }
        });
    }

    @Override
    public void disconnect(String mac) {
        Bundle args = new Bundle();
        args.putString(EXTRA_MAC, mac);
        safeCallBluetoothApi(CODE_DISCONNECT, args, null);
        clearNotifyListener(mac);
    }

    @Override
    public void registerConnectStatusListener(String mac, BleConnectStatusListener listener) {
        checkRuntime(true);
        List<BleConnectStatusListener> listeners = mConnectStatusListeners.get(mac);
        if (listeners == null) {
            listeners = new ArrayList<BleConnectStatusListener>();
            mConnectStatusListeners.put(mac, listeners);
        }
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    @Override
    public void unregisterConnectStatusListener(String mac, BleConnectStatusListener listener) {
        checkRuntime(true);
        List<BleConnectStatusListener> listeners = mConnectStatusListeners.get(mac);
        if (listener != null && !ListUtils.isEmpty(listeners)) {
            listeners.remove(listener);
        }
    }

    @Override
    public void read(String mac, UUID service, UUID character, final BleReadResponse response) {
        Bundle args = new Bundle();
        args.putString(EXTRA_MAC, mac);
        args.putSerializable(EXTRA_SERVICE_UUID, service);
        args.putSerializable(EXTRA_CHARACTER_UUID, character);
        safeCallBluetoothApi(CODE_READ, args, new BluetoothResponse() {
            @Override
            protected void onAsyncResponse(int code, Bundle data) {
                checkRuntime(true);
                if (response != null) {
                    response.onResponse(code, data.getByteArray(EXTRA_BYTE_VALUE));
                }
            }
        });
    }

    @Override
    public void write(String mac, UUID service, UUID character, byte[] value, final BleWriteResponse response) {
        Bundle args = new Bundle();
        args.putString(EXTRA_MAC, mac);
        args.putSerializable(EXTRA_SERVICE_UUID, service);
        args.putSerializable(EXTRA_CHARACTER_UUID, character);
        args.putByteArray(EXTRA_BYTE_VALUE, value);
        safeCallBluetoothApi(CODE_WRITE, args, new BluetoothResponse() {
            @Override
            protected void onAsyncResponse(int code, Bundle data) {
                checkRuntime(true);
                if (response != null) {
                    response.onResponse(code);
                }
            }
        });
    }

    @Override
    public void readDescriptor(String mac, UUID service, UUID character, UUID descriptor, final BleReadResponse response) {
        Bundle args = new Bundle();
        args.putString(EXTRA_MAC, mac);
        args.putSerializable(EXTRA_SERVICE_UUID, service);
        args.putSerializable(EXTRA_CHARACTER_UUID, character);
        args.putSerializable(EXTRA_DESCRIPTOR_UUID, descriptor);
        safeCallBluetoothApi(CODE_READ_DESCRIPTOR, args, new BluetoothResponse() {
            @Override
            protected void onAsyncResponse(int code, Bundle data) {
                checkRuntime(true);
                if (response != null) {
                    response.onResponse(code, data.getByteArray(EXTRA_BYTE_VALUE));
                }
            }
        });
    }

    @Override
    public void writeDescriptor(String mac, UUID service, UUID character, UUID descriptor, byte[] value, final BleWriteResponse response) {
        Bundle args = new Bundle();
        args.putString(EXTRA_MAC, mac);
        args.putSerializable(EXTRA_SERVICE_UUID, service);
        args.putSerializable(EXTRA_CHARACTER_UUID, character);
        args.putSerializable(EXTRA_DESCRIPTOR_UUID, descriptor);
        args.putByteArray(EXTRA_BYTE_VALUE, value);
        safeCallBluetoothApi(CODE_WRITE_DESCRIPTOR, args, new BluetoothResponse() {
            @Override
            protected void onAsyncResponse(int code, Bundle data) {
                checkRuntime(true);
                if (response != null) {
                    response.onResponse(code);
                }
            }
        });
    }

    @Override
    public void writeNoRsp(String mac, UUID service, UUID character, byte[] value, final BleWriteResponse response) {
        Bundle args = new Bundle();
        args.putString(EXTRA_MAC, mac);
        args.putSerializable(EXTRA_SERVICE_UUID, service);
        args.putSerializable(EXTRA_CHARACTER_UUID, character);
        args.putByteArray(EXTRA_BYTE_VALUE, value);
        safeCallBluetoothApi(CODE_WRITE_NORSP, args, new BluetoothResponse() {
            @Override
            protected void onAsyncResponse(int code, Bundle data) {
                checkRuntime(true);
                if (response != null) {
                    response.onResponse(code);
                }
            }
        });
    }

    @Override
    public void writeNoRspDirectly(String mac, UUID service, UUID character, byte[] value, final BleWriteResponse response) {


//        BluetoothGattCharacteristic characteristic = BluetoothGattSingleton.getGatt().getService(Statics.SPOTA_SERVICE_UUID).getCharacteristic(Statics.SPOTA_PATCH_DATA_UUID);
//        characteristic.setValue(chunk);
//        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
//        boolean r = BluetoothGattSingleton.getGatt().writeCharacteristic(characteristic);
//        Log.d(TAG, "writeCharacteristic: " + r);
//
//        Bundle args = new Bundle();
//        args.putString(EXTRA_MAC, mac);
//        args.putSerializable(EXTRA_SERVICE_UUID, service);
//        args.putSerializable(EXTRA_CHARACTER_UUID, character);
//        args.putByteArray(EXTRA_BYTE_VALUE, value);
//        safeCallBluetoothApi(CODE_WRITE_NORSP_DIRECTLY, args, new BluetoothResponse() {
//            @Override
//            protected void onAsyncResponse(int code, Bundle data) {
//                checkRuntime(true);
//                if (response != null) {
//                    response.onResponse(code);
//                }
//            }
//        });
        BleConnectManager.writeNoRspDirectly(mac, service, character, value, new BleGeneralResponse() {
            @Override
            public void onResponse(int code, Bundle data) {
//                checkRuntime(true);
                if (response != null) {
                    BluetoothLog.d("BleWriteNoRspDirectRequest writeNoRspDirectly mResponse code = " + code);
                    response.onResponse(code);
                }
            }
        });
    }

    private void saveNotifyListener(String mac, UUID service, UUID character, BleNotifyResponse response) {
        checkRuntime(true);
        HashMap<String, List<BleNotifyResponse>> listenerMap = mNotifyResponses.get(mac);
        if (listenerMap == null) {
            listenerMap = new HashMap<String, List<BleNotifyResponse>>();
            mNotifyResponses.put(mac, listenerMap);
        }

        String key = generateCharacterKey(service, character);
        List<BleNotifyResponse> responses = listenerMap.get(key);
        if (responses == null) {
            responses = new ArrayList<BleNotifyResponse>();
            listenerMap.put(key, responses);
        }

        responses.add(response);
    }

    private void removeNotifyListener(String mac, UUID service, UUID character) {
        checkRuntime(true);
        HashMap<String, List<BleNotifyResponse>> listenerMap = mNotifyResponses.get(mac);
        if (listenerMap != null) {
            String key = generateCharacterKey(service, character);
            listenerMap.remove(key);
        }
    }

    private void clearNotifyListener(String mac) {
        checkRuntime(true);
        mNotifyResponses.remove(mac);
    }

    private String generateCharacterKey(UUID service, UUID character) {
        return String.format("%s_%s", service, character);
    }

    @Override
    public void notify(final String mac, final UUID service, final UUID character, final BleNotifyResponse response) {
        Bundle args = new Bundle();
        args.putString(EXTRA_MAC, mac);
        args.putSerializable(EXTRA_SERVICE_UUID, service);
        args.putSerializable(EXTRA_CHARACTER_UUID, character);
        safeCallBluetoothApi(CODE_NOTIFY, args, new BluetoothResponse() {
            @Override
            protected void onAsyncResponse(int code, Bundle data) {
                checkRuntime(true);
                if (response != null) {
                    if (code == REQUEST_SUCCESS) {
                        saveNotifyListener(mac, service, character, response);
                    }
                    response.onResponse(code);
                }
            }
        });
    }

    @Override
    public void unnotify(final String mac, final UUID service, final UUID character, final BleUnnotifyResponse response) {
        Bundle args = new Bundle();
        args.putString(EXTRA_MAC, mac);
        args.putSerializable(EXTRA_SERVICE_UUID, service);
        args.putSerializable(EXTRA_CHARACTER_UUID, character);
        safeCallBluetoothApi(CODE_UNNOTIFY, args, new BluetoothResponse() {
            @Override
            protected void onAsyncResponse(int code, Bundle data) {
                checkRuntime(true);

                removeNotifyListener(mac, service, character);

                if (response != null) {
                    response.onResponse(code);
                }
            }
        });
    }

    @Override
    public void indicate(final String mac, final UUID service, final UUID character, final BleNotifyResponse response) {
        Bundle args = new Bundle();
        args.putString(EXTRA_MAC, mac);
        args.putSerializable(EXTRA_SERVICE_UUID, service);
        args.putSerializable(EXTRA_CHARACTER_UUID, character);
        safeCallBluetoothApi(CODE_INDICATE, args, new BluetoothResponse() {
            @Override
            protected void onAsyncResponse(int code, Bundle data) {
                checkRuntime(true);
                if (response != null) {
                    if (code == REQUEST_SUCCESS) {
                        saveNotifyListener(mac, service, character, response);
                    }
                    response.onResponse(code);
                }
            }
        });
    }

    @Override
    public void unindicate(String mac, UUID service, UUID character, BleUnnotifyResponse response) {
        unnotify(mac, service, character, response);
    }

    @Override
    public void readRssi(String mac, final BleReadRssiResponse response) {
        Bundle args = new Bundle();
        args.putString(EXTRA_MAC, mac);
        safeCallBluetoothApi(CODE_READ_RSSI, args, new BluetoothResponse() {
            @Override
            protected void onAsyncResponse(int code, Bundle data) {
                checkRuntime(true);
                if (response != null) {
                    response.onResponse(code, data.getInt(EXTRA_RSSI, 0));
                }
            }
        });
    }

    @Override
    public void requestMtu(String mac, int mtu, final BleMtuResponse response) {
        Bundle args = new Bundle();
        args.putString(EXTRA_MAC, mac);
        args.putInt(EXTRA_MTU, mtu);
        safeCallBluetoothApi(CODE_REQUEST_MTU, args, new BluetoothResponse() {
            @Override
            protected void onAsyncResponse(int code, Bundle data) {
                checkRuntime(true);
                if (response != null) {
                    response.onResponse(code, data.getInt(EXTRA_MTU, GATT_DEF_BLE_MTU_SIZE));
                }
            }
        });
    }

    @Override
    public void search(SearchRequest request, final SearchResponse response) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_REQUEST, request);
        safeCallBluetoothApi(CODE_SEARCH, args, new BluetoothResponse() {
            @Override
            protected void onAsyncResponse(int code, Bundle data) {
                checkRuntime(true);

                if (response == null) {
                    return;
                }

                data.setClassLoader(getClass().getClassLoader());

                switch (code) {
                    case SEARCH_START:
                        response.onSearchStarted();
                        break;

                    case SEARCH_CANCEL:
                        response.onSearchCanceled();
                        break;

                    case SEARCH_STOP:
                        response.onSearchStopped();
                        break;

                    case DEVICE_FOUND:
                        SearchResult device = data.getParcelable(EXTRA_SEARCH_RESULT);
                        response.onDeviceFounded(device);
                        break;

                    case SEARCH_FAIL:
                        int errorCode = data.getInt(EXTRA_SEARCH_RESULT);
                        response.onSearchFail(errorCode);
                        break;

                    default:
                        throw new IllegalStateException("unknown code");
                }
            }
        });
    }

    @Override
    public void stopSearch() {
        safeCallBluetoothApi(CODE_STOP_SESARCH, null, null);
    }

    @Override
    public void registerBluetoothStateListener(BluetoothStateListener listener) {
        try {
            BluetoothLog.i("BluetoothClientImpl registerBluetoothStateListener");
            checkRuntime(true);
            if (listener != null && !mBluetoothStateListeners.contains(listener)) {
                mBluetoothStateListeners.add(listener);
            }
        } catch (Exception e) {
            Log.e(TAG, "registerBluetoothStateListener: ", e);
            e.printStackTrace();
        }
    }

    @Override
    public void unregisterBluetoothStateListener(BluetoothStateListener listener) {
        try {
            checkRuntime(true);
            if (listener != null && mBluetoothStateListeners != null && mBluetoothStateListeners.size() > 0) {
                mBluetoothStateListeners.remove(listener);
            }
        } catch (Exception e) {
            Log.e(TAG, "unregisterBluetoothStateListener: ", e);
            e.printStackTrace();
        }
    }

    @Override
    public void registerBluetoothBondListener(BluetoothBondListener listener) {
        checkRuntime(true);
        if (listener != null && !mBluetoothBondListeners.contains(listener)) {
            mBluetoothBondListeners.add(listener);
        }
    }

    @Override
    public void unregisterBluetoothBondListener(BluetoothBondListener listener) {
        checkRuntime(true);
        if (listener != null) {
            mBluetoothBondListeners.remove(listener);
        }
    }

    @Override
    public void clearRequest(String mac, int type) {
        Bundle args = new Bundle();
        args.putString(EXTRA_MAC, mac);
        args.putInt(EXTRA_TYPE, type);
        safeCallBluetoothApi(CODE_CLEAR_REQUEST, args, null);
    }

    @Override
    public void refreshCache(String mac) {
        checkRuntime(true);
        Bundle args = new Bundle();
        args.putString(EXTRA_MAC, mac);
        safeCallBluetoothApi(CODE_REFRESH_CACHE, args, null);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void stopAdvertising(final AdvertisingResponse response) {
        if (advertiser != null) {
            state = 0;
            advertisingResponse = response;
            advertiser.stopAdvertising(advertiseCallback);
            advertiser = null;
        }

        // 取消超时定时器
        if (mAdvertisingTimeoutRunnable != null) {
            mWorkerHandler.removeCallbacks(mAdvertisingTimeoutRunnable);
            mAdvertisingTimeoutRunnable = null;
        }

        // 清除缓存数据
        mCurrentAdvertisingData = null;
        BluetoothLog.v(String.format("stopAdvertising: 已清除广播数据缓存"));
    }

    @SuppressLint("MissingPermission")
    @Override
    public void startAdvertising(UUID SERVICE_UUID, UUID CHARACTERISTIC_UUID, String name, boolean connectable, byte[] data,
                                 int timeoutMillis, int manufacturerId, final AdvertisingResponse response) {
        BluetoothAdapter bluetoothAdapter = BluetoothUtils.getBluetoothAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            return;
        }

        // 数据长度检查
        if (data.length > 20) {
            BluetoothLog.e("startAdvertising Manufacturer data is too long!");
            return;
        }

        if (BluetoothLog.isDebug) {
            String dataHex = byte2Hex(data);
            BluetoothLog.d("startAdvertising Manufacturer data :" + dataHex + " timeoutMillis:" + timeoutMillis + " name:" + name + " connectable:" + connectable);
        }

        // 智能比较：如果data数据相同且当前正在广播，则无需重启
        if (mCurrentAdvertisingData != null && Arrays.equals(mCurrentAdvertisingData, data) &&
                state == 1 &&
                advertiser != null) {
            BluetoothLog.d("Advertising data unchanged, skipping restart");

            // 更新回调（即使data相同，回调可能不同）
            advertisingResponse = response;
            return;
        }

        BluetoothLog.d("Advertising data changed or not running, restarting advertising");

        // 停止之前的广播（如果正在运行）
        if (advertiser != null && state == 1) {
            BluetoothLog.d("Stopping previous advertising before restart");
            advertiser.stopAdvertising(advertiseCallback);
        }

        // 取消之前的超时定时器
        if (mAdvertisingTimeoutRunnable != null) {
            mWorkerHandler.removeCallbacks(mAdvertisingTimeoutRunnable);
            mAdvertisingTimeoutRunnable = null;
        }

        // 设置设备名称
        bluetoothAdapter.setName(name);

        // 更新状态和回调
        state = 1;
        advertisingResponse = response;
        mCurrentAdvertisingData = data != null ? Arrays.copyOf(data, data.length) : null;

        // 创建 AdvertiseData 对象
        AdvertiseData.Builder builder = new AdvertiseData.Builder();
        if (manufacturerId < 0) {
            builder.addManufacturerData(0x0000, data);
            BluetoothLog.d("startAdvertising Manufacturer manufacturerId :" + 0x0000);
        } else {
            builder.addManufacturerData(manufacturerId, data);
            BluetoothLog.d("startAdvertising Manufacturer manufacturerId :" + manufacturerId);
        }
        builder.setIncludeDeviceName(true);
        if (SERVICE_UUID != null) {
            builder.addServiceUuid(new ParcelUuid(SERVICE_UUID));
        }
        if (CHARACTERISTIC_UUID != null) {
            builder.addServiceData(new ParcelUuid(CHARACTERISTIC_UUID), data);
        }
        AdvertiseData advertiseData = builder.build();

        // 获取广播器并开始广播
        advertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        advertiser.startAdvertising(new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setConnectable(connectable)
                .setTimeout(timeoutMillis)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .build(), advertiseData, advertiseCallback);

        // 设置超时定时器
        if (timeoutMillis > 0 && advertisingResponse != null) {
            mAdvertisingTimeoutRunnable = new Runnable() {
                @Override
                public void run() {
                    BluetoothLog.d("Advertising timeout reached, calling onAdvertisingTimeout");
                    if (advertisingResponse != null) {
                        advertisingResponse.onAdvertisingTimeout();
                    }
                }
            };
            mWorkerHandler.postDelayed(mAdvertisingTimeoutRunnable, timeoutMillis);
        }
    }

    AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            if (state == 0) {
                BluetoothLog.i("stopAdvertise Success");
            } else {
                BluetoothLog.i("startAdvertise Success");
            }
            if (advertisingResponse != null) {
                advertisingResponse.onStartSuccess();
            }
        }

        public void onStartFailure(int errorCode) {
            if (state == 0) {
                BluetoothLog.e("stopAdvertise Fail errorCode:" + errorCode);
            } else {
                BluetoothLog.e("startAdvertise Fail errorCode:" + errorCode);
            }
            if (advertisingResponse != null) {
                advertisingResponse.onStartFailure(errorCode);
            }
        }
    };

    private void safeCallBluetoothApi(int code, Bundle args, final BluetoothResponse response) {
        checkRuntime(true);

//        BluetoothLog.v(String.format("safeCallBluetoothApi code = %d", code));

        try {
            IBluetoothService service = getBluetoothService();

//            BluetoothLog.v(String.format("IBluetoothService = %s", service));

            if (service != null) {
                args = (args != null ? args : new Bundle());
                service.callBluetoothApi(code, args, response);
            } else {
                response.onResponse(SERVICE_UNREADY, null);
            }
        } catch (Throwable e) {
            BluetoothLog.e(e);
        }
    }

    @Override
    public boolean onIntercept(final Object object, final Method method, final Object[] args) {
        mWorkerHandler.obtainMessage(MSG_INVOKE_PROXY, new ProxyBulk(object, method, args))
                .sendToTarget();
        return true;
    }

    private void notifyBluetoothManagerReady() {
//        BluetoothLog.v(String.format("notifyBluetoothManagerReady %s", mCountDownLatch));

        if (mCountDownLatch != null) {
            mCountDownLatch.countDown();
            mCountDownLatch = null;
        }
    }

    private void waitBluetoothManagerReady() {
//        BluetoothLog.v(String.format("waitBluetoothManagerReady %s", mCountDownLatch));
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        try {
            switch (msg.what) {
                case MSG_INVOKE_PROXY:
                    ProxyBulk.safeInvoke(msg.obj);
                    break;
                case MSG_REG_RECEIVER:
                    registerBluetoothReceiver();
                    break;
            }
        } catch (Exception e) {
            BluetoothLog.e("BluetoothClientImpl msg what:" + msg.what);
            BluetoothLog.e(e);
            e.printStackTrace();
        }
        return true;
    }

    private void registerBluetoothReceiver() {
        checkRuntime(true);
        if (mBluetoothReceiverRegistered) {
            return;
        }
        BluetoothReceiver.getInstance().register(new BluetoothStateChangeListener() {
            @Override
            protected void onBluetoothStateChanged(int prevState, int curState) {
                checkRuntime(true);
                dispatchBluetoothStateChanged(curState);
            }
        });
        BluetoothReceiver.getInstance().register(new BluetoothBondStateChangeListener() {
            @Override
            protected void onBondStateChanged(String mac, int bondState) {
                checkRuntime(true);
                dispatchBondStateChanged(mac, bondState);
            }
        });
        BluetoothReceiver.getInstance().register(new BleConnectStatusChangeListener() {
            @Override
            protected void onConnectStatusChanged(String mac, int status) {
                checkRuntime(true);
                if (status == Constants.STATUS_DISCONNECTED) {
                    clearNotifyListener(mac);
                }
                dispatchConnectionStatus(mac, status);
            }
        });
        BluetoothReceiver.getInstance().register(new BleCharacterChangeListener() {
            @Override
            public void onCharacterChanged(String mac, UUID service, UUID character, byte[] value) {
                checkRuntime(true);
                dispatchCharacterNotify(mac, service, character, value);
            }
        });
        mBluetoothReceiverRegistered = true;
    }

    private void dispatchCharacterNotify(String mac, UUID service, UUID character, byte[] value) {
        checkRuntime(true);
        HashMap<String, List<BleNotifyResponse>> notifyMap = mNotifyResponses.get(mac);
        if (notifyMap != null) {
            String key = generateCharacterKey(service, character);
            List<BleNotifyResponse> responses = notifyMap.get(key);
            if (responses != null) {
                for (final BleNotifyResponse response : responses) {
                    response.onNotify(service, character, value);
                }
            }
        }
    }

    private void dispatchConnectionStatus(final String mac, final int status) {
        checkRuntime(true);
        List<BleConnectStatusListener> listeners = mConnectStatusListeners.get(mac);
        if (!ListUtils.isEmpty(listeners)) {
            for (final BleConnectStatusListener listener : listeners) {
                listener.invokeSync(mac, status);
            }
        }
    }

    private void dispatchBluetoothStateChanged(final int currentState) {
        checkRuntime(true);
        if (currentState == Constants.STATE_OFF || currentState == Constants.STATE_ON) {
            for (final BluetoothStateListener listener : mBluetoothStateListeners) {
                listener.invokeSync(currentState == Constants.STATE_ON);
            }
        }
    }

    private void dispatchBondStateChanged(final String mac, final int bondState) {
        checkRuntime(true);
        for (final BluetoothBondListener listener : mBluetoothBondListeners) {
            listener.invokeSync(mac, bondState);
        }
    }

    private void checkRuntime(boolean async) {
        try {
            if (Looper.myLooper() == null) {
                BluetoothLog.e("No Looper; Looper.myLooper() == null");
                Looper.prepare();
            }
            Looper targetLooper = async ? mWorkerHandler.getLooper() : Looper.getMainLooper();
            if (Looper.myLooper() != targetLooper) {
                throw new RuntimeException();
            }
        } catch (RuntimeException e) {
            BluetoothLog.e(e);
            e.printStackTrace();
        }
    }


    /**
     * 将byte[]数组转换成16进制字符。一个byte生成两个字符，长度对应1:2
     *
     * @param bytes，输入byte[]数组
     * @return 16进制字符
     */
    public String byte2Hex(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        // 遍历byte[]数组，将每个byte数字转换成16进制字符，再拼接起来成字符串
        for (int i = 0; i < bytes.length; i++) {
            // 每个byte转换成16进制字符时，bytes[i] & 0xff如果高位是0，输出将会去掉，所以+0x100(在更高位加1)，再截取后两位字符
            builder.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return builder.toString();
    }
}
