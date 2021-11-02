package com.lefu.ppscale.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.peng.ppscale.util.ByteUtil;


public class TestBleActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        bluetoothAdapter.startLeScan(mLeScanCallback);


    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, final byte[] scanRecord) {

            Log.d("liyp_ ble = ", ByteUtil.byteToString(scanRecord));

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
//                        String struuid = NumberUtils.bytes2HexString(NumberUtils.reverseBytes(scanRecord)).replace("-", "").toLowerCase();
//                        if (device!=null && struuid.contains(DEVICE_UUID_PREFIX.toLowerCase())) {
//                            mBluetoothDevices.add(device);
//                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothAdapter != null) {
            bluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }
}
