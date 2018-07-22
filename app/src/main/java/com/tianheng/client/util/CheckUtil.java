package com.tianheng.client.util;

import com.tianheng.client.global.Const;
import com.tianheng.client.model.frame.BMSFrame;

/**
 * Created by huyg on 2018/3/16.
 * 电压3375
 * 温度5~40
 */

public class CheckUtil {

    public static boolean checkBattery(BMSFrame bmsFrame) {
        if (!checkVoltage(bmsFrame.voltage1)) {
            return false;
        }
        if (!checkVoltage(bmsFrame.voltage2)) {
            return false;
        }
        if (!checkVoltage(bmsFrame.voltage3)) {
            return false;
        }
        if (!checkVoltage(bmsFrame.voltage4)) {
            return false;
        }
        if (!checkVoltage(bmsFrame.voltage5)) {
            return false;
        }
        if (!checkVoltage(bmsFrame.voltage6)) {
            return false;
        }
        if (!checkVoltage(bmsFrame.voltage7)) {
            return false;
        }
        if (!checkVoltage(bmsFrame.voltage8)) {
            return false;
        }
        if (!checkVoltage(bmsFrame.voltage9)) {
            return false;
        }
        if (!checkVoltage(bmsFrame.voltage10)) {
            return false;
        }
        if (!checkVoltage(bmsFrame.voltage11)) {
            return false;
        }
        if (!checkVoltage(bmsFrame.voltage12)) {
            return false;
        }
        if (!checkVoltage(bmsFrame.voltage13)) {
            return false;
        }
        if (!checkVoltage(bmsFrame.voltage14)) {
            return false;
        }
        if (!checkVoltage(bmsFrame.voltage15)) {
            return false;
        }
        if (!checkVoltage(bmsFrame.voltage16)) {
            return false;
        }
        if (!checkT(bmsFrame.bmsT1)){
            return false;
        }
        if (!checkT(bmsFrame.bmsT2)){
            return false;
        }
        if (!checkT(bmsFrame.batterieT1)){
            return false;
        }
        if (!checkT(bmsFrame.batterieT2)){
            return false;
        }
        if (!checkCapacity(bmsFrame.capacity)){
            return false;
        }
        if (!checkCapacity(bmsFrame.capacitySum)){
            return false;
        }

        return true;
    }

    public static boolean checkVoltage(String voltage) {
        int num = Integer.parseInt(voltage, 16);
        return num > Const.LOW_VOLTAGE;
    }

    public static boolean checkT(String temperature) {
        int num = Integer.parseInt(temperature, 16);
        if (num > Const.LOW_T && num < Const.HEIGHT_T) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkCapacity(String capacity) {
        int num = Integer.parseInt(capacity, 16);
        return num != 0;
    }

}
