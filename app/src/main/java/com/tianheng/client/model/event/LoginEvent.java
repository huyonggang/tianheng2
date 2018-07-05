package com.tianheng.client.model.event;

import com.tianheng.client.model.bean.UserBean;

/**
 * Created by huyg on 2018/1/29.
 */

public class LoginEvent {

    public UserBean userBean;

    public LoginEvent(UserBean userBean) {
        this.userBean = userBean;
    }
}
