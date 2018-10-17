package com.tianheng.client.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by huyg on 2018/10/16.
 */
public class DateUtil {
    /**
     * 获取当前时间
     */
    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        return str;
    }
}
