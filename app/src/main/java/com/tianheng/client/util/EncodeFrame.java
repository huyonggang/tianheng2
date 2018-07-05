package com.tianheng.client.util;

/**
 * Created by huyg on 2018/3/16.
 */

public class EncodeFrame {

    public static byte[] selectFirst(String deviceNo) {
        StringBuilder builder = new StringBuilder();
        builder.append("1801");
        builder.append(getDeviceNo(deviceNo));
        builder.append("7806181E18340001");
        return DataUtils.HexString2Bytes(builder.toString());
    }

    public static String getDeviceNo(String deviceNo) {
        int length = deviceNo.length();
        if (length == 1) {
            return "000" + deviceNo;
        } else if (length == 2) {
            return "00" + deviceNo;
        } else if (length == 3) {
            return "0" + deviceNo;
        } else {
            return deviceNo;
        }
    }

    public static byte[] selectByPageNo(String deviceNo, String pageNo) {
        StringBuilder builder = new StringBuilder();
        builder.append("1806E5F4");
        builder.append("FFFFFFFF00");
        builder.append(getDeviceNo(deviceNo));
        if (pageNo.length() == 1) {
            pageNo = "0" + pageNo;
        }
        builder.append(pageNo);
        return DataUtils.HexString2Bytes(builder.toString());
    }


    public static byte[] selectEnd(String deviceNo) {
        return selectByPageNo(deviceNo, "FF");
    }

    //取电池 18 01 00 02 78 06 18 1E 06 46 00 01
    public static byte[] takeOut(String deviceNo) {
        StringBuilder builder = new StringBuilder();
        builder.append("1801");
        builder.append(getDeviceNo(deviceNo));
        builder.append("7806181E06460001");
        return DataUtils.HexString2Bytes(builder.toString());
    }

    //放电池 18 01 00 02 78 06 18 1E 08 44 00 01
    public static byte[] putIn(String deviceNo) {
        StringBuilder builder = new StringBuilder();
        builder.append("1801");
        builder.append(getDeviceNo(deviceNo));
        builder.append("7806181E08440001");
        return DataUtils.HexString2Bytes(builder.toString());
    }


}
