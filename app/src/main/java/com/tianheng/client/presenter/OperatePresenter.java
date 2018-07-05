package com.tianheng.client.presenter;

import android.text.TextUtils;

import com.tianheng.client.base.RxPresenter;
import com.tianheng.client.manage.TTSManage;
import com.tianheng.client.model.bean.ExchangeBean;
import com.tianheng.client.model.http.ApiFactory;
import com.tianheng.client.model.http.BaseHttpResponse;
import com.tianheng.client.model.http.HttpResponse;
import com.tianheng.client.presenter.contract.OperateContract;
import com.tianheng.client.util.rx.RxException;
import com.tianheng.client.util.rx.RxResult;
import com.tianheng.client.util.rx.RxSchedulers;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by huyg on 2017/12/26.
 */

public class OperatePresenter extends RxPresenter<OperateContract.View> implements OperateContract.Presenter {

    private ApiFactory mApiFactory;
    private TTSManage mTTSManage;

    @Inject
    public OperatePresenter(ApiFactory apiFactory, TTSManage ttsManage) {
        mApiFactory = apiFactory;
        mTTSManage = ttsManage;
    }


    @Override
    public void exchange() {
        Disposable disposable = mApiFactory.getOperateApi().exchange()
                .compose(RxSchedulers.io_main())
                .compose(RxResult.handleResult())
                .subscribe(new Consumer<ExchangeBean>() {
                    @Override
                    public void accept(ExchangeBean exchangeBean) throws Exception {
                        if (exchangeBean != null) {
                            mView.openDoor(exchangeBean);
                        }
                    }
                }, new RxException<>(e -> {
                    mView.showContent(e.getMessage());
                }));
        addDispose(disposable);
    }

    @Override
    public void putInOld() {
        Disposable disposable = mApiFactory.getOperateApi().putInOld()
                .compose(RxSchedulers.io_main())
                .subscribe(new Consumer<BaseHttpResponse>() {
                    @Override
                    public void accept(BaseHttpResponse baseHttpResponse) throws Exception {
                        if (baseHttpResponse.isSuccess()) {
                            mView.putOldSuccess();
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.showContent(throwable.getMessage());
                    }
                });
        addDispose(disposable);
    }

    @Override
    public void closeNew(String oldOrderId) {
        Disposable disposable = mApiFactory.getOperateApi().closeNew(oldOrderId)
                .compose(RxSchedulers.io_main())
                .subscribe(new Consumer<BaseHttpResponse>() {
                    @Override
                    public void accept(BaseHttpResponse baseHttpResponse) throws Exception {
                        if (baseHttpResponse.isSuccess()) {

                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.showContent(throwable.getMessage());
                    }
                });
        addDispose(disposable);
    }

    @Override
    public void loginOut(String ticket) {
        Disposable disposable = mApiFactory.getUserApi().logout(ticket)
                .compose(RxSchedulers.io_main())
                .subscribe(new Consumer<BaseHttpResponse>() {
                    @Override
                    public void accept(BaseHttpResponse baseHttpResponse) throws Exception {
                        if (baseHttpResponse.isSuccess()) {
                            mView.logoutSuccess();
                        } else {
                            mView.showContent(baseHttpResponse.getMessage());
                        }


                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.showContent(throwable.getMessage());
                    }
                });
        addDispose(disposable);
    }

    @Override
    public void takeoutOld() {
        Disposable disposable = mApiFactory.getOperateApi().takeoutOld()
                .compose(RxSchedulers.io_main())
                .subscribe(new Consumer<BaseHttpResponse>() {
                    @Override
                    public void accept(BaseHttpResponse baseHttpResponse) throws Exception {
                        if (baseHttpResponse.isSuccess()) {
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.showContent(throwable.getMessage());
                    }
                });
        addDispose(disposable);
    }

    @Override
    public void speak(String content) {
        mTTSManage.speak(content);
    }

    @Override
    public void closeOld(int contain, String leaseBatteryNumber, int emptyBoxNumber, int exchangeBoxNumber, String exchangeBatteryNumber) {
        Disposable disposable = mApiFactory.getOperateApi().closeOld(contain, leaseBatteryNumber, emptyBoxNumber, exchangeBoxNumber, exchangeBatteryNumber)
                .compose(RxSchedulers.io_main())
                .subscribe(new Consumer<BaseHttpResponse>() {
                    @Override
                    public void accept(BaseHttpResponse baseHttpResponse) throws Exception {
                        if (baseHttpResponse.isSuccess()) {
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.showContent(throwable.getMessage());
                    }
                });
        addDispose(disposable);
    }

    @Override
    public void takeoutNew(String oldOrderId) {
        Disposable disposable = mApiFactory.getOperateApi().takeoutNew(oldOrderId)
                .compose(RxSchedulers.io_main())
                .subscribe(new Consumer<BaseHttpResponse>() {
                    @Override
                    public void accept(BaseHttpResponse baseHttpResponse) throws Exception {
                        if (baseHttpResponse.isSuccess()) {
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.showContent(throwable.getMessage());
                    }
                });
        addDispose(disposable);
    }

    @Override
    public void putInNew(String oldOrderId) {
        Disposable disposable = mApiFactory.getOperateApi().putInNew(oldOrderId)
                .compose(RxSchedulers.io_main())
                .subscribe(new Consumer<BaseHttpResponse>() {
                    @Override
                    public void accept(BaseHttpResponse baseHttpResponse) throws Exception {
                        if (baseHttpResponse.isSuccess()) {
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.showContent(throwable.getMessage());
                    }
                });
        addDispose(disposable);
    }


}
