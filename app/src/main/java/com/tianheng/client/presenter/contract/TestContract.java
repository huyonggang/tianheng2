package com.tianheng.client.presenter.contract;

import com.tianheng.client.base.BasePresenter;
import com.tianheng.client.base.BaseView;

/**
 * Created by huyg on 2018/1/29.
 */

public interface TestContract {

    interface View extends BaseView {

    }

    interface Presenter extends BasePresenter<View>{

    }
}
