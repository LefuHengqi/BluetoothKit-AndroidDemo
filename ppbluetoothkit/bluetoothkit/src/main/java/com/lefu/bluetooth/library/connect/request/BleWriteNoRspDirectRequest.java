package com.lefu.bluetooth.library.connect.request;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import com.lefu.bluetooth.library.Code;
import com.lefu.bluetooth.library.Constants;
import com.lefu.bluetooth.library.connect.listener.WriteCharacterListener;
import com.lefu.bluetooth.library.connect.response.BleGeneralResponse;
import com.lefu.bluetooth.library.utils.BluetoothLog;

import java.util.UUID;

public class BleWriteNoRspDirectRequest extends BleRequest implements WriteCharacterListener {

    private static volatile BleWriteNoRspDirectRequest instance = null;

    public BleWriteNoRspDirectRequest(BleGeneralResponse response) {
        super(response);
    }

    public static BleWriteNoRspDirectRequest getInstance(BleGeneralResponse response) {
        if (instance == null) {
            synchronized (BleWriteNoRspDirectRequest.class) {
                if (instance == null) {
                    instance = new BleWriteNoRspDirectRequest(response);
                }
            }
        }
        return instance;
    }

    public static BleWriteNoRspDirectRequest getInstance() {
        return instance;
    }

    public void init() {
        instance = null;
    }

    @Override
    public void processRequest() {
        switch (getCurrentStatus()) {
            case Constants.STATUS_DEVICE_DISCONNECTED:
                onRequestCompleted(Code.REQUEST_FAILED);
                break;
            case Constants.STATUS_DEVICE_CONNECTED:
//                startWrite();
                break;
            case Constants.STATUS_DEVICE_SERVICE_READY:
//                startWrite();
                break;
            default:
                onRequestCompleted(Code.REQUEST_FAILED);
                break;
        }
    }

    public void startWrite(UUID service, UUID character, byte[] bytes) {
        BluetoothLog.v("BleWriteNoRspDirectRequest startWrite");
        if (!writeCharacteristicWithNoRsp(service, character, bytes)) {
            BluetoothLog.e("BleWriteNoRspDirectRequest startWrite failed code= " + Code.REQUEST_FAILED);
            onRequestCompleted(Code.REQUEST_FAILED);
        } else {
            BluetoothLog.v("BleWriteNoRspDirectRequest startWrite success");
//            startRequestTiming();
        }
    }

    protected void onRequestCompleted(int code) {
//        checkRuntime();

        BluetoothLog.v("BleWriteNoRspDirectRequest onRequestCompleted code = " + code);

//        mHandler.removeCallbacksAndMessages(null);
        clearGattResponseListener(this);

        onResponse(code);

//        mDispatcher.onRequestCompleted(this);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGattCharacteristic characteristic, int status, byte[] value) {
        BluetoothLog.v("收到写入完成监听 status = " + status);
//        stopRequestTiming();
        if (status == BluetoothGatt.GATT_SUCCESS) {
            onRequestCompleted(Code.REQUEST_SUCCESS);
        } else {
            onRequestCompleted(Code.REQUEST_FAILED);
        }
    }
}
