package com.lefu.bluetooth.library.connect.listener;
import com.lefu.bluetooth.library.receiver.listener.BluetoothClientListener;
import android.util.Log;

/**
 * Created by dingjikerbo on 2016/11/26.
 */

public abstract class BluetoothStateListener extends BluetoothClientListener {

    public abstract void onBluetoothStateChanged(boolean openOrClosed);

    @Override
    public void onSyncInvoke(Object... args) {
        try {
            boolean openOrClosed = (boolean) args[0];
            onBluetoothStateChanged(openOrClosed);
        } catch (Exception e) {
            Log.e("BluetoothStateListener", "onSyncInvoke" + e.getMessage());
            e.printStackTrace();
        }
    }
}
