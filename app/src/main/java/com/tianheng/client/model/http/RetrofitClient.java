package com.tianheng.client.model.http;


import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.tianheng.client.global.Const;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by huyg on 2017/1/3.
 */

public class RetrofitClient {

    private Retrofit mRetrofit;

    private static final class RetrofitClientHolder {
        private static final RetrofitClient INSTANCE = new RetrofitClient();
    }

    public static RetrofitClient getInstance() {
        return RetrofitClientHolder.INSTANCE;
    }

    private RetrofitClient() {
        createRetrofit();
    }

    private void createRetrofit() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new LoggingInterceptor());
        builder.addInterceptor(new ParamsInterceptor());
        builder.addNetworkInterceptor(new StethoInterceptor());
        //设置超时
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(20, TimeUnit.SECONDS);
        builder.writeTimeout(20, TimeUnit.SECONDS);
        //错误重连
        builder.retryOnConnectionFailure(true);

        mRetrofit = new Retrofit.Builder()
                .baseUrl(Const.BASE_URL)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public <T> T create(Class<?> clazz) {
        return (T) mRetrofit.create(clazz);
    }
}
