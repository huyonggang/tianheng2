package com.tianheng.client.broad;

import android.content.Context;
import android.content.Intent;

import com.tianheng.client.global.Const;

/**
 * Created by huyg on 2018/1/3.
 */

public class CabinetManager {
    private Intent mIntent;
    private Context mContext;

    private static final String BOARD_ID = "iBoardId";
    private static final String LOCK_ID = "iLockId";
    private static final String BOXESCOUNTS = "iBoxesCounts";

    public CabinetManager(Context context){
        mIntent = new Intent();
        mContext = context;
    }


    public void sendBroadcast(){
        mContext.sendBroadcast(mIntent);
    }

    /**
     * 打开指定副机指定箱格
     * @param iBoardId 副机id
     * @param iLockId 箱门id
     */
    public void openDoor(int iBoardId,int iLockId){
        mIntent.setAction(Const.REQ_OPEN_DOOR);
        mIntent.putExtra(BOARD_ID,iBoardId);
        mIntent.putExtra(LOCK_ID,iLockId);
        sendBroadcast();
    }

    /**
     * 获取指定门的状态
     * @param iBoardId
     * @param iLockId
     */
    public void getDoorStatus(int iBoardId,int iLockId){
        mIntent.setAction(Const.REQ_DOOR_STATUS);
        mIntent.putExtra(BOARD_ID,iBoardId);
        mIntent.putExtra(LOCK_ID,iLockId);
        sendBroadcast();
    }


    /**
     * 获取指定副机所有门的状态
     * @param iBoardId
     * @param iBoxesCounts
     */
    public void getDoorsStatus(int iBoardId,int iBoxesCounts){
        mIntent.setAction(Const.REQ_DOORS_STATUS);
        mIntent.putExtra(BOARD_ID,iBoardId);
        mIntent.putExtra(BOXESCOUNTS,iBoxesCounts);
        sendBroadcast();
    }
    /**
     * 获取指定副机所在门的物品状态
     * @param iBoardId
     * @param lookId
     */
//    public void getGoodStatus(int iBoardId,int lookId){
//        mIntent.setAction(Const.REQ_GOODS_STATUS);
//        mIntent.putExtra(BOARD_ID,iBoardId);
//        mIntent.putExtra(LOCK_ID,lookId);
//        sendBroadcast();
//    }

    /**
     * 获取指定副机所有门的物品状态
     * @param iBoardId
     */
//    public void getGoodsStatus(int iBoardId){
//        mIntent.setAction(Const.REQ_GOODSES_STATUS);
//        mIntent.putExtra(BOARD_ID,iBoardId);
//        mIntent.putExtra(BOXESCOUNTS,8);
//        sendBroadcast();
//    }





}
