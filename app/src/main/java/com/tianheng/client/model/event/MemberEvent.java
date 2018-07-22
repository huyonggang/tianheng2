package com.tianheng.client.model.event;

import com.tianheng.client.model.bean.UserMemberBean;

/**
 * Created by huyg on 2018/3/17.
 */

public class MemberEvent {
    public UserMemberBean memberBean;

    public MemberEvent(UserMemberBean memberBean) {
        this.memberBean = memberBean;
    }
}
