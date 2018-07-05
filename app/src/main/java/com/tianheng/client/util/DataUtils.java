package com.tianheng.client.util;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DataUtils {

    /**
     *  普通字符转换成16进制字符串
     * @param str
     * @return
     */
    public static String str2HexStr(String str)
    {
        byte[] bytes = str.getBytes();
        // 如果不是宽类型的可以用Integer
        BigInteger bigInteger = new BigInteger(1, bytes);
        return bigInteger.toString(16);
    }

    /** 16进制的字符串转换成16进制字符串数组
     * @param src
     * @return
     */
    public static byte[] HexString2Bytes(String src) {
        int len = src.length() / 2;
        byte[] ret = new byte[len];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < len; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    /*
    * 字节数组转16进制字符串显示
    */
    public static String bytes2HexString(byte[] b, int length) {
        String r = "";

        for (int i = 0; i < length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            r += hex.toUpperCase();
        }

        return r;
    }

    /**
     * 计算校验码
     *
     * @param data
     * @param start
     * @param end
     * @return
     */
    public static byte checkData(byte[] data, int start, int end) {
        if (data == null || data.length < end || start >= end || start < 0)
            return 0;
        int sum = 0;
        for (int i = start; i < end; i++) {
            sum += (data[i] & 0xFF);
        }
        return (byte) (sum % 256);
    }

    public static byte[] hexStringToByteArray(String s)
    {
        byte data[] = new byte[s.length() / 2];
        for (int i = 0; i < s.length(); i += 2)
        {
            data[i / 2] = (Integer.decode("0x" + s.charAt(i) + s.charAt(i + 1))).byteValue();
        }
        return data;
    }


}
