// IBleResponse.aidl
package com.lefu.bluetooth.library;

// Declare any non-default types here with import statements

import android.os.Bundle;

interface IResponse {
    void onResponse(int code, inout Bundle data);
}
