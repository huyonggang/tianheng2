package com.tianheng.client.model.event;

import com.tianheng.client.model.bean.MemberBean;

/**
 * Created by huyg on 2018/3/17.
 */

public class MemberEvent {
    public MemberBean memberBean;

    public MemberEvent(MemberBean memberBean) {
        this.memberBean = memberBean;
    }
}
