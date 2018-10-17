package com.tianheng.client.presenter;

import com.tianheng.client.base.RxPresenter;
import com.tianheng.client.presenter.contract.LogContract;

import javax.inject.Inject;

/**
 * Created by huyg on 2018/10/16.
 */
public class LogPresenter extends RxPresenter<LogContract.View> implements LogContract.Presenter {


    @Inject
    public LogPresenter(){

    }
}
