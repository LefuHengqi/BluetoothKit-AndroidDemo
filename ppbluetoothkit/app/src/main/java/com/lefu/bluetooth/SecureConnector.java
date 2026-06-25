package com.lefu.bluetooth;


import android.bluetooth.BluetoothDevice;

import com.lefu.bluetooth.library.Code;
import com.lefu.bluetooth.library.connect.response.BleNotifyResponse;
import com.lefu.bluetooth.library.connect.response.BleWriteResponse;
import com.lefu.bluetooth.library.utils.ByteUtils;
import com.lefu.bluetooth.library.utils.UUIDUtils;

import java.util.UUID;

/**
 * Created by liwentian on 2017/3/27.
 */

public class SecureConnector {

    private static BluetoothDevice mDevice;

    public static void processStep1(BluetoothDevice device) {
        mDevice = device;

        ClientManager.getClient().write(device.getAddress(), UUIDUtils.makeUUID(0xFE95), UUIDUtils.makeUUID(0x10),
                ByteUtils.fromInt(0xDE85CA90), new BleWriteResponse() {
                    @Override
                    public void onResponse(int code) {
                        if (code == Code.REQUEST_SUCCESS) {
                            processStep2();
                        }
                    }
                });
    }

    public static void processStep2() {
        ClientManager.getClient().notify(mDevice.getAddress(), UUIDUtils.makeUUID(0xFE95),
                UUIDUtils.makeUUID(0x01), new BleNotifyResponse() {
                    @Override
                    public void onNotify(UUID service, UUID character, byte[] value) {

                    }

                    @Override
                    public void onResponse(int code) {

                    }
                });
    }
}
