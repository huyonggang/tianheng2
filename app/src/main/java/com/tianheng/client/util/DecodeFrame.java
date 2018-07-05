package com.tianheng.client.util;

import com.tianheng.client.model.frame.BMSFrame;

/**
 * Created by huyg on 2018/3/16.
 */

public class DecodeFrame {

    public static BMSFrame decodeBmsFrame(String frame) {
        BMSFrame bmsFrame=null;
        if (frame.length() == 120) {
            bmsFrame = new BMSFrame();
            bmsFrame.head = frame.substring(0, 2);
            bmsFrame.length = frame.substring(2, 4);
            bmsFrame.order = frame.substring(4, 6);
            bmsFrame.features = frame.substring(6, 8);
            bmsFrame.features_ = frame.substring(8, 10);
            bmsFrame.times = frame.substring(10, 14);
            bmsFrame.code = frame.substring(14, 22);
            bmsFrame.status = frame.substring(22, 30);
            bmsFrame.electric = frame.substring(30, 34);
            bmsFrame.voltage1 = frame.substring(34, 38);
            bmsFrame.voltage2 = frame.substring(38, 42);
            bmsFrame.voltage3 = frame.substring(42, 46);
            bmsFrame.voltage4 = frame.substring(46, 50);
            bmsFrame.voltage5 = frame.substring(50, 54);
            bmsFrame.voltage6 = frame.substring(54, 58);
            bmsFrame.voltage7 = frame.substring(58, 62);
            bmsFrame.voltage8 = frame.substring(62, 66);
            bmsFrame.voltage9 = frame.substring(66, 70);
            bmsFrame.voltage10 = frame.substring(70, 74);
            bmsFrame.voltage11 = frame.substring(74, 78);
            bmsFrame.voltage12 = frame.substring(78, 82);
            bmsFrame.voltage13 = frame.substring(82, 86);
            bmsFrame.voltage14 = frame.substring(86, 90);
            bmsFrame.voltage15 = frame.substring(90, 94);
            bmsFrame.voltage16 = frame.substring(94, 98);
            bmsFrame.bmsT1 = frame.substring(98, 100);
            bmsFrame.bmsT2 = frame.substring(100, 102);
            bmsFrame.batterieT1 = frame.substring(102, 104);
            bmsFrame.batterieT2 = frame.substring(104, 106);
            bmsFrame.count = frame.substring(106, 110);
            bmsFrame.capacity = frame.substring(110, 114);
            bmsFrame.capacitySum = frame.substring(114, 118);
            bmsFrame.checkCode = frame.substring(118, 120);
            return bmsFrame;
        }
        return bmsFrame;
    }
}
