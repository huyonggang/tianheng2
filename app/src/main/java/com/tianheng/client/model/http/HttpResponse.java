package com.tianheng.client.model.http;

/**
 * Created by huyg on 2017/1/17.
 */

public class HttpResponse<T> extends BaseHttpResponse{


    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
