package com.lefu.test260h.util;

import android.text.TextUtils;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ByteUtil {

    public static final byte[] EMPTY_BYTES = new byte[0];

    public static byte[] getNonEmptyByte(byte[] bytes) {
        return bytes != null ? bytes : EMPTY_BYTES;
    }

    /*16进制字符串转10进制int*/
    public static int hexToTen(String hex) {
        if (TextUtils.isEmpty(hex)) {
            return 0;
        }
        return Integer.valueOf(hex, 16);
    }

    public static Long hexToTenL(String hex) {
        if (TextUtils.isEmpty(hex)) {
            return 0L;
        }
        return Long.valueOf(hex, 16);
    }

    /*ElectronicScale异或检验*/
    public static byte[] getXorForElectronicScale(byte[] datas) {

        byte[] bytes = new byte[datas.length + 1];
        byte temp = datas[1];
        bytes[0] = datas[0];
        bytes[1] = datas[1];
        for (int i = 2; i < datas.length; i++) {
            bytes[i] = datas[i];
            temp ^= datas[i];
        }
        bytes[datas.length] = temp;
        return bytes;
    }

    /*异或检验*/
    public static byte[] getXor(byte[] datas) {

        byte[] bytes = new byte[datas.length + 1];
        byte temp = datas[0];
        bytes[0] = datas[0];
        for (int i = 1; i < datas.length; i++) {
            bytes[i] = datas[i];
            temp ^= datas[i];
        }
        bytes[datas.length] = temp;
        return bytes;
    }

    /*异或检验*/
    public static byte getXorValue(byte[] datas) {

        byte[] bytes = new byte[datas.length + 1];
        byte temp = datas[0];
        bytes[0] = datas[0];
        for (int i = 1; i < datas.length; i++) {
            bytes[i] = datas[i];
            temp ^= datas[i];
        }
//        bytes[datas.length] = temp;
        return temp;
    }

    /*异或检验*/
    public static boolean isXorValue(String checkString, String xorString) {
        byte[] bytes = ByteUtil.stringToBytes(checkString);
        byte temp = bytes[0];
        bytes[0] = bytes[0];
        for (int i = 1; i < bytes.length; i++) {
            bytes[i] = bytes[i];
            temp ^= bytes[i];
        }
        return String.format("%02X", temp).equals(xorString);
    }


    public static String decimal2Hex(long decimal) {
        String hex = "";
        String letter;
        int number;
        for (int i = 0; i < 9; i++) {
            number = (int) (decimal % 16);
            decimal = decimal / 16;
            switch (number) {
                case 10:
                    letter = "A";
                    break;
                case 11:
                    letter = "B";
                    break;
                case 12:
                    letter = "C";
                    break;
                case 13:
                    letter = "D";
                    break;
                case 14:
                    letter = "E";
                    break;
                case 15:
                    letter = "F";
                    break;
                default:
                    letter = String.valueOf(number);
            }
            hex = letter + hex;
            if (decimal == 0) {
                break;
            }
        }
        if (hex.length() % 2 != 0) {
            hex = "0" + hex;
        }
        return hex;
    }

    public static String decimal2Hex(int decimal) {
        String hex = "";
        String letter;
        int number;
        for (int i = 0; i < 9; i++) {
            number = decimal % 16;
            decimal = decimal / 16;
            switch (number) {
                case 10:
                    letter = "A";
                    break;
                case 11:
                    letter = "B";
                    break;
                case 12:
                    letter = "C";
                    break;
                case 13:
                    letter = "D";
                    break;
                case 14:
                    letter = "E";
                    break;
                case 15:
                    letter = "F";
                    break;
                default:
                    letter = String.valueOf(number);
            }
            hex = letter + hex;
            if (decimal == 0) {
                break;
            }
        }
        if (hex.length() % 2 != 0) {
            hex = "0" + hex;
        }
        return hex;
    }

    /**
     * 字符串转换为16进制字符串
     */
    public static String stringToHexString(String s) {
        if (s == null || s.isEmpty()) return "";
        byte[] bytes = s.getBytes();
        String str = "";
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            str += hex.toUpperCase();
        }
        return str;
    }

    /**
     * 16进制转换成为string类型字符串
     *
     * @param s
     * @return
     */
    public static String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        s = getTailOffString(s);
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                String hex = s.substring(i * 2, i * 2 + 2);
                baKeyword[i] = (byte) (0xff & Integer.parseInt(hex, 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "UTF-8");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    /**
     * 去尾
     *
     * @param s
     * @return
     */
    @NotNull
    private static String getTailOffString(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (i % 2 == 0) {
                String hex = s.substring(i, i + 2);
                if (hex.equals("00")) {
                    return s.substring(0, i);
                }
            }
        }
        return s;
    }

    public static String byteArrayToStr(byte[] buffer) {
        try {
            int length = 0;
            for (int i = 0; i < buffer.length; ++i) {
                if (buffer[i] == 0) {
                    length = i;
                    break;
                }
            }
            return new String(buffer, 0, length, "UTF-8");
        } catch (Exception e) {
//            Logger.e(e.toString());
            return "";
        }
    }

    public static String byteToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        if (!isEmpty(bytes)) {
            for (int i = 0; i < bytes.length; ++i) {
                sb.append(String.format("%02X", bytes[i]));
            }
        }
        return sb.toString();
    }

    public static String byteToStringHex(byte b) {
        return String.format("%02X", b);
    }

    static String[] hexStr = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};

    /**
     * 二进制字符转16进制字符
     *
     * @param
     * @return
     */
    public static String binaryToHexString(String binary) {
        //00101000000000000000000111111111111
        int length = binary.length();
        int temp = length % 4;
        // 每四位2进制数字对应一位16进制数字
        // 补足4位
        if (temp != 0) {
            for (int i = 0; i < 4 - temp; i++) {
                binary = "0" + binary;
            }
        }
        // 重新计算长度
        length = binary.length();
        StringBuilder sb = new StringBuilder();
        // 每4个二进制数为一组进行计算
        for (int i = 0; i < length / 4; i++) {
            int num = 0;
            // 将4个二进制数转成整数
            for (int j = i * 4; j < i * 4 + 4; j++) {
                num <<= 1;// 左移
                num |= (binary.charAt(j) - '0');    // 或运算
            }
            // 直接找到该整数对应的16进制
            sb.append(hexStr[num]);
        }
        return sb.toString();
    }

    public static boolean isEmpty(byte[] bytes) {
        return bytes == null || bytes.length == 0;
    }


    public static byte[] trimLast(byte[] bytes) {
        int i;
        for (i = bytes.length - 1; i >= 0 && bytes[i] == 0; --i) {
        }

        return Arrays.copyOfRange(bytes, 0, i + 1);
    }

    public static byte[] stringToBytes(String text) {
        int len = text.length();
        byte[] bytes = new byte[(len + 1) / 2];

        for (int i = 0; i < len; i += 2) {
            int size = Math.min(2, len - i);
            String sub = text.substring(i, i + size);
            bytes[i / 2] = (byte) Integer.parseInt(sub, 16);
        }

        return bytes;
    }


    public static byte[] fromInt(int n) {
        byte[] bytes = new byte[4];

        for (int i = 0; i < 4; ++i) {
            bytes[i] = (byte) (n >>> i * 8);
        }

        return bytes;
    }

    public static boolean byteEquals(byte[] lbytes, byte[] rbytes) {
        if (lbytes == null && rbytes == null) {
            return true;
        } else if (lbytes != null && rbytes != null) {
            int llen = lbytes.length;
            int rlen = rbytes.length;
            if (llen != rlen) {
                return false;
            } else {
                for (int i = 0; i < llen; ++i) {
                    if (lbytes[i] != rbytes[i]) {
                        return false;
                    }
                }

                return true;
            }
        } else {
            return false;
        }
    }

    public static byte[] fillBeforeBytes(byte[] bytes, int len, byte fill) {
        byte[] result = bytes;
        int oldLen = bytes != null ? bytes.length : 0;
        if (oldLen < len) {
            result = new byte[len];
            int i = len - 1;

            for (int j = oldLen - 1; i >= 0; --j) {
                if (j >= 0) {
                    result[i] = bytes[j];
                } else {
                    result[i] = fill;
                }

                --i;
            }
        }

        return result;
    }

    public static byte[] cutBeforeBytes(byte[] bytes, byte cut) {
        if (isEmpty(bytes)) {
            return bytes;
        } else {
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] != cut) {
                    return Arrays.copyOfRange(bytes, i, bytes.length);
                }
            }

            return EMPTY_BYTES;
        }
    }

    public static byte[] cutAfterBytes(byte[] bytes, byte cut) {
        if (isEmpty(bytes)) {
            return bytes;
        } else {
            for (int i = bytes.length - 1; i >= 0; --i) {
                if (bytes[i] != cut) {
                    return Arrays.copyOfRange(bytes, 0, i + 1);
                }
            }

            return EMPTY_BYTES;
        }
    }

    public static byte[] getBytes(byte[] bytes, int start, int end) {
        if (bytes == null) {
            return null;
        } else if (start >= 0 && start < bytes.length) {
            if (end >= 0 && end < bytes.length) {
                if (start > end) {
                    return null;
                } else {
                    byte[] newBytes = new byte[end - start + 1];

                    for (int i = start; i <= end; ++i) {
                        newBytes[i - start] = bytes[i];
                    }

                    return newBytes;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static int ubyteToInt(byte b) {
        return b & 255;
    }

    public static boolean isAllFF(byte[] bytes) {
        int len = bytes != null ? bytes.length : 0;

        for (int i = 0; i < len; ++i) {
            if (ubyteToInt(bytes[i]) != 255) {
                return false;
            }
        }

        return true;
    }

    public static byte[] fromLong(long n) {
        byte[] bytes = new byte[8];

        for (int i = 0; i < 8; ++i) {
            bytes[i] = (byte) ((int) (n >>> i * 8));
        }

        return bytes;
    }

    public static void copy(byte[] lbytes, byte[] rbytes, int lstart, int rstart) {
        if (lbytes != null && rbytes != null && lstart >= 0) {
            int i = lstart;

            for (int j = rstart; j < rbytes.length && i < lbytes.length; ++j) {
                lbytes[i] = rbytes[j];
                ++i;
            }
        }

    }

    /**
     * 大端转成小端，或小端转大端
     *
     * @param uidHex
     * @return
     */
    @NotNull
    public static String hexToLittleEndianMode(String uidHex) {
        if (uidHex == null || uidHex.isEmpty()) return "";
        List<String> arrayList = new ArrayList<>();
        for (int i = 0; i < uidHex.length() / 2; i++) {
            arrayList.add(uidHex.substring(i * 2, i * 2 + 2));
        }
        StringBuffer stringBuffer = new StringBuffer();
        Collections.reverse(arrayList);
        for (String s : arrayList) {
            stringBuffer.append(s);
        }
        return stringBuffer.toString();
    }

    /**
     * long转16进制字符串并且指定长度，长度不够默认补0
     *
     * @param memberid
     * @param i2
     * @return
     */
    @NotNull
    public static String longToHexAndSpecLen(long memberid, int i2) {
        StringBuffer buffer = new StringBuffer();
        String memberHex = ByteUtil.decimal2Hex(memberid);
        if (memberHex.length() < i2) {
            for (int i = 0; i < i2 - memberHex.length(); i++) {
                buffer.append("0");
            }
        }
        buffer.append(memberHex);
        return buffer.toString();
    }

    public static String autoPadZero(String hex, int maxLen) {
        StringBuffer stringBuffer = new StringBuffer();
        if (hex.length() < maxLen) {
            stringBuffer.append(hex);
            for (int i = 0; i < maxLen - hex.length(); i++) {
                stringBuffer.append("0");
            }
        } else if (hex.length() >= maxLen) {
            return hex.substring(0, maxLen);
        }
        return stringBuffer.toString();
    }

    /**
     * 自动在前面补0
     *
     * @param hex
     * @param maxLen
     * @return
     */
    public static String autoLeftPadZero(String hex, int maxLen) {
        StringBuffer stringBuffer = new StringBuffer();
        if (hex.length() < maxLen) {
            for (int i = 0; i < maxLen - hex.length(); i++) {
//                hex = "0" + hex;
                stringBuffer.append("0");
            }
        } else if (hex.length() > maxLen) {
            return hex.substring(0, maxLen);
        }
        stringBuffer.append(hex);
        return stringBuffer.toString();
    }

    /**
     * 根据mtu拆包
     *
     * @param userDataHex
     * @param mtu
     * @return
     */
    public static List<byte[]> subAccordToMTU(String userDataHex, int mtu) {
        if (userDataHex == null || userDataHex.isEmpty()) {
            return new ArrayList<>();
        }
        mtu = mtu - 2;
        mtu = mtu * 2;
        int dataNum = userDataHex.length() / mtu + (userDataHex.length() % mtu == 0 ? 0 : 1);

        List<byte[]> dataList = new ArrayList<>();
        for (int i = 0; i < dataNum; i++) {
            String item;
            if (i == dataNum - 1) {
                item = String.format("00%s%s", ByteUtil.decimal2Hex(i), userDataHex.substring(i * mtu));
            } else {
                item = String.format("00%s%s", ByteUtil.decimal2Hex(i), userDataHex.substring(i * mtu, (i + 1) * mtu));
            }
            byte[] bytes = ByteUtil.stringToBytes(item);
            dataList.add(bytes);
        }
        return dataList;
    }

    /**
     * 根据mtu拆包
     *
     * @param mtu
     * @return
     */
    public static List<byte[]> subAccordToMTU(byte[] data, int mtu) {
        if (data == null || data.length <= 0) {
            return new ArrayList<>();
        }
        mtu = mtu - 2;
//        Logger.v("subAccordToMTU data size :" + data.length + " mtu :" + mtu);
        int dataNum = data.length / mtu + (data.length % mtu == 0 ? 0 : 1);
        int surplusLen = data.length % mtu;
//        Logger.v("subAccordToMTU dataNum :" + dataNum + " surplusLen :" + surplusLen);
        List<byte[]> dataList = new ArrayList<>();
        for (int i = 0; i < dataNum; i++) {
            if (i < dataNum - 1) {
                byte[] bytes = new byte[mtu + 2];
                System.arraycopy(data, mtu * i, bytes, 2, mtu);
                byte indexByte = Byte.parseByte("" + i, 10);
                bytes[0] = 0;
                bytes[1] = indexByte;
                dataList.add(bytes);
                //                item = String.format("00%s%s", ByteUtil.decimal2Hex(i), data.substring(i * mtu, (i + 1) * mtu));
            } else if (surplusLen == 0){
                byte[] bytes = new byte[mtu + 2];
                System.arraycopy(data, mtu * i, bytes, 2, mtu);
                byte indexByte = Byte.parseByte("" + i, 10);
                bytes[0] = 0;
                bytes[1] = indexByte;
                dataList.add(bytes);
            } else {
                byte[] bytes = new byte[surplusLen + 2];
                System.arraycopy(data, mtu * i, bytes, 2, surplusLen);
                byte indexByte = Byte.parseByte("" + i, 10);
                bytes[0] = 0;
                bytes[1] = indexByte;
                dataList.add(bytes);
                //                item = String.format("00%s%s", ByteUtil.decimal2Hex(i), data.substring(i * mtu));
            }
//            byte[] bytes = ByteUtil.stringToBytes(item);

        }
        return dataList;
    }


}
