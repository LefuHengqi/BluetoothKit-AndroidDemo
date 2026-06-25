package com.lefu.bluetooth.library.search;

import android.os.Bundle;

import com.lefu.bluetooth.library.connect.response.BleGeneralResponse;
import com.lefu.bluetooth.library.search.response.BluetoothSearchResponse;

import static com.lefu.bluetooth.library.Constants.DEVICE_FOUND;
import static com.lefu.bluetooth.library.Constants.EXTRA_SEARCH_RESULT;
import static com.lefu.bluetooth.library.Constants.SEARCH_CANCEL;
import static com.lefu.bluetooth.library.Constants.SEARCH_FAIL;
import static com.lefu.bluetooth.library.Constants.SEARCH_START;
import static com.lefu.bluetooth.library.Constants.SEARCH_STOP;

/**
 * Created by dingjikerbo on 2016/8/28.
 */
public class BluetoothSearchManager {

    public static void search(SearchRequest request, final BleGeneralResponse response) {
        BluetoothSearchRequest requestWrapper = new BluetoothSearchRequest(request);
        BluetoothSearchHelper.getInstance().startSearch(requestWrapper, new BluetoothSearchResponse() {
            @Override
            public void onSearchStarted() {
                response.onResponse(SEARCH_START, null);
            }

            @Override
            public void onDeviceFounded(SearchResult device) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(EXTRA_SEARCH_RESULT, device);
                response.onResponse(DEVICE_FOUND, bundle);
            }

            @Override
            public void onSearchStopped() {
                response.onResponse(SEARCH_STOP, null);
            }

            @Override
            public void onSearchCanceled() {
                response.onResponse(SEARCH_CANCEL, null);
            }

            @Override
            public void onSearchFail(int errorCode) {
                Bundle bundle = new Bundle();
                bundle.putInt(EXTRA_SEARCH_RESULT, errorCode);
                response.onResponse(SEARCH_FAIL, bundle);
            }
        });
    }

    public static void stopSearch() {
        BluetoothSearchHelper.getInstance().stopSearch();
    }
}
