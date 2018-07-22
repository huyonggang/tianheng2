package com.tianheng.client.model.http.api;

import com.tianheng.client.model.bean.ExchangeBean;
import com.tianheng.client.model.bean.MemberBean;
import com.tianheng.client.model.bean.OrderBean;
import com.tianheng.client.model.bean.SubscribeBean;
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
     *
     * @return
     */
    @POST("cabinet/exchange")
    Observable<HttpResponse<ExchangeBean>> exchange();


    @FormUrlEncoded
    @POST("cabinet/putInOldAndClose")
    Observable<HttpResponse<OrderBean>> closeOld(@Field("leaseBatteryNumber") String leaseBatteryNumber,
                                                 @Field("emptyBoxNumber") int emptyBox);


    /**
     * 关闭新柜门
     *
     * @return
     */
    @FormUrlEncoded
    @POST("cabinet/takeOutNewAndClose")
    Observable<BaseHttpResponse> closeNew(@Field("exchangeBoxNumber") int exchangeBoxNumber,
                                          @Field("exchangeBatteryNumber") String exchangeBatteryNumber);

    @FormUrlEncoded
    @POST("subscribeCode")
    Observable<HttpResponse<SubscribeBean>> subscribeCode(@Field("code") String code);


}
