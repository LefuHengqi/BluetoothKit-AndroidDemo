package com.lefu.bluetooth.library.receiver.listener;

import android.util.Log;

/**
 * Created by dingjikerbo on 17/1/14.
 */

public abstract class BluetoothClientListener extends AbsBluetoothListener {

    @Override
    final public void onInvoke(Object... args) {
        try {
            throw new UnsupportedOperationException();
        } catch (UnsupportedOperationException e) {
            Log.e("BluetoothClientListener", "onInvoke args:" + args);
            e.printStackTrace();
        }
    }
}
