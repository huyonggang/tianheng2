package com.tianheng.client.model.http;

import com.tianheng.client.model.http.api.BatteryApi;
import com.tianheng.client.model.http.api.OperateApi;
import com.tianheng.client.model.http.api.OtherApi;
import com.tianheng.client.model.http.api.UserApi;

/**
 * Created by huyg on 2017/1/3.
 */

public class ApiFactory {
    private BatteryApi mBatteryApi;
    private OperateApi mOperateApi;
    private OtherApi mOtherApi;
    private UserApi mUserApi;



    public OtherApi getOtherApi() {
        return mOtherApi;
    }

    public OperateApi getOperateApi() {
        return mOperateApi;
    }

    public UserApi getUserApi() {
        return mUserApi;
    }

    public BatteryApi getBatteryApi() {

        return mBatteryApi;
    }




    public ApiFactory() {
        final RetrofitClient retrofitClient = RetrofitClient.getInstance();
        mBatteryApi = retrofitClient.create(BatteryApi.class);
        mOperateApi = retrofitClient.create(OperateApi.class);
        mUserApi = retrofitClient.create(UserApi.class);
        mOtherApi = retrofitClient.create(OtherApi.class);
    }

}
