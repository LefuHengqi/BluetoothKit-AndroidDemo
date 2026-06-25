package com.lefu.bluetooth.library.connect.request;

import android.bluetooth.BluetoothGatt;

import com.lefu.bluetooth.library.Code;
import com.lefu.bluetooth.library.Constants;
import com.lefu.bluetooth.library.connect.listener.RequestMtuListener;
import com.lefu.bluetooth.library.connect.response.BleGeneralResponse;

/**
 * Created by fuhao on 2017/8/24.
 */

public class BleMtuRequest extends BleRequest implements RequestMtuListener {

    private int mMtu;

    public BleMtuRequest(int mtu, BleGeneralResponse response) {
        super(response);
        mMtu = mtu;
    }

    @Override
    public void processRequest() {
        switch (getCurrentStatus()) {
            case Constants.STATUS_DEVICE_DISCONNECTED:
                onRequestCompleted(Code.REQUEST_FAILED);
                break;

            case Constants.STATUS_DEVICE_CONNECTED:
                requestMtu();
                break;

            case Constants.STATUS_DEVICE_SERVICE_READY:
                requestMtu();
                break;

            default:
                onRequestCompleted(Code.REQUEST_FAILED);
                break;
        }
    }

    private void requestMtu() {
        if (!requestMtu(mMtu)) {
            onRequestCompleted(Code.REQUEST_FAILED);
        } else {
            startRequestTiming();
        }
    }

    @Override
    public void onMtuChanged(int mtu, int status) {
        stopRequestTiming();

        if (status == BluetoothGatt.GATT_SUCCESS) {
            putIntExtra(Constants.EXTRA_MTU, mtu);
            onRequestCompleted(Code.REQUEST_SUCCESS);
        } else {
            onRequestCompleted(Code.REQUEST_FAILED);
        }
    }
}
