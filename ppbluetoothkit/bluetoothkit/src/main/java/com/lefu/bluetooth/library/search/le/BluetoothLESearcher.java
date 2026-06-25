package com.lefu.bluetooth.library.search.le;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;

import com.lefu.bluetooth.library.search.BluetoothSearcher;
import com.lefu.bluetooth.library.search.SearchResult;
import com.lefu.bluetooth.library.search.response.BluetoothSearchResponse;
import com.lefu.bluetooth.library.utils.BluetoothLog;
import com.lefu.bluetooth.library.utils.BluetoothUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dingjikerbo
 */
public class BluetoothLESearcher extends BluetoothSearcher {

    private BluetoothLESearcher() {
        mBluetoothAdapter = BluetoothUtils.getBluetoothAdapter();
    }

    public static BluetoothLESearcher getInstance() {
        return BluetoothLESearcherHolder.instance;
    }

    private static class BluetoothLESearcherHolder {
        private static BluetoothLESearcher instance = new BluetoothLESearcher();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void startScanBluetooth(BluetoothSearchResponse response) {
        super.startScanBluetooth(response);
//        mBluetoothAdapter.startLeScan(mLeScanCallback);
        BluetoothLeScanner bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (bluetoothLeScanner != null) {
            ScanSettings scanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
//            ScanFilter scanFilter = new ScanFilter.Builder().build();
//            List<ScanFilter> scanFilters = new ArrayList();
//            scanFilters.add(scanFilter);
            bluetoothLeScanner.startScan(null, scanSettings, scanCallBack);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void stopScanBluetooth() {
        try {
//            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            if (scanCallBack != null) {
                mBluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallBack);
            }
        } catch (Exception e) {
            BluetoothLog.e(e);
        }
        super.stopScanBluetooth();
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void cancelScanBluetooth() {
//        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        if (scanCallBack != null) {
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallBack);
        }
        super.cancelScanBluetooth();
    }

    private final LeScanCallback mLeScanCallback = new LeScanCallback() {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            notifyDeviceFounded(new SearchResult(device, rssi, scanRecord));
        }

    };

    ScanCallback scanCallBack = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
//            super.onScanResult(callbackType, result);
            notifyDeviceFounded(new SearchResult(result.getDevice(), result.getRssi(), result.getScanRecord().getBytes()));
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
//            super.onScanFailed(errorCode);
            BluetoothLog.e("ScanCallback.onScanFailed errorCode:" + errorCode);
            notifySearchFail(errorCode);
        }
    };

}
