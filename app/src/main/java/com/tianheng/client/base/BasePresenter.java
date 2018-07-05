package com.tianheng.client.base;

/**
 * Created by huyg on 2017/12/25.
 * Presenter 基类
 */

public interface BasePresenter<T extends BaseView> {

    void attachView(T view);

    void detachView();
}
