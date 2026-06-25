package com.lefu.bluetooth.library.search.response;

import com.lefu.bluetooth.library.search.SearchResult;

public interface BluetoothSearchResponse {
    void onSearchStarted();

    void onDeviceFounded(SearchResult device);

    void onSearchStopped();

    void onSearchCanceled();
    void onSearchFail(int errorCode);
}
