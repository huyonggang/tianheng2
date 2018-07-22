package com.tianheng.client.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import com.tianheng.client.global.Const;
import com.tianheng.client.model.frame.BatteryFrame;
import com.tianheng.client.model.frame.ForwardFrame;
import com.tianheng.client.port.SerialPortUtil;
import com.tianheng.client.util.DataUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by huyg on 2018/2/4.
 */

public class SerialPortService extends Service implements SerialPortUtil.OnDataReceiveListener {
    private SerialPortUtil mSerial;
    private MyBind mBind = new MyBind();
    private int destPos;
    private StringBuffer frameBuffer = new StringBuffer();
    private static final String HEAD = "18FF";
    private MyThread mThread = new MyThread();
    private MyHandler mHandler = new MyHandler();
    private static final String TAG = "SerialPortService";
    private boolean threadTag = true;
    private String lastFrame;
    private BatteryFrame batteryFrame = new BatteryFrame();
    private ForwardFrame forwardFrame = new ForwardFrame();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBind;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mSerial == null) {
            mSerial = SerialPortUtil.getInstance();
            mSerial.setOnDataReceiveListener(this);
        }
        mThread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDataReceive(byte[] buffer, int size) {
        byte[] data = new byte[size];
        System.arraycopy(buffer, 0, data, 0, size);
        String frame = DataUtils.bytes2HexString(data, data.length);
        frameBuffer.append(frame);
    }


    class MyThread extends Thread {
        @Override
        public void run() {
            while (threadTag) {
                int index = frameBuffer.indexOf(HEAD);
                if (index > 0) {
                    frameBuffer.delete(0, index);
                } else {
                    if (frameBuffer.length() > 30) {
                        int twoIndex = frameBuffer.indexOf(HEAD, 1);//第二个头的位置
                        if (twoIndex > 0 && frameBuffer.length() > twoIndex) {
                            String frame = frameBuffer.substring(0, twoIndex);
                            if (!frame.equals(lastFrame)) {
                                Message message = Message.obtain();
                                message.obj = frame;
                                mHandler.sendMessage(message);
                                lastFrame = frame;
                            } else {
                                frameBuffer.delete(0, twoIndex);
                            }
                        }
                    }
                }
            }
        }
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            String realFrame = (String) msg.obj;
            Log.d(TAG, "realFrame--->" + realFrame);
            if (realFrame.length() == 24) {
                if (Const.FRAME_80.equals(realFrame.substring(8, 10))) {
                    batteryFrame.head = realFrame.substring(0, 4);
                    batteryFrame.device = realFrame.substring(4, 8);
                    batteryFrame.fixed = realFrame.substring(8, 10);
                    batteryFrame.voltage = realFrame.substring(10, 14);
                    batteryFrame.electric = realFrame.substring(14, 18);
                    batteryFrame.status = realFrame.substring(18, 20);
                    batteryFrame.bms = realFrame.substring(20, 22);
                    batteryFrame.keep = realFrame.substring(22, 24);
                    EventBus.getDefault().post(batteryFrame);
                } else {
                    forwardFrame.head = realFrame.substring(0, 4);
                    forwardFrame.device = realFrame.substring(4, 8);
                    forwardFrame.packNo = realFrame.substring(8, 10);
                    forwardFrame.data = realFrame.substring(10, realFrame.length());
                    EventBus.getDefault().post(forwardFrame);
                }
            }

        }
    }


    public class MyBind extends Binder {

        public SerialPortService getService() {
            return SerialPortService.this;
        }
    }


    public boolean sendData(byte[] buffer) {
        return mSerial.sendData(buffer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSerial.closeSerialPort();
        threadTag = false;
        mHandler.removeCallbacks(mThread);
    }
}
