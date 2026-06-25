package com.lefu.bluetooth.library.connect;

import com.lefu.bluetooth.library.connect.request.BleRequest;

public interface IBleConnectDispatcher {

    void onRequestCompleted(BleRequest request);
}
