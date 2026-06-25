package com.lefu.bluetooth.library.search.response;

import com.lefu.bluetooth.library.search.SearchResult;

/**
 * Created by dingjikerbo on 2016/9/1.
 */
public interface SearchResponse {

    void onSearchStarted();

    void onDeviceFounded(SearchResult device);

    void onSearchStopped();

    void onSearchCanceled();
    void onSearchFail(int errorCode);
}
