package com.tianheng.client.presenter.contract;

import com.tianheng.client.base.BasePresenter;
import com.tianheng.client.base.BaseView;
import com.tianheng.client.model.bean.UserBean;

/**
 * Created by huyg on 2018/1/6.
 */

public interface UserContract {

    interface View extends BaseView {
        void showQRImg(String url);
        void loginSuccess(UserBean userBean);
    }

    interface Presenter extends BasePresenter<View>{
        void getQRCode(String cabinetNumber,int imgWidth,int imgHeight,String imgType);
        void login(String phone,String code);
        void getCode(String phone);
    }
}
