package com.lefu.bluetooth.library.connect.listener;

import com.lefu.bluetooth.library.model.BleGattProfile;

/**
 * Created by dingjikerbo on 2016/8/25.
 */
public interface ServiceDiscoverListener extends GattResponseListener {
    void onServicesDiscovered(int status, BleGattProfile profile);

    //返回fasle则通知外部断开连接，如返回true内部会自动重试，不对外回调断开连接状态
    boolean isRetry();

}
