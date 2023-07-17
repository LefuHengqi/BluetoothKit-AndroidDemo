package com.lefu.test260h.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class BeaconParser {
    private byte[] bytes;
    private ByteBuffer mByteBuffer;

    public BeaconParser(BeaconItem item) {
        this(item.bytes);
    }

    public BeaconParser(byte[] bytes) {
        this.bytes = bytes;
        this.mByteBuffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
    }

    public void setPosition(int position) {
        this.mByteBuffer.position(position);
    }

    public int readByte() {
        return this.mByteBuffer.get() & 255;
    }

    public int readShort() {
        return this.mByteBuffer.getShort() & '\uffff';
    }

    public boolean getBit(int n, int index) {
        return (n & 1 << index) != 0;
    }

    public static List<BeaconItem> parseBeacon(byte[] bytes) {
        ArrayList<BeaconItem> items = new ArrayList();

        BeaconItem item;
        for(int i = 0; i < bytes.length; i += item.len + 1) {
            item = parse(bytes, i);
            if (item == null) {
                break;
            }

            items.add(item);
        }

        return items;
    }

    private static BeaconItem parse(byte[] bytes, int startIndex) {
        BeaconItem item = null;
        if (bytes.length - startIndex >= 2) {
            byte length = bytes[startIndex];
            if (length > 0) {
                byte type = bytes[startIndex + 1];
                int firstIndex = startIndex + 2;
                if (firstIndex < bytes.length) {
                    item = new BeaconItem();
                    int endIndex = firstIndex + length - 2;
                    if (endIndex >= bytes.length) {
                        endIndex = bytes.length - 1;
                    }

                    item.type = type & 255;
                    item.len = length;
                    item.bytes = ByteUtil.getBytes(bytes, firstIndex, endIndex);
                }
            }
        }

        return item;
    }
}