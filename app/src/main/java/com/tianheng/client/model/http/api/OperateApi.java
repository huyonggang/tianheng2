package com.tianheng.client.model.http.api;

import com.tianheng.client.model.bean.ExchangeBean;
import com.tianheng.client.model.bean.MemberBean;
import com.tianheng.client.model.bean.OrderBean;
import com.tianheng.client.model.http.BaseHttpResponse;
import com.tianheng.client.model.http.HttpResponse;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by huyg on 2017/12/25.
 * 操作api
 */

public interface OperateApi {

    /**
     * 点击换按钮
     * @return
     */
    @POST("cabinet/exchange")
    Observable<HttpResponse<ExchangeBean>> exchange();

    /**
     * 放入旧电池
     * @return
     */
    @POST("cabinet/putInOld")
    Observable<BaseHttpResponse> putInOld();

    /**
     * 放入旧电池
     * @return
     */
    @POST("cabinet/takeoutOld")
    Observable<BaseHttpResponse> takeoutOld();

    /**
     * 关闭旧柜门
     * @param emptyBox
     * @return
     */
    @FormUrlEncoded
    @POST("cabinet/closeOld")
    Observable<HttpResponse<OrderBean>> closeOld(@Field("contain") int contain, @Field("leaseBatteryNumber") String leaseBatteryNumber, @Field("emptyBoxNumber") int emptyBox, @Field("exchangeBoxNumber") int exchangeBoxNumber, @Field("exchangeBatteryNumber") String exchangeBatteryNumber);


    /**
     * 取出新电池
     * @return
     */
    @FormUrlEncoded
    @POST("cabinet/takeoutNew")
    Observable<BaseHttpResponse> takeoutNew(@Field("oldOrderId") String oldOrderId);


    /**
     * 放入新电池(违规操作)
     * @return
     */
    @FormUrlEncoded
    @POST("cabinet/putInNew")
    Observable<BaseHttpResponse> putInNew(@Field("oldOrderId") String oldOrderId);


    /**
     * 关闭新柜门
     * @return
     */
    @FormUrlEncoded
    @POST("cabinet/closeNew")
    Observable<BaseHttpResponse> closeNew(@Field("oldOrderId") String oldOrderId);


    /**
     * 获取用户信息
     * @return
     */
    @POST("cabinet/memberDetail")
    Observable<HttpResponse<MemberBean>> getMemberDetail();




}
