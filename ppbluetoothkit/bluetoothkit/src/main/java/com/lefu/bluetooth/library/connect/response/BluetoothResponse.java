package com.lefu.bluetooth.library.connect.response;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;

import com.lefu.bluetooth.library.IResponse;

/**
 * Created by dingjikerbo on 2015/12/31.
 */
public abstract class BluetoothResponse extends IResponse.Stub implements Handler.Callback {

    private static final int MSG_RESPONSE = 1;

    protected abstract void onAsyncResponse(int code, Bundle data);

    private Handler mHandler;

    protected BluetoothResponse() {
        Looper looper = Looper.myLooper();
        if (looper == null) {
            looper = Looper.getMainLooper();
        }
        mHandler = new Handler(looper, this);
    }

    @Override
    public void onResponse(int code, Bundle data) throws RemoteException {
        mHandler.obtainMessage(MSG_RESPONSE, code, 0, data).sendToTarget();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_RESPONSE:
                onAsyncResponse(msg.arg1, (Bundle) msg.obj);
                break;
        }
        return true;
    }
}
