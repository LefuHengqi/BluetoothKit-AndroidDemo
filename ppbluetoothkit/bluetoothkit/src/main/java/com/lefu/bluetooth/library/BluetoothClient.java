package com.lefu.bluetooth.library;

import android.content.Context;

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
import com.lefu.bluetooth.library.utils.BluetoothLog;
import com.lefu.bluetooth.library.utils.BluetoothUtils;
import com.lefu.bluetooth.library.utils.ByteUtils;
import com.lefu.bluetooth.library.utils.proxy.ProxyUtils;

import java.util.UUID;

/**
 * Created by dingjikerbo on 2016/9/1.
 */
public class BluetoothClient implements IBluetoothClient {

    private IBluetoothClient mClient;

    public BluetoothClient(Context context) {
        if (context == null) {
            throw new NullPointerException("Context null");
        }
        mClient = BluetoothClientImpl.getInstance(context);
    }

    public void connect(String mac, BleConnectResponse response) {
        connect(mac, null, response);
    }

    @Override
    public void connect(String mac, BleConnectOptions options, BleConnectResponse response) {
        BluetoothLog.v(String.format("connect %s", mac));
        response = ProxyUtils.getUIProxy(response);
        mClient.connect(mac, options, response);
    }

    @Override
    public void disconnect(String mac) {
        BluetoothLog.v(String.format("disconnect %s", mac));
        mClient.disconnect(mac);
    }

    @Override
    public void read(String mac, UUID service, UUID character, BleReadResponse response) {
        BluetoothLog.v(String.format("read character for %s: service = %s, character = %s", mac, service, character));

        response = ProxyUtils.getUIProxy(response);
        mClient.read(mac, service, character, response);
    }

    @Override
    public void write(String mac, UUID service, UUID character, byte[] value, BleWriteResponse response) {
        BluetoothLog.v(String.format("write character for %s: service = %s, character = %s, value = %s",
                mac, service, character, ByteUtils.byteToString(value)));

        response = ProxyUtils.getUIProxy(response);
        mClient.write(mac, service, character, value, response);
    }

    @Override
    public void readDescriptor(String mac, UUID service, UUID character, UUID descriptor, BleReadResponse response) {
        BluetoothLog.v(String.format("readDescriptor for %s: service = %s, character = %s", mac, service, character));
        response = ProxyUtils.getUIProxy(response);
        mClient.readDescriptor(mac, service, character, descriptor, response);
    }

    @Override
    public void writeDescriptor(String mac, UUID service, UUID character, UUID descriptor, byte[] value, BleWriteResponse response) {
        BluetoothLog.v(String.format("writeDescriptor for %s: service = %s, character = %s", mac, service, character));
        response = ProxyUtils.getUIProxy(response);
        mClient.writeDescriptor(mac, service, character, descriptor, value, response);
    }

    @Override
    public void writeNoRsp(String mac, UUID service, UUID character, byte[] value, BleWriteResponse response) {
        BluetoothLog.v(String.format("writeNoRsp %s: service = %s, character = %s, value = %s", mac, service, character, ByteUtils.byteToString(value)));

        response = ProxyUtils.getUIProxy(response);
        mClient.writeNoRsp(mac, service, character, value, response);
    }

    @Override
    public void writeNoRspDirectly(String mac, UUID service, UUID character, byte[] value, BleWriteResponse response) {
//        BluetoothLog.v(String.format("writeNoRspDirectly %s: service = %s, character = %s, value = %s", mac, service, character, ByteUtils.byteToString(value)));
        BluetoothLog.v("writeNoRspDirectly " + mac +
                ": service = " + service +
                ", character = " + character +
                ", value = " + value.length);

//        response = ProxyUtils.getUIProxy(response);
        mClient.writeNoRspDirectly(mac, service, character, value, response);
    }


    @Override
    public void notify(String mac, UUID service, UUID character, BleNotifyResponse response) {
        BluetoothLog.v(String.format("notify %s: service = %s, character = %s", mac, service, character));

        response = ProxyUtils.getUIProxy(response);
        mClient.notify(mac, service, character, response);
    }

    @Override
    public void unnotify(String mac, UUID service, UUID character, BleUnnotifyResponse response) {
        BluetoothLog.v(String.format("unnotify %s: service = %s, character = %s", mac, service, character));

        response = ProxyUtils.getUIProxy(response);
        mClient.unnotify(mac, service, character, response);
    }

    @Override
    public void indicate(String mac, UUID service, UUID character, BleNotifyResponse response) {
        BluetoothLog.v(String.format("indicate %s: service = %s, character = %s", mac, service, character));

        response = ProxyUtils.getUIProxy(response);
        mClient.indicate(mac, service, character, response);
    }

    @Override
    public void unindicate(String mac, UUID service, UUID character, BleUnnotifyResponse response) {
        BluetoothLog.v(String.format("unindicate %s: service = %s, character = %s", mac, service, character));

        response = ProxyUtils.getUIProxy(response);
        mClient.unindicate(mac, service, character, response);
    }

    @Override
    public void readRssi(String mac, BleReadRssiResponse response) {
        BluetoothLog.v(String.format("readRssi %s", mac));

        response = ProxyUtils.getUIProxy(response);
        mClient.readRssi(mac, response);
    }

    @Override
    public void requestMtu(String mac, int mtu, BleMtuResponse response) {
        BluetoothLog.v(String.format("requestMtu %s", mac));

        response = ProxyUtils.getUIProxy(response);
        mClient.requestMtu(mac, mtu, response);
    }

    @Override
    public void search(SearchRequest request, SearchResponse response) {
        BluetoothLog.v(String.format("search %s", request));

        response = ProxyUtils.getUIProxy(response);
        mClient.search(request, response);
    }

    @Override
    public void stopSearch() {
        BluetoothLog.v(String.format("stopSearch"));
        mClient.stopSearch();
    }

    @Override
    public void registerConnectStatusListener(String mac, BleConnectStatusListener listener) {
        mClient.registerConnectStatusListener(mac, listener);
    }

    @Override
    public void unregisterConnectStatusListener(String mac, BleConnectStatusListener listener) {
        mClient.unregisterConnectStatusListener(mac, listener);
    }

    @Override
    public void registerBluetoothStateListener(BluetoothStateListener listener) {
        mClient.registerBluetoothStateListener(listener);
    }

    @Override
    public void unregisterBluetoothStateListener(BluetoothStateListener listener) {
        mClient.unregisterBluetoothStateListener(listener);
    }

    @Override
    public void registerBluetoothBondListener(BluetoothBondListener listener) {
        mClient.registerBluetoothBondListener(listener);
    }

    @Override
    public void unregisterBluetoothBondListener(BluetoothBondListener listener) {
        mClient.unregisterBluetoothBondListener(listener);
    }

    public int getConnectStatus(String mac) {
        return BluetoothUtils.getConnectStatus(mac);
    }

    public boolean isBluetoothOpened() {
        return BluetoothUtils.isBluetoothEnabled();
    }

    public boolean openBluetooth() {
        return BluetoothUtils.openBluetooth();
    }

    public boolean closeBluetooth() {
        return BluetoothUtils.closeBluetooth();
    }

    public boolean isBleSupported() {
        return BluetoothUtils.isBleSupported();
    }

    public int getBondState(String mac) {
        return BluetoothUtils.getBondState(mac);
    }

    @Override
    public void clearRequest(String mac, int type) {
        mClient.clearRequest(mac, type);
    }

    @Override
    public void refreshCache(String mac) {
        mClient.refreshCache(mac);
    }

    @Override
    public void stopAdvertising(AdvertisingResponse response) {
        mClient.stopAdvertising(response);
    }

    @Override
    public void startAdvertising(UUID service, UUID character, String name, boolean connectable, byte[] value, int timeoutMillis, int manufacturerId, AdvertisingResponse response) {
        mClient.startAdvertising(service, character, name, connectable, value, timeoutMillis, manufacturerId, response);
    }
}
