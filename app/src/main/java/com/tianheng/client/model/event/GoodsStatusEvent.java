package com.tianheng.client.model.event;

import java.util.List;

/**
 * Created by huyg on 2018/7/7.
 */
public class GoodsStatusEvent {




    public List<Integer> goodsList;
    public int iBoardId;
    public GoodsStatusEvent(List<Integer> goodsList, int iBoardId) {
        this.goodsList = goodsList;
        this.iBoardId = iBoardId;
    }
}
