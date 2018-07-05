package com.tianheng.client.model.http.api;

import com.tianheng.client.model.bean.UserBean;
import com.tianheng.client.model.http.BaseHttpResponse;
import com.tianheng.client.model.http.HttpResponse;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by huyg on 2017/12/25.
 */

public interface UserApi {


    @FormUrlEncoded
    @POST("cabinet/sendSmsCode")
    Observable<BaseHttpResponse> SendCode(@Field("mobile") String mobile);


    @FormUrlEncoded
    @POST("cabinet/login")
    Observable<HttpResponse<UserBean>> login(@Field("mobile") String mobile, @Field("code") String code, @Field("cabinetNumber") String cabinetNumber);


    @FormUrlEncoded
    @POST("cabinet/createQRcode")
    Observable<HttpResponse<String>> createQRCode(@Field("cabinetNumber") String cabinetNumber, @Field("imgWidth") int imgWidth, @Field("imgHeight") int imgHeight, @Field("imgType") String imgType);

    @FormUrlEncoded
    @POST("cabinet/logout")
    Observable<BaseHttpResponse> logout(@Field("ticket") String ticket);



}
