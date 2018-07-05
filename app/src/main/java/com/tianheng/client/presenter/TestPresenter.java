package com.tianheng.client.presenter;

import com.tianheng.client.base.RxPresenter;
import com.tianheng.client.presenter.contract.TestContract;

import javax.inject.Inject;

/**
 * Created by huyg on 2018/1/29.
 */

public class TestPresenter extends RxPresenter<TestContract.View> implements TestContract.Presenter{

    @Inject
    public TestPresenter(){

    }
}
