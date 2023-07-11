package com.lefu.test260h.util;

import java.util.LinkedList;
import java.util.List;

public class Beacon {
    public byte[] mBytes;
    public List<BeaconItem> mItems = new LinkedList();

    public Beacon(byte[] scanRecord) {
        if (!ByteUtil.isEmpty(scanRecord)) {
            this.mBytes = ByteUtil.trimLast(scanRecord);
            this.mItems.addAll(BeaconParser.parseBeacon(this.mBytes));
        }

    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("preParse: %s\npostParse:\n", ByteUtil.byteToString(this.mBytes)));

        for(int i = 0; i < this.mItems.size(); ++i) {
            sb.append(((BeaconItem)this.mItems.get(i)).toString());
            if (i != this.mItems.size() - 1) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }
}
