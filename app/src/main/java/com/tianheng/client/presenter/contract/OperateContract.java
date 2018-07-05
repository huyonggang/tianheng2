package com.tianheng.client.presenter.contract;

import com.tianheng.client.base.BasePresenter;
import com.tianheng.client.base.BaseView;
import com.tianheng.client.model.bean.ExchangeBean;

import java.util.List;

/**
 * Created by huyg on 2017/12/26.
 */

public interface OperateContract {

    interface View extends BaseView {

        void openDoor(ExchangeBean exchangeBean);
        void putOldSuccess();

        void logoutSuccess();
    }

    interface Presenter extends BasePresenter<View>{

        void exchange();
        void putInOld();
        void closeNew(String oldOrderId);
        void loginOut(String ticket);
        void takeoutOld();
        void speak(String content);
        void closeOld(int contain,String leaseBatteryNumber,int emptyBoxNumber,int exchangeBoxNumber,String exchangeBatteryNumber);
        void takeoutNew(String oldOrderId);
        void putInNew(String oldOrderId);
    }
}
