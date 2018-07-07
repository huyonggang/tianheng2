package com.tianheng.client.model.event;

/**
 * Created by huyg on 2018/7/7.
 */
public class GoodStatusEvent {

    public boolean isGoods;
    public int iLockId;
    public int iBoardId;

    public GoodStatusEvent(boolean isGoods, int iLockId, int iBoardId) {
        this.isGoods = isGoods;
        this.iLockId = iLockId;
        this.iBoardId = iBoardId;
    }

}
