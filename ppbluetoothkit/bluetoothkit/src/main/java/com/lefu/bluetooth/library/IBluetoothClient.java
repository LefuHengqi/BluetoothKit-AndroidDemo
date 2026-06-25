package com.lefu.bluetooth.library;

import com.lefu.bluetooth.library.advertising.AdvertisingResponse;
import com.lefu.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.lefu.bluetooth.library.connect.options.BleConnectOptions;
import com.lefu.bluetooth.library.connect.response.BleConnectResponse;
import com.lefu.bluetooth.library.connect.response.BleMtuResponse;
import com.lefu.bluetooth.library.connect.response.BleNotifyResponse;
import com.lefu.bluetooth.library.connect.response.BleReadResponse;
import com.lefu.bluetooth.library.connect.response.BleReadRssiResponse;
import com.lefu.bluetooth.library.connect.response.BleUnnotifyResponse;
import com.lefu.bluetooth.library.connect.response.BleWriteResponse;
import com.lefu.bluetooth.library.receiver.listener.BluetoothBondListener;
import com.lefu.bluetooth.library.connect.listener.BluetoothStateListener;
import com.lefu.bluetooth.library.search.SearchRequest;
import com.lefu.bluetooth.library.search.response.SearchResponse;

import java.util.UUID;

/**
 * Created by dingjikerbo on 2016/8/25.
 */
public interface IBluetoothClient {

    void connect(String mac, BleConnectOptions options, BleConnectResponse response);

    void disconnect(String mac);

    void registerConnectStatusListener(String mac, BleConnectStatusListener listener);

    void unregisterConnectStatusListener(String mac, BleConnectStatusListener listener);

    void read(String mac, UUID service, UUID character, BleReadResponse response);

    void write(String mac, UUID service, UUID character, byte[] value, BleWriteResponse response);

    void readDescriptor(String mac, UUID service, UUID character, UUID descriptor, BleReadResponse response);

    void writeDescriptor(String mac, UUID service, UUID character, UUID descriptor, byte[] value, BleWriteResponse response);

    void writeNoRsp(String mac, UUID service, UUID character, byte[] value, BleWriteResponse response);

    void writeNoRspDirectly(String mac, UUID service, UUID character, byte[] value, BleWriteResponse response);

    void notify(String mac, UUID service, UUID character, BleNotifyResponse response);

    void unnotify(String mac, UUID service, UUID character, BleUnnotifyResponse response);

    void indicate(String mac, UUID service, UUID character, BleNotifyResponse response);

    void unindicate(String mac, UUID service, UUID character, BleUnnotifyResponse response);

    void readRssi(String mac, BleReadRssiResponse response);

    void requestMtu(String mac, int mtu, BleMtuResponse response);

    void search(SearchRequest request, SearchResponse response);

    void stopSearch();

    void registerBluetoothStateListener(BluetoothStateListener listener);

    void unregisterBluetoothStateListener(BluetoothStateListener listener);

    void registerBluetoothBondListener(BluetoothBondListener listener);

    void unregisterBluetoothBondListener(BluetoothBondListener listener);

    void clearRequest(String mac, int type);

    void refreshCache(String mac);

    void stopAdvertising(AdvertisingResponse response);

    void startAdvertising(UUID service, UUID character, String name, boolean connectable, byte[] value, int timeoutMillis, int manufacturerId, AdvertisingResponse response);
}
