package com.tianheng.client.model.event;

import java.util.List;

/**
 * Created by huyg on 2018/1/30.
 */

public class DoorsStatusEvent {
    public List<Integer> opendArray;

    public DoorsStatusEvent(List<Integer> opendArray) {
        this.opendArray = opendArray;
    }
}
