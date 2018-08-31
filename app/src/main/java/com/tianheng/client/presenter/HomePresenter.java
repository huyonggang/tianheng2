package com.tianheng.client.presenter;

import com.tianheng.client.base.RxPresenter;
import com.tianheng.client.model.bean.AdBean;
import com.tianheng.client.model.http.ApiFactory;
import com.tianheng.client.presenter.contract.HomeContract;
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

public class HomePresenter extends RxPresenter<HomeContract.View> implements HomeContract.Presenter {

    private ApiFactory mApiFactory;

    @Inject
    public HomePresenter(ApiFactory apiFactory) {
        this.mApiFactory = apiFactory;
    }

    @Override
    public void getPicture(int imageSize) {
        Disposable disposable = mApiFactory.getOtherApi().getBannerImages()
                .compose(RxSchedulers.io_main())
                .compose(RxResult.handleResult())
                .subscribe(new Consumer<List<AdBean>>() {
                    @Override
                    public void accept(List<AdBean> images) throws Exception {
                        if (images != null && images.size() > 0) {
                            mView.showImage(images);
                        }
                    }
                }, new RxException<>(e -> {
                    e.printStackTrace();
                }
                ));
        addDispose(disposable);
    }
}
