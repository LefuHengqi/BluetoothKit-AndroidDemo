// IBluetoothManager.aidl
package com.lefu.bluetooth.library;

// Declare any non-default types here with import statements

import android.os.Bundle;
import com.lefu.bluetooth.library.IResponse;

interface IBluetoothService {
    void callBluetoothApi(int code, inout Bundle args, IResponse response);
}
