package com.lefu.test260h.util;



public class BleSearchBroadcastHelper {

    private static int advDataType = 0xFF;
    private static int connectAbledType = 0x01;

    /**
     * 0201050502F0FF121814FF64FF884A188031A0CF000092040000000101590E094865616C7468205363616C65370000000000000000000000000000000000
     * @param scanRecord
     * @return
     */
    public static BroadcastData analysiBroadcastDataNormal(byte[] scanRecord) {
        Beacon beacon = new Beacon(scanRecord);
        BroadcastData broadcastData = new BroadcastData();
        for (BeaconItem beaconItem : beacon.mItems) {
            if (beaconItem.type == connectAbledType) {
                if (beaconItem.bytes != null && beaconItem.bytes.length > 0) {
                    broadcastData.connectAbled = beaconItem.bytes[0] == 0x06;
                }
            }
            if (beaconItem.type == advDataType) {
                broadcastData.beacondata = beaconItem.bytes;
                broadcastData.dataLen = beaconItem.len - 1;
            }
        }
        return broadcastData;
    }

    public static class BroadcastData {
        public boolean connectAbled = false;
        public byte[] beacondata = new byte[]{};

        public int dataLen = 0;

    }

}


