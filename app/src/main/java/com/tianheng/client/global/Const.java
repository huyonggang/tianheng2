package com.tianheng.client.global;

/**
 * Created by huyg on 2017/12/20.
 */

public class Const {

    public static final String BASE_URL = "https://tianheng.xlxiot.com/";
    public static final String BASE_PORT = "9999";
    public static final String BASE_IP = "116.62.202.206";


    /**
     * 电池柜操作Action
     */

    //打开柜子
    public static final String REQ_OPEN_DOOR = "com.hzjubu.action.REQ_OPEN_DOOR";
    public static final String ACK_OPEN_DOOR = "com.hzjubu.action.ACK_OPEN_DOOR";

    //柜子当前的状态
    public static final String REQ_DOOR_STATUS = "com.hzjubu.action.REQ_DOOR_STATUS";
    public static final String ACK_DOOR_STATUS = "com.hzjubu.action.ACK_DOOR_STATUS";

    //所有柜子的当前状态
    public static final String REQ_DOORS_STATUS = "com.hzjubu.action.REQ_DOORS_STATUS";
    public static final String ACK_DOORS_STATUS = "com.hzjubu.action.ACK_DOORS_STATUS";

    //获取指定副机指定箱格物检状态
    public static final String ACK_GOODS_STATUS = "com.hzjubu.action.ACK_GOODS_STATUS";
    public static final String REQ_GOODS_STATUS = "com.hzjubu.action.REQ_GOODS_STATUS";
    //获取指定副机所有箱格物检状态
    public static final String ACK_GOODSES_STATUS = "com.hzjubu.action.ACK_GOODSES_STATUS";
    public static final String REQ_GOODSES_STATUS = "com.hzjubu.action.REQ_GOODSES_STATUS";


    public static final String FRAME_80 = "80";
    public static final String FRAME_78 = "783C";

    public static final int LOW_VOLTAGE = 2700;
    public static final int HEIGHT_VOLTAGE = 4300;

    public static final int LOW_T = 5;
    public static final int HEIGHT_T = 65;


}
