package com.tianheng.client.model.event;

/**
 * Created by huyg on 2018/1/6.
 */

public class OpenDoorEvent {

    public boolean isOpen;
    public int iLockId;
    public int iBoardId;

    public OpenDoorEvent(boolean isOpen, int iLockId, int iBoardId) {
        this.isOpen = isOpen;
        this.iLockId = iLockId;
        this.iBoardId = iBoardId;
    }
}
