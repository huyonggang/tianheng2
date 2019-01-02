package com.tianheng.client.util;

/**
 * Created by huyg on 2018/3/16.
 */

public class EncodeFrame {

    public static byte[] selectFirst(int deviceNo) {
        StringBuilder builder = new StringBuilder();
        builder.append("1801");
        builder.append(getDeviceNo(deviceNo));
        builder.append("78061810005A0001");
        return DataUtils.HexString2Bytes(builder.toString());
    }

    //解锁放电
    public static byte[] discharge(){
        String frame = "78061810005A";
        return DataUtils.HexString2Bytes(frame);
    }

    public static String getDeviceNo(int deviceNo) {
        return "000"+deviceNo;
    }

    public static byte[] selectByPageNo(int deviceNo, String pageNo) {
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


    public static byte[] selectEnd(int deviceNo) {
        return selectByPageNo(deviceNo, "FF");
    }

    //取电池 18 img1 00 02 78 06 18 1E 06 46 00 img1
    public static byte[] takeOut(int deviceNo) {
        StringBuilder builder = new StringBuilder();
        builder.append("1801");
        builder.append(getDeviceNo(deviceNo));
        builder.append("7806181E06460001");
        return DataUtils.HexString2Bytes(builder.toString());
    }

    //放电池 18 img1 00 02 78 06 18 1E 08 44 00 img1
    public static byte[] putIn(int deviceNo) {
        StringBuilder builder = new StringBuilder();
        builder.append("1801");
        builder.append(getDeviceNo(deviceNo));
        builder.append("7806181E08440001");
        return DataUtils.HexString2Bytes(builder.toString());
    }


}
