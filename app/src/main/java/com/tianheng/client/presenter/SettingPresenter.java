package com.tianheng.client.presenter;

import com.tianheng.client.base.RxPresenter;
import com.tianheng.client.model.http.ApiFactory;
import com.tianheng.client.presenter.contract.SettingContract;

import javax.inject.Inject;

/**
 * Created by huyg on 2018/1/19.
 */

public class SettingPresenter extends RxPresenter<SettingContract.View> implements SettingContract.Presenter{

    private ApiFactory mApiFactory;

    @Inject
    public SettingPresenter(ApiFactory apiFactory){

    }
}
