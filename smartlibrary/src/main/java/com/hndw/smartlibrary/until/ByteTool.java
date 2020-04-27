package com.hndw.smartlibrary.until;

import java.io.UnsupportedEncodingException;

public class ByteTool {
    public static int bytes2Int(byte[] bytes) {
        int int1 = bytes[0] & 0xff;
        int int2 = (bytes[1] & 0xff) << 8;
        int int3 = (bytes[2] & 0xff) << 16;
        int int4 = (bytes[3] & 0xff) << 24;
        return int1 | int2 | int3 | int4;
    }

    public static byte[] int2Bytes(int integer) {
        byte[] bytes = new byte[4];
        bytes[3] = (byte) (integer >> 24);
        bytes[2] = (byte) (integer >> 16);
        bytes[1] = (byte) (integer >> 8);
        bytes[0] = (byte) integer;
        return bytes;
    }

    public static byte[] short2byte(short s) {
        byte[] b = new byte[2];
        for (int i = 0; i < 2; i++) {
            int offset = 16 - (i + 1) * 8; //因为byte占4个字节，所以要计算偏移量
            b[i] = (byte) ((s >> offset) & 0xff); //把16位分为2个8位进行分别存储
        }
        return b;
    }

    public static short byte2short(byte[] b) {
        short lt = 0;
        for (int i = 0; i < 2; i++) {
            lt <<= 8; //<<=和我们的 +=是一样的，意思就是 l = l << 8
            lt |= (b[i] & 0xff); //和上面也是一样的  l = l | (b[i]&0xff)
        }
        return lt;
    }


    public static String convertStringToHex(String str) {

        char[] chars = str.toCharArray();

        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            hex.append(Integer.toHexString((int) chars[i]));
        }

        return hex.toString();
    }

    public static String convertHexToString(String hex) {

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < hex.length() - 1; i += 2) {
            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char) decimal);
            temp.append(decimal);
        }
        return sb.toString();
    }

    public static int hexCharToInt(char c) {
        if ((c >= '0') && (c <= '9'))
            return (c - '0');
        if ((c >= 'A') && (c <= 'F'))
            return (c - 'A' + 10);
        if ((c >= 'a') && (c <= 'f'))
            return (c - 'a' + 10);

        throw new RuntimeException("invalid hex char '" + c + "'");
    }

    public static String bytesToAscii(byte[] bytes, int offset, int dateLen) {
        if ((bytes == null) || (bytes.length == 0) || (offset < 0) || (dateLen <= 0)) {
            return null;
        }
        if ((offset >= bytes.length) || (bytes.length - offset < dateLen)) {
            return null;
        }

        String asciiStr = null;
        byte[] data = new byte[dateLen];
        System.arraycopy(bytes, offset, data, 0, dateLen);
        try {
            asciiStr = new String(data, "ISO8859-1");
        } catch (UnsupportedEncodingException e) {
        }
        return asciiStr;
    }

    public static String bytesToAscii(byte[] bytes, int dateLen) {
        return bytesToAscii(bytes, 0, dateLen);
    }

    public static String bytesToAscii(byte[] bytes) {
        return bytesToAscii(bytes, 0, bytes.length);
    }

    public static String bytesToHexString(byte[] bytes, int offset, int len) {
        if (bytes == null)
            return "null!";

        StringBuilder ret = new StringBuilder(2 * len);

        for (int i = 0; i < len; ++i) {
            int b = 0xF & bytes[(offset + i)] >> 4;
            ret.append("0123456789abcdef".charAt(b));
            b = 0xF & bytes[(offset + i)];
            ret.append("0123456789abcdef".charAt(b));
            // ret.append(" ");
        }

        return ret.toString();
    }

    public static String bytesToHexString(byte[] bytes, int len) {
        return ((bytes == null) ? "null!" : bytesToHexString(bytes, 0, len));
    }

    public static String bytesToHexString(byte[] bytes) {
        return ((bytes == null) ? "null!" : bytesToHexString(bytes, bytes.length));
    }

    /**
     * To byte array byte [ ].
     *
     * @param hexString the hex string
     * @return the byte [ ]
     */
    public static byte[] toByteArray(String hexString) {
        if (null == hexString)
            return null;
        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() >> 1];
        int index = 0;
        for (int i = 0; i < hexString.length(); i++) {
            if (index >= hexString.length() - 1)
                return byteArray;
            byte highDit = (byte) (Character.digit(hexString.charAt(index), 16) & 0xFF);
            byte lowDit = (byte) (Character.digit(hexString.charAt(index + 1), 16) & 0xFF);
            byteArray[i] = (byte) (highDit << 4 | lowDit);
            index += 2;
        }
        return byteArray;
    }

    public static double bytes2Double(byte[] arr) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value |= ((long) (arr[i] & 0xff)) << (8 * i);
        }
        return Double.longBitsToDouble(value);
    }

    public static float bytes2Float(byte[] data) {
        int value = bytes2Int(data);
        return Float.intBitsToFloat(value);
    }

    public static float byte2float(byte[] b, int index) {
        int l;
        l = b[index + 0];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 2] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 3] << 24);
        return Float.intBitsToFloat(l);
    }
}
