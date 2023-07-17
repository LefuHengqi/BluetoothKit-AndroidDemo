package com.lefu.test260h.util;

public class BeaconItem {
    public int len;
    public int type;
    public byte[] bytes;

    public BeaconItem() {
    }

    public String toString() {
        String format = "";
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("@Len = %02X, @Type = 0x%02X", this.len, this.type));
        switch (this.type) {
            case 8:
            case 9:
                format = "%c";
                break;
            default:
                format = "%02X ";
        }

        sb.append(" -> ");
        StringBuilder sbSub = new StringBuilder();

        try {
            byte[] var4 = this.bytes;
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                byte b = var4[var6];
                sbSub.append(String.format(format, b & 255));
            }

            sb.append(sbSub.toString());
        } catch (Exception var8) {
            sb.append(ByteUtil.byteToString(this.bytes));
        }

        return sb.toString();
    }
}