package com.tianheng.client.broad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.tianheng.client.global.Const;
import com.tianheng.client.model.event.DoorErrorEvent;
import com.tianheng.client.model.event.DoorStatusEvent;
import com.tianheng.client.model.event.DoorsStatusEvent;
import com.tianheng.client.model.event.OpenDoorEvent;
import com.tianheng.client.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huyg on 2017/12/26.
 * 电池柜操作receiver
 */

public class CabinetBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        int errorCode = intent.getIntExtra("iErrorCode", -1);
        boolean isOpen=intent.getBooleanExtra("bOpend",false);
        int iLockId = intent.getIntExtra("iLockId",-1);
        int iBoardId = intent.getIntExtra("iBoardId",-1);
        if (errorCode < 0) {
            EventBus.getDefault().post(new DoorErrorEvent());
        } else {
            switch (action) {
                case Const.ACK_OPEN_DOOR:

                    //打开状态
                    EventBus.getDefault().post(new OpenDoorEvent(isOpen,iLockId,iBoardId));
//                    if (isOpen){
//
//                    }
                    break;
                case Const.ACK_DOOR_STATUS:
                    //打开状态
                    EventBus.getDefault().post(new DoorStatusEvent(isOpen,iLockId,iBoardId));
                    break;
                case Const.ACK_DOORS_STATUS:
                    Bundle b= intent.getExtras();
                    List<Integer> opendArray = b.getIntegerArrayList("iOpendArray");
                    EventBus.getDefault().post(new DoorsStatusEvent(opendArray));
                    break;
            }
        }
    }
}
