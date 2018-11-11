package com.tianheng.client.presenter.contract;

import com.tianheng.client.base.BasePresenter;
import com.tianheng.client.base.BaseView;
import com.tianheng.client.model.bean.ExchangeBean;
import com.tianheng.client.model.bean.MemberBean;
import com.tianheng.client.model.bean.OrderBean;
import com.tianheng.client.model.bean.SubscribeBean;
import com.tianheng.client.model.bean.UserBean;

import java.util.List;

/**
 * Created by huyg on 2017/12/26.
 */

public interface OperateContract {

    interface View extends BaseView {

        void openDoor(ExchangeBean exchangeBean);
        void closeOldSuccess();
        void closeOldFail(int box);

        void closeNewSuccess();
        void closeNewFail(int box);
        void closeDialog();
        void showDialog(String message);
        void showQRImg(String url);
        void loginSuccess(UserBean userBean);
        void subscribeSuccess(SubscribeBean subscribeBean);
        void logoutSuccess();
    }

    interface Presenter extends BasePresenter<View>{

        void exchange();
        void closeNew(int exchangeBoxNumber,String exchangeBatteryNumber);
        void speak(String content);
        void closeOld(String leaseBatteryNumber,int emptyBoxNumber);
        void getQRCode(String cabinetNumber,int imgWidth,int imgHeight,String imgType);
        void login(String phone,String code);
        void getCode(String phone);
        void subscribeCode(String code);
        void logout(String ticket);
    }
}
