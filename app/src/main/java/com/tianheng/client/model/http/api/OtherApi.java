package com.tianheng.client.model.http.api;
import com.tianheng.client.model.bean.AdBean;
import com.tianheng.client.model.http.HttpResponse;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by huyg on 2017/12/26.
 */

public interface OtherApi {


    @GET("ad/list")
    Observable<HttpResponse<List<AdBean>>> getBannerImages();



}
