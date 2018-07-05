package com.tianheng.client.model.event;

/**
 * Created by huyg on 2018/1/6.
 */

public class DoorStatusEvent {

    public boolean isOpen;
    public int iLockId;
    public int iBoardId;

    public DoorStatusEvent(boolean isOpen, int iLockId, int iBoardId) {
        this.isOpen = isOpen;
        this.iLockId = iLockId;
        this.iBoardId = iBoardId;
    }
}
