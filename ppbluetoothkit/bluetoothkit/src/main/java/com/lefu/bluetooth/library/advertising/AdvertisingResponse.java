package com.lefu.bluetooth.library.advertising;

/**
 * Created by dingjikerbo on 2016/9/1.
 */
public interface AdvertisingResponse {

    void onStartSuccess();

    void onStartFailure(int errorCode);

    void onAdvertisingTimeout();

}
