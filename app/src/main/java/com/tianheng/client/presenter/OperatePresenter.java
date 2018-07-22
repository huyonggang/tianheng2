package com.tianheng.client.presenter;

import android.text.TextUtils;

import com.tianheng.client.base.RxPresenter;
import com.tianheng.client.manage.TTSManage;
import com.tianheng.client.model.bean.ExchangeBean;
import com.tianheng.client.model.bean.MemberBean;
import com.tianheng.client.model.bean.OrderBean;
import com.tianheng.client.model.bean.SubscribeBean;
import com.tianheng.client.model.bean.UserBean;
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
                .subscribe(exchangeBean -> {
                    if (exchangeBean != null) {
                        mView.openDoor(exchangeBean);
                    }
                }, new RxException<>(e -> {
                    mView.closeDialog();
                    mView.showContent(e.getMessage());
                }));
        addDispose(disposable);
    }


    @Override
    public void closeNew(int exchangeBoxNumber,String exchangeBatteryNumber) {
        Disposable disposable = mApiFactory.getOperateApi().closeNew(exchangeBoxNumber,exchangeBatteryNumber)
                .compose(RxSchedulers.io_main())
                .subscribe(new Consumer<BaseHttpResponse>() {
                    @Override
                    public void accept(BaseHttpResponse baseHttpResponse) throws Exception {
                        if (baseHttpResponse.isSuccess()) {
                            mView.closeNewSuccess();
                        }else{
                            mView.showContent(baseHttpResponse.getMessage());
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.showContent(throwable.getMessage());
                        mView.closeDialog();
                    }
                });
        addDispose(disposable);
    }



    @Override
    public void speak(String content) {
        mTTSManage.speak(content);
    }

    @Override
    public void closeOld( String leaseBatteryNumber, int emptyBoxNumber) {
        Disposable disposable = mApiFactory.getOperateApi().closeOld(leaseBatteryNumber, emptyBoxNumber)
                .compose(RxSchedulers.io_main())
                .subscribe(new Consumer<HttpResponse<OrderBean>>() {
                    @Override
                    public void accept(HttpResponse<OrderBean> response) throws Exception {
                        if (response.isSuccess()) {
                            mView.closeOldSuccess(response.getData());
                        }else{
                            mView.showContent(response.getMessage());
                            mView.closeDialog();
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
    public void getQRCode(String cabinetNumber, int imgWidth, int imgHeight, String imgType) {
        Disposable disposable = mApiFactory.getUserApi().createQRCode("653101421454007",imgWidth,imgHeight,imgType)
                .compose(RxSchedulers.io_main())
                .subscribe(new Consumer<HttpResponse<String>>() {
                    @Override
                    public void accept(HttpResponse<String> response) throws Exception {
                        if (response.isSuccess()){
                            String url=response.getData();
                            if (!TextUtils.isEmpty(url)){
                                mView.showQRImg(url);
                            }
                        }

                    }
                }, new RxException<>(e -> {
                    e.printStackTrace();
                }
                ));
        addDispose(disposable);
    }

    @Override
    public void login(String phone, String code) {
        Disposable disposable = mApiFactory.getUserApi().login(phone,code, "653101421454007")
                .compose(RxSchedulers.io_main())
                .compose(RxResult.handleResult())
                .subscribe(new Consumer<UserBean>() {
                    @Override
                    public void accept(UserBean userBean) throws Exception {
                        if (userBean!=null){
                            mView.loginSuccess(userBean);
                        }

                    }
                },new RxException<>(e -> {
                    e.printStackTrace();
                    mView.showContent("登录失败");
                }
                ));
        addDispose(disposable);
    }

    @Override
    public void getCode(String phone) {
        Disposable disposable = mApiFactory.getUserApi().SendCode(phone)
                .compose(RxSchedulers.io_main())
                .subscribe(new Consumer<BaseHttpResponse>() {
                    @Override
                    public void accept(BaseHttpResponse baseHttpResponse) throws Exception {
                        if (!baseHttpResponse.isSuccess()){
                            mView.showContent("发送验证码失败");
                        }
                    }
                } ,new RxException<>(e -> {
                    e.printStackTrace();
                    mView.showContent("发送验证码失败");
                }
                ));
        addDispose(disposable);
    }

    @Override
    public void subscribeCode(String code) {
        Disposable disposable = mApiFactory.getOperateApi().subscribeCode(code)
                .compose(RxSchedulers.io_main())
                .compose(RxResult.handleResult())
                .subscribe(new Consumer<SubscribeBean>() {
                    @Override
                    public void accept(SubscribeBean subscribeBean) throws Exception {
                        mView.subscribeSuccess(subscribeBean);
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
    public void logout(String ticket) {
        Disposable disposable = mApiFactory.getUserApi().logout(ticket)
                .compose(RxSchedulers.io_main())
                .subscribe(new Consumer<BaseHttpResponse>() {
                    @Override
                    public void accept(BaseHttpResponse response) throws Exception {
                        if (response.isSuccess()){
                            mView.logoutSuccess();
                        }else{
                            mView.showContent("登出失败，请联系客服");
                        }
                    }
                }, new RxException<>(e -> {
                    e.printStackTrace();
                    mView.showContent(e.getMessage());
                }
                ));
        addDispose(disposable);
    }


}
