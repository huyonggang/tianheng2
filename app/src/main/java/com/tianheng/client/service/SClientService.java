package com.tianheng.client.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


import com.google.gson.Gson;
import com.tianheng.client.App;
import com.tianheng.client.global.Const;
import com.tianheng.client.model.bean.BatteryInfo;
import com.tianheng.client.model.frame.BMSFrame;
import com.tianheng.client.model.netty.SClientManager;
import com.tianheng.client.util.DataUtils;

/**
 * Created by huyg on 18/1/10.
 */
public class SClientService extends Service {

    private final String tag = getClass().getSimpleName();
    private MyBind mBind = new MyBind();


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(tag, "SClientService is creating");
        SClientManager.getInstance().init(getApplicationContext());
        Log.i(tag, "SClientService is starting");
        if (!SClientManager.getInstance().isRunning) {
            SClientManager.getInstance().start(Const.BASE_IP, Const.BASE_PORT);
        }
    }

    @Override
    public void onDestroy() {
        Log.i(tag, "SClientService is destroying");
        super.onDestroy();
        if (SClientManager.getInstance().isRunning)
            try {
                SClientManager.getInstance().stop();
            } catch (Exception e) {
                Log.e(tag, "SClient stop failed");
                e.printStackTrace();
            }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBind;
    }

    public class MyBind extends Binder {

        public SClientService getService() {
            return SClientService.this;
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }


    public void sendFrame(BMSFrame bmsFrame) {
        BatteryInfo batteryInfo = new BatteryInfo();
        batteryInfo.setType(1);
        batteryInfo.setCabinetNumber(App.getInstance().getImei());
        batteryInfo.setBatteryNumber(bmsFrame.code);
        batteryInfo.setBoxNumber(bmsFrame.pageNo);
        batteryInfo.setCurPower(getPower(bmsFrame));
        batteryInfo.setCurVoltage(sumVol(bmsFrame));
        //batteryInfo.setChargerStatus(resolveStatus(bmsFrame));
        Log.d(tag, new Gson().toJson(batteryInfo));
        String frame = new Gson().toJson(batteryInfo);
        SClientManager.getInstance().sendFrame(frame);
    }

    public void sendErrorFrame(String content) {
        BatteryInfo batteryInfo = new BatteryInfo();
        batteryInfo.setType(3);
        batteryInfo.setCabinetNumber(App.getInstance().getImei());
        batteryInfo.setContent(content);
        String frame = new Gson().toJson(batteryInfo);
//        SClientManager.getInstance().sendFrame(frame);
    }

    public int getPower(BMSFrame bmsFrame) {
        float capacity = Integer.parseInt(bmsFrame.capacity, 16);
        float capacitySum = Integer.parseInt(bmsFrame.capacitySum, 16);
        int power = (int) (capacity / capacitySum * 100);
        return power;
    }


    public int resolveStatus(BMSFrame bmsFrame) {
        int status = 0;
        byte[] bytes = new byte[32];
        bytes = DataUtils.hexStringToByteArray(bmsFrame.status);
        if (bytes[2] == 1) {
            sendErrorFrame("充电过流");
        } else if (bytes[3] == 1) {
            sendErrorFrame("电芯过压");
        } else if (bytes[5] == 1) {
            sendErrorFrame("短路");
        } else if (bytes[6] == 1) {
            sendErrorFrame("放电过流");
        } else if (bytes[7] == 1) {
            sendErrorFrame("电芯欠压");
        } else if (bytes[8] == 1) {
            sendErrorFrame("电芯侦测线开路");
        } else if (bytes[9] == 1) {
            sendErrorFrame("温感侦测线开路");
        } else if (bytes[10] == 1) {
            sendErrorFrame("电芯温度过高");
        } else if (bytes[11] == 1) {
            sendErrorFrame("电芯温度过低");
        }
        return status;
    }

    public double sumVol(BMSFrame bmsFrame) {
        double sum = 0;
        if (bmsFrame != null) {
            sum = Integer.parseInt(bmsFrame.voltage1, 16) +
                    Integer.parseInt(bmsFrame.voltage2, 16) +
                    Integer.parseInt(bmsFrame.voltage3, 16) +
                    Integer.parseInt(bmsFrame.voltage4, 16) +
                    Integer.parseInt(bmsFrame.voltage5, 16) +
                    Integer.parseInt(bmsFrame.voltage6, 16) +
                    Integer.parseInt(bmsFrame.voltage7, 16) +
                    Integer.parseInt(bmsFrame.voltage8, 16) +
                    Integer.parseInt(bmsFrame.voltage9, 16) +
                    Integer.parseInt(bmsFrame.voltage10, 16) +
                    Integer.parseInt(bmsFrame.voltage11, 16) +
                    Integer.parseInt(bmsFrame.voltage12, 16) +
                    Integer.parseInt(bmsFrame.voltage13, 16) +
                    Integer.parseInt(bmsFrame.voltage14, 16) +
                    Integer.parseInt(bmsFrame.voltage15, 16) +
                    Integer.parseInt(bmsFrame.voltage16, 16)
            ;
        }
        sum = sum / 1000.0;
        return sum;
    }

}
