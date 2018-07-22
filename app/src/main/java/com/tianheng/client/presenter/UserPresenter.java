package com.tianheng.client.presenter;

import android.text.TextUtils;

import com.tianheng.client.App;
import com.tianheng.client.base.BasePresenter;
import com.tianheng.client.base.RxPresenter;
import com.tianheng.client.model.bean.UserBean;
import com.tianheng.client.model.http.ApiFactory;
import com.tianheng.client.model.http.BaseHttpResponse;
import com.tianheng.client.model.http.HttpResponse;
import com.tianheng.client.presenter.contract.UserContract;
import com.tianheng.client.util.rx.RxException;
import com.tianheng.client.util.rx.RxResult;
import com.tianheng.client.util.rx.RxSchedulers;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by huyg on 2018/1/6.
 */

public class UserPresenter extends RxPresenter<UserContract.View> implements UserContract.Presenter{


    private ApiFactory mApiFactory;

    @Inject
    public UserPresenter(ApiFactory apiFactory) {
        mApiFactory = apiFactory;
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
}
