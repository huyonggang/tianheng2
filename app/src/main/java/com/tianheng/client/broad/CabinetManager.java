package com.tianheng.client.broad;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tianheng.client.global.Const;

/**
 * Created by huyg on 2018/1/3.
 */

public class CabinetManager {
    private Context mContext;

    private static final String BOARD_ID = "iBoardId";
    private static final String LOCK_ID = "iLockId";
    private static final String BOXESCOUNTS = "iBoxesCounts";

    public CabinetManager(Context context){
        mContext = context;
    }


    /**
     * 打开指定副机指定箱格
     * @param iBoardId 副机id
     * @param iLockId 箱门id
     */
    public void openDoor(int iBoardId,int iLockId){
        Intent intent = new Intent();
        intent.setAction(Const.REQ_OPEN_DOOR);
        intent.putExtra(BOARD_ID,iBoardId);
        intent.putExtra(LOCK_ID,iLockId);
        mContext.sendBroadcast(intent);
    }

    /**
     * 获取指定门的状态
     * @param iBoardId
     * @param iLockId
     */
    public void getDoorStatus(int iBoardId,int iLockId){
        Intent intent = new Intent();
        intent.setAction(Const.REQ_DOOR_STATUS);
        intent.putExtra(BOARD_ID,iBoardId);
        intent.putExtra(LOCK_ID,iLockId);
        mContext.sendBroadcast(intent);
    }

    /**
     * 获取指定门的物检
     * @param iBoardId
     * @param iLockId
     */
    public void getGoodStatus(int iBoardId,int iLockId){
        Intent intent = new Intent();
        intent.setAction(Const.REQ_GOODS_STATUS);
        intent.putExtra(BOARD_ID,iBoardId);
        intent.putExtra(LOCK_ID,iLockId);
        Log.d("CabinetBroadcast",Const.REQ_GOODS_STATUS);
        mContext.sendBroadcast(intent);
    }

    /**
     * 获取所有门的物检
     * @param iBoardId
     */
    public void getGoodsStatus(int iBoardId){
        Intent intent = new Intent();
        intent.setAction(Const.REQ_GOODSES_STATUS);
        intent.putExtra(BOARD_ID,iBoardId);
        intent.putExtra(BOXESCOUNTS,8);
        mContext.sendBroadcast(intent);
    }
}
