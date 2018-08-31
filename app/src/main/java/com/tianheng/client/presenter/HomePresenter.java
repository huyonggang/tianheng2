package com.tianheng.client.presenter;

import com.tianheng.client.base.RxPresenter;
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
        Disposable disposable = mApiFactory.getOtherApi().getBannerImages(imageSize)
                .compose(RxSchedulers.io_main())
                .compose(RxResult.handleResult())
                .subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(List<String> images) throws Exception {
                        if (images != null && images.size() > 0) {
                            mView.showImage(images);
                        } else {
                            images.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1516431257780&di=e9714fab0e6ababbe83e4b6e1a424596&imgtype=0&src=http%3A%2F%2Fimglf0.ph.126.net%2F1EnYPI5Vzo2fCkyy2GsJKg%3D%3D%2F2829667940890114965.jpg");
                            images.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1516431257779&di=e36c770c42166426aa6aa0e2bcdfc4d7&imgtype=0&src=http%3A%2F%2Fimages.trvl-media.com%2Fhotels%2F4000000%2F3900000%2F3893200%2F3893187%2F3893187_25_y.jpg");
                            images.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1516431257780&di=e9714fab0e6ababbe83e4b6e1a424596&imgtype=0&src=http%3A%2F%2Fimglf0.ph.126.net%2F1EnYPI5Vzo2fCkyy2GsJKg%3D%3D%2F2829667940890114965.jpg");
                            images.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1516431257779&di=e36c770c42166426aa6aa0e2bcdfc4d7&imgtype=0&src=http%3A%2F%2Fimages.trvl-media.com%2Fhotels%2F4000000%2F3900000%2F3893200%2F3893187%2F3893187_25_y.jpg");
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
