package com.tianheng.client.presenter;


import com.tianheng.client.base.RxPresenter;
import com.tianheng.client.model.http.ApiFactory;
import com.tianheng.client.presenter.contract.BatteryContract;

import javax.inject.Inject;

/**
 * Created by huyg on 2017/12/26.
 */

public class BatteryPresenter extends RxPresenter<BatteryContract.View> implements BatteryContract.Presenter{


    private ApiFactory mApiFactory;

    @Inject
    public BatteryPresenter(ApiFactory apiFactory){
        mApiFactory=apiFactory;
    }
}
