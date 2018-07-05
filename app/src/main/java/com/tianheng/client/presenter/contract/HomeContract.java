package com.tianheng.client.presenter.contract;

import com.tianheng.client.base.BasePresenter;
import com.tianheng.client.base.BaseView;

import java.util.List;

/**
 * Created by huyg on 2017/12/26.
 */

public interface HomeContract {


    interface View extends BaseView {

        void showImage(List<String> images);
    }

    interface Presenter extends BasePresenter<View>{
        void getPicture(int imageSize);
    }
}
