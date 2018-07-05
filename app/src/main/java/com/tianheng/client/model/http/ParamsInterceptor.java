package com.tianheng.client.model.http;

import android.text.TextUtils;

import com.tianheng.client.App;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by huyg on 2017/8/18.
 */

public class ParamsInterceptor implements Interceptor {

    private Map<String, String> bodyParams = null;

    @Override
    public Response intercept(Chain chain) throws IOException {
        bodyParams = new TreeMap<>();
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        if (!TextUtils.isEmpty(App.getInstance().getTicket())) {
            builder.addHeader("ticket", App.getInstance().getTicket());
        }
        request = builder.build();
        Response response = chain.proceed(request);
        return response;
    }

}
